package com.lastmile.accountservice.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.lastmile.accountservice.client.AuthServiceFeignClient;
import com.lastmile.accountservice.client.AuthyApiValidator;
import com.lastmile.accountservice.client.DriverServiceFeignClient;
import com.lastmile.accountservice.client.OAuthTokenClient;
import com.lastmile.accountservice.client.addresses.AddressBridge;
import com.lastmile.accountservice.client.payments.PaymentBridge;
import com.lastmile.accountservice.domain.Account;
import com.lastmile.accountservice.domain.AccountDevice;
import com.lastmile.accountservice.dto.CreateAccountDto;
import com.lastmile.accountservice.dto.CreateAccountResponseDto;
import com.lastmile.accountservice.dto.CreatePushNotificationRequestDto;
import com.lastmile.accountservice.dto.GetAccountDeviceRequestDto;
import com.lastmile.accountservice.dto.GetAccountDto;
import com.lastmile.accountservice.dto.PatchAccountRoleRequestDto;
import com.lastmile.accountservice.dto.ResendActivationCodeRequestDto;
import com.lastmile.accountservice.dto.ActivationCodeDto;
import com.lastmile.accountservice.dto.ReturnUserLoginDto;
import com.lastmile.accountservice.dto.UpdateAccountRequestDto;
import com.lastmile.accountservice.dto.UserLoginDto;
import com.lastmile.accountservice.dto.UserRecoverPasswordRequestDto;
import com.lastmile.accountservice.dto.UserUpdatePasswordRequestDto;
import com.lastmile.accountservice.dto.UserUpdateRequestDto;
import com.lastmile.accountservice.dto.payments.GetPaymentDetailResponseDto;
import com.lastmile.accountservice.enums.AccountType;
import com.lastmile.accountservice.enums.Authorities;
import com.lastmile.accountservice.client.rabbitmq.EventPublisher;
import com.lastmile.accountservice.dto.AuthUserRegistrationDto;
import com.lastmile.accountservice.repository.AccountDeviceRepository;
import com.lastmile.accountservice.repository.AccountPropertiesRepository;
import com.lastmile.accountservice.repository.AccountRepository;
import com.lastmile.accountservice.service.AccountService;
import com.lastmile.accountservice.service.exception.UserAlreadyActiveException;
import com.lastmile.accountservice.service.exception.UserAlreadyExistsException;
import com.lastmile.accountservice.service.exception.UserNotActiveException;
import com.lastmile.accountservice.service.exception.UserNotFoundException;
import com.lastmile.accountservice.service.exception.DeviceNotFoundException;
import com.lastmile.accountservice.service.exception.InvalidActivationCodeException;
import com.lastmile.accountservice.service.exception.InvalidLoginException;
import com.lastmile.utils.clients.aws.AWSS3Client;
import com.lastmile.utils.exceptions.AddressNotFoundException;
import com.lastmile.utils.exceptions.DocumentNotFoundException;
import com.lastmile.utils.exceptions.ExternalServerException;
import com.lastmile.utils.exceptions.FeignCommunicationException;
import com.lastmile.utils.exceptions.GenericException;
import com.lastmile.utils.exceptions.MissingRequiredFieldException;
import com.lastmile.utils.exceptions.PaymentDetailNotFoundException;
import com.lastmile.utils.exceptions.RabbitException;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.enums.EntityType;
import com.lastmile.utils.enums.notifications.PushNotificationsExternalEntities;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Configuration
public class AccountServiceImpl implements AccountService {

    private static final String BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY = "bypass_external_validation";
    private static final String PROFILE_PICTURES_S3_PATH = "profile-pictures/";

    @Value("${spring.profiles.active}")
    private String environment;
    @Value("${rabbitmq.routing-key.account-recovery-email}")
    private String passwordRecoveryEmailEvent;
    @Value("${rabbitmq.routing-key.account-recovery-sms}")
    private String passwordRecoverySmsEvent;
    @Value("${rabbitmq.routing-key.account-activation-email}")
    private String accountActivationEmailEvent;
    @Value("${rabbitmq.routing-key.account-activation-sms}")
    private String accountActivationSmsEvent;
    @Value("${rabbitmq.sms.account-activation-template}")
    private String accountActivationTemplate;
    @Value("${rabbitmq.sms.code-length}")
    private String codeLength;
    @Value("${rabbitmq.sms.code-length-key}")
    private String codeLengthKey;

    private final DriverServiceFeignClient driverServiceFeignClient;
    private final OAuthTokenClient oAuthTokenClient;
    private final AuthServiceFeignClient authServiceFeignClient;
    private final AccountRepository accountRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final AccountPropertiesRepository accountPropertiesRepository;
    private final EventPublisher eventPublisher;
    private final AuthyApiValidator authyApiValidator;
    private final AddressBridge addressBridge;
    private final PaymentBridge paymentBridge;
    private final AWSS3Client awsS3Client;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public AccountServiceImpl(final AuthServiceFeignClient authServiceFeignClient,
                              final DriverServiceFeignClient driverServiceFeignClient,
                              final AccountRepository accountRepository,
                              final AccountDeviceRepository accountDeviceRepository,
                              final OAuthTokenClient oAuthTokenClient,
                              final EventPublisher eventPublisher,
                              final AccountPropertiesRepository accountPropertiesRepository,
                              final AuthyApiValidator authyApiValidator,
                              final AddressBridge addressBridge,
                              final PaymentBridge paymentBridge,
                              final AWSS3Client awsS3Client) {
        this.authServiceFeignClient = authServiceFeignClient;
        this.driverServiceFeignClient = driverServiceFeignClient;
        this.accountRepository = accountRepository;
        this.accountDeviceRepository = accountDeviceRepository;
        this.oAuthTokenClient = oAuthTokenClient;
        this.eventPublisher = eventPublisher;
        this.accountPropertiesRepository = accountPropertiesRepository;
        this.authyApiValidator = authyApiValidator;
        this.addressBridge = addressBridge;
        this.paymentBridge = paymentBridge;
        this.awsS3Client = awsS3Client;
    }

    @Override
    @Transactional(rollbackFor = { FeignCommunicationException.class, GenericException.class, UserAlreadyExistsException.class, MissingRequiredFieldException.class, RabbitException.class })
    public CreateAccountResponseDto createAccount(final CreateAccountDto accountDto, ServiceContext serviceContext) throws FeignCommunicationException, GenericException, UserAlreadyExistsException, MissingRequiredFieldException, RabbitException {

        ModelMapper modelMapper = new ModelMapper();
        Random random = new Random();
        final AuthUserRegistrationDto user = new AuthUserRegistrationDto();

        // map from DTO
        Account newAccount = modelMapper.map(accountDto, Account.class);

        // get bypass property value
        boolean skipExternalValidation = false;
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                skipExternalValidation = true;
        } else {
                skipExternalValidation = false;
        }

        // validations based on account type
        switch (accountDto.getAccountType()) {
            case WEB:
                // ensure that email exists
                if (null == accountDto.getEmail() || accountDto.getEmail().isEmpty()) {
                    throw new MissingRequiredFieldException("email");
                }
                // ensure that email is unique
                if (accountRepository.findByUsername(accountDto.getEmail()).isPresent()) {
                    throw new UserAlreadyExistsException(accountDto.getEmail());
                }
                // set username
                newAccount.setUsername(accountDto.getEmail());
                // TODO - generate random activation token and send email
                String randomToken = String.format("%04d", random.nextInt(10000));
                newAccount.setActivationCode(randomToken);
                newAccount.setActivationCode("0000");
                break;
            case MOBILE:
                // ensure that phone number exists
                if (null == accountDto.getPhoneNumber() || accountDto.getPhoneNumber().isEmpty()) {
                    throw new MissingRequiredFieldException("phone number");
                }
                // ensure that phone number is unique
                if (accountRepository.findByUsername(accountDto.getPhoneNumber()).isPresent()) {
                    throw new UserAlreadyExistsException(accountDto.getPhoneNumber());
                }
                // set username
                newAccount.setUsername(accountDto.getPhoneNumber());
                if (skipExternalValidation) {
                    // generate hard coded activation code and skip SMS activation
                    String randomCode = String.format("%04d", random.nextInt(10000));
                    newAccount.setActivationCode(randomCode);
                    newAccount.setActivationCode("0000");
                } else {
                    // send activation message to rabbitmq
                    try {
                        this.activationCommunicationSender(serviceContext, AccountType.MOBILE, newAccount);
                    } catch (final Exception e) {
                        throw new RabbitException(e.getMessage(), e.getCause());
                    }
                }
                break;
        }

        try {
            // generate random account identifier
            newAccount.setUserIdentification(UUID.randomUUID().toString());
            // set default role as ROLE_USER if not defined
            if (null == newAccount.getRole() || newAccount.getRole().isEmpty()) {
                newAccount.setRole(Authorities.ROLE_USER.toString());
            }
            // set account as inactive
            newAccount.setActive(false);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // try to create address
        if (accountDto.getAddress() != null && !accountDto.getAddress().getAddressLine1().isEmpty()) {
            String addressId;
            try {
                addressId = addressBridge.createAddress(newAccount.getUserIdentification(),
                                                        EntityType.ACCOUNT,
                                                        accountDto.getAddress(),
                                                        serviceContext);
            } catch (Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }
            newAccount.setActiveAddressId(addressId);
        }

        // try to create billing address
        if (accountDto.getBillingAddress() != null && !accountDto.getBillingAddress().getAddressLine1().isEmpty()) {
            String billingAddressId;
            try {
                billingAddressId = addressBridge.createAddress(newAccount.getUserIdentification(),
                                                               EntityType.ACCOUNT,
                                                               accountDto.getBillingAddress(),
                                                               serviceContext);
            } catch (Exception ex) {
                throw new FeignCommunicationException(ex.getMessage());
            }
            newAccount.setActiveBillingAddressId(billingAddressId);
        }

        // save account
        accountRepository.save(newAccount);

        user.setUserIdentification(newAccount.getUserIdentification());
        user.setUsername(newAccount.getUsername());
        user.setPassword(accountDto.getPassword());
        user.setRole(Authorities.valueOf(newAccount.getRole()));

        // create user in authorization service
        authServiceFeignClient.createUser(user);

        return new CreateAccountResponseDto(newAccount.getUserIdentification());

    }

    @Override
    @Transactional(rollbackFor = { GenericException.class, UserNotFoundException.class, UserNotActiveException.class })
    public ReturnUserLoginDto login(final UserLoginDto user)
            throws GenericException, UserNotFoundException, UserNotActiveException, InvalidLoginException {

        ReturnUserLoginDto returnUser;
        Optional<Account> account = accountRepository.findByUsername(user.getUsername());

        // validate that the account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(user.getUsername());
        }

        // validate that the account is active
        if (!account.get().getActive()) {
            throw new UserNotActiveException(user.getUsername());
        }

        // try to login token from auth service
        try {
            returnUser = oAuthTokenClient.login(user);
        } catch (final Exception e) {
            throw new InvalidLoginException(user.getUsername(), e.getCause());
        }

        // set account device id
        if (null != user.getAccountDeviceRequestDto() 
                && null != user.getAccountDeviceRequestDto().getExternalEntityToken()
                && !user.getAccountDeviceRequestDto().getExternalEntityToken().isEmpty()
                && null != user.getAccountDeviceRequestDto().getExternalEntity()
                && !user.getAccountDeviceRequestDto().getExternalEntity().toString().isEmpty()) {
            Optional<AccountDevice> accountDevice = accountDeviceRepository.findByUserIdentification(account.get().getUserIdentification());
            if (accountDevice.isPresent()) {
                AccountDevice updatedAccountDevice = accountDevice.get();
                updatedAccountDevice.setExternalEntity(user.getAccountDeviceRequestDto().getExternalEntity());
                updatedAccountDevice.setExternalEntityToken(user.getAccountDeviceRequestDto().getExternalEntityToken());
                accountDeviceRepository.save(updatedAccountDevice);
            } else {
                AccountDevice newAccountDevice = new AccountDevice(account.get().getUserIdentification(),
                                                                   user.getAccountDeviceRequestDto().getExternalEntity(),
                                                                   user.getAccountDeviceRequestDto().getExternalEntityToken());
                accountDeviceRepository.save(newAccountDevice);
            }
        }

        try {
            returnUser.setUserIdentification(account.get().getUserIdentification());
            returnUser.setRole(account.get().getRole());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return returnUser;
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class, UserNotFoundException.class, UserNotActiveException.class })
    public void recoverPassword(UserRecoverPasswordRequestDto recoverPasswordRequestDto, ServiceContext serviceContext) throws GenericException, UserNotFoundException, UserNotActiveException {

        Optional<Account> account = accountRepository.findByUsername(recoverPasswordRequestDto.getUsername());

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(recoverPasswordRequestDto.getUsername());
        }

        // validate that the account is active
        if (!account.get().getActive()) {
            throw new UserNotActiveException(recoverPasswordRequestDto.getUsername());
        }

        // get bypass property value
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                return;
        }

        // get account type
        AccountType accountType = AccountType.valueOf(account.get().getAccountType()); 

        // send new activation code
        switch (accountType) {
            case MOBILE:
                // send activation message to rabbitmq
                try {
                    this.activationCommunicationSender(serviceContext, AccountType.MOBILE, account.get());
                } catch (final Exception e) {
                    throw new GenericException(e.getMessage(), e.getCause());
                }
                break;
            // TODO - send email with new activation code
            case WEB:
                break;
        }

    }

    @Override
    @Transactional(rollbackFor = { GenericException.class, UserNotFoundException.class, UserNotActiveException.class, InvalidActivationCodeException.class })
    public void updatePassword(UserUpdatePasswordRequestDto updatePasswordRequestDto, ServiceContext serviceContext) throws GenericException, UserNotFoundException, UserNotActiveException, InvalidActivationCodeException {

        Optional<Account> account = accountRepository.findByUsername(updatePasswordRequestDto.getUsername());

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(updatePasswordRequestDto.getUsername());
        }

        // validate that the account is active
        if (!account.get().getActive()) {
            throw new UserNotActiveException(updatePasswordRequestDto.getUsername());
        }

        // get bypass property value
        boolean skipExternalValidation = false;
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                skipExternalValidation = true;
        } else {
                skipExternalValidation = false;
        }

        // get account type
        AccountType accountType = AccountType.valueOf(account.get().getAccountType()); 

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setPassword(updatePasswordRequestDto.getNewPassword());

        // internal validation
        if (skipExternalValidation) {
            // validate activation code
            if (!(account.get().getActivationCode().equals(updatePasswordRequestDto.getActivationCode()))) {
                throw new InvalidActivationCodeException(updatePasswordRequestDto.getActivationCode());
            }
            try {
                // update password in auth service
                authServiceFeignClient.updateUser(updatePasswordRequestDto.getUsername(), userUpdateRequestDto);
            } catch (final Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
        
        // validate in external service
        } else {
            switch (accountType) {
                case MOBILE:
                    // parse phone number
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    PhoneNumber phone;
                    try {
                        phone = phoneUtil.parse(updatePasswordRequestDto.getUsername(), "");
                    } catch (NumberParseException e) {
                        throw new GenericException("Error parsing phone number: " + updatePasswordRequestDto.getUsername(), e.getCause());
                    }
                    String countryCode = String.valueOf(phone.getCountryCode());
                    String number = String.valueOf(phone.getNationalNumber());
                    // verify token in authy external service
                    try {
                        if (authyApiValidator.verifyToken(countryCode, number, updatePasswordRequestDto.getActivationCode())) {
                            // update password in auth service
                            authServiceFeignClient.updateUser(updatePasswordRequestDto.getUsername(), userUpdateRequestDto);
                        } else {
                            throw new Exception("Error verifying token in external service");
                        }
                    } catch (final Exception e) {
                        throw new InvalidActivationCodeException(updatePasswordRequestDto.getActivationCode(), e.getCause());
                    }
                    break;
                case WEB:
                    // TODO - validate activation code external for WEB
                    if (!(account.get().getActivationCode().equals(updatePasswordRequestDto.getActivationCode()))) {
                        throw new InvalidActivationCodeException(updatePasswordRequestDto.getActivationCode());
                    }
                    try {
                        // update password in auth service
                        authServiceFeignClient.updateUser(updatePasswordRequestDto.getUsername(), userUpdateRequestDto);
                    } catch (final Exception e) {
                        throw new GenericException(e.getMessage(), e.getCause());
                    }
                    break;
            }
        }

    }

    @Override
    public List<GetAccountDto> getAccounts(Optional<Integer> limit,
                                           Optional<Integer> offset,
                                           Optional<String> role,
                                           Optional<String> firstName,
                                           Optional<String> lastName,
                                           Optional<String> phoneNumber,
                                           Optional<String> email,
                                           Optional<String> accountType) throws GenericException {

        ModelMapper modelMapper = new ModelMapper();
        List<Account> accounts;

        Pageable pageable = PageRequest.of(offset.orElse(Constants.DEFAULT_VALUE_OFFSET), limit.orElse(Constants.DEFAULT_VALUE_LIMIT));

        try {
            // try to fetch accounts
            accounts = accountRepository.findAllAccounts(role.orElse(""),
                                                         firstName.orElse(""),
                                                         lastName.orElse(""),
                                                         phoneNumber.orElse(""),
                                                         email.orElse(""),
                                                         accountType.orElse(""),
                                                         pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return accounts.stream().map(account -> modelMapper.map(account, GetAccountDto.class)).collect(Collectors.toList());

    }

    @Override
    public GetAccountDto getAccount(String userIdentification) throws UserNotFoundException, GenericException {

        ModelMapper modelMapper = new ModelMapper();
        Optional<Account> account = accountRepository.findByUserIdentification(userIdentification);

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        GetAccountDto getAccountDto = new GetAccountDto();
        try {
            // try to fetch account
            account = accountRepository.findByUserIdentification(userIdentification);
            getAccountDto = modelMapper.map(account.get(), GetAccountDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        // if user has photo, include in dto
        if (null != account.get().getProfilePicture() && !account.get().getProfilePicture().isEmpty()) {
            // get documents from AWS - document back
            String documentPath = PROFILE_PICTURES_S3_PATH + userIdentification + "/";
            getAccountDto.setProfilePicture(awsS3Client.downloadFile(documentPath + account.get().getProfilePicture()));
        }

        return getAccountDto;
    }

    @Override
    public GetAccountDeviceRequestDto getAccountDevice(String userIdentification) throws UserNotFoundException, GenericException {

        Optional<AccountDevice> accountDevice = accountDeviceRepository.findByUserIdentification(userIdentification);

        // validate if account exists
        if (!accountDevice.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        ModelMapper modelMapper = new ModelMapper();
        GetAccountDeviceRequestDto getAccountDeviceDto = new GetAccountDeviceRequestDto();
        try {
            getAccountDeviceDto = modelMapper.map(accountDevice.get(), GetAccountDeviceRequestDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return getAccountDeviceDto;
    }

    @Override
    @Transactional(rollbackFor = { UserNotFoundException.class, GenericException.class })
    public void deleteAccount(String userIdentification) throws UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUserIdentification(userIdentification);

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        try {

            accountRepository.deleteById(account.get().getId());
            authServiceFeignClient.deleteUser(account.get().getUsername());

            // delete driver information
            if (account.get().getRole().equals(Authorities.ROLE_DRIVER.toString())) {
                driverServiceFeignClient.deleteDriver(account.get().getUsername());
            }

        } catch (final Exception e) {

            throw new GenericException("AccountServiceImpl - deleteAccount", e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = { UserNotFoundException.class,GenericException.class, AddressNotFoundException.class, PaymentDetailNotFoundException.class })
    public void updateAccount(String userIdentification, UpdateAccountRequestDto updateAccountRequestDto, ServiceContext serviceContext) throws PaymentDetailNotFoundException, AddressNotFoundException, UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUserIdentification(userIdentification);
        Random random = new Random();

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        // get bypass property value
        boolean skipExternalValidation = false;
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                skipExternalValidation = true;
        } else {
                skipExternalValidation = false;
        }

        try {

            Account updatedAccount = account.get();
            UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();

            if (updateAccountRequestDto.getEmail() != null 
                    && !updateAccountRequestDto.getEmail().isEmpty()
                    && !updateAccountRequestDto.getEmail().equals(updatedAccount.getEmail())) {
                updatedAccount.setEmail(updateAccountRequestDto.getEmail());

                // if type of account is WEB - inactivate account and send new activation email
                // (TODO)
                if (updatedAccount.getAccountType().equals(AccountType.WEB.toString())) {

                    // set new username
                    updatedAccount.setUsername(updateAccountRequestDto.getEmail());
                    updatedAccount.setActive(false);

                    // set new username to be updated in auth service
                    userUpdateRequestDto.setUsername(updateAccountRequestDto.getEmail());

                }

            }

            if (updateAccountRequestDto.getPhoneNumber() != null
                    && !updateAccountRequestDto.getPhoneNumber().isEmpty()
                    && !updateAccountRequestDto.getPhoneNumber().equals(updatedAccount.getPhoneNumber())) {

                updatedAccount.setPhoneNumber(updateAccountRequestDto.getPhoneNumber());
                // if type of account is MOBILE - inactivate account and send new activation SMS to new number
                if (updatedAccount.getAccountType().equals(AccountType.MOBILE.toString())) {
                    // set new username
                    updatedAccount.setUsername(updateAccountRequestDto.getPhoneNumber());
                    updatedAccount.setActive(false);
                    // set new username to be updated in auth service
                    userUpdateRequestDto.setUsername(updateAccountRequestDto.getPhoneNumber());
                    if (skipExternalValidation) {
                        // generate hard coded activation code and skip SMS activation
                        String randomCode = String.format("%04d", random.nextInt(10000));
                        updatedAccount.setActivationCode(randomCode);
                        updatedAccount.setActivationCode("0000");
                    } else {
                        // send activation message to rabbitmq
                        try {
                            this.activationCommunicationSender(serviceContext, AccountType.MOBILE, updatedAccount);
                        } catch (final Exception e) {
                            throw new RabbitException(e.getMessage(), e.getCause());
                        }
                    }
                }
            }

            // first name
            if (updateAccountRequestDto.getFirstName() != null && !updateAccountRequestDto.getFirstName().isEmpty()) {
                updatedAccount.setFirstName(updateAccountRequestDto.getFirstName());
            }

            // last name
            if (updateAccountRequestDto.getLastName() != null && !updateAccountRequestDto.getLastName().isEmpty()) {
                updatedAccount.setLastName(updateAccountRequestDto.getLastName());
            }

            // active address id
            if (updateAccountRequestDto.getActiveAddressId() != null && !updateAccountRequestDto.getActiveAddressId().isEmpty()) {
                // validate that address exists
                if (!addressBridge.getAddress(updateAccountRequestDto.getActiveAddressId(), serviceContext).isPresent()) {
                    throw new AddressNotFoundException(updateAccountRequestDto.getActiveAddressId());
                }
                updatedAccount.setActiveAddressId(updateAccountRequestDto.getActiveAddressId());
            }

            // active billing address id
            if (updateAccountRequestDto.getActiveBillingAddressId() != null && !updateAccountRequestDto.getActiveBillingAddressId().isEmpty()) {
                // validate that address exists
                if (!addressBridge.getAddress(updateAccountRequestDto.getActiveBillingAddressId(), serviceContext).isPresent()) {
                    throw new AddressNotFoundException(updateAccountRequestDto.getActiveBillingAddressId());
                }
                updatedAccount.setActiveBillingAddressId(updateAccountRequestDto.getActiveBillingAddressId());
            }

            // active payment details id
            if (updateAccountRequestDto.getActivePaymentDetailsId() != null && !updateAccountRequestDto.getActivePaymentDetailsId().isEmpty()) {
                Optional<GetPaymentDetailResponseDto> paymentDetail;
                // try to fetch payment details
                try {
                    paymentDetail = paymentBridge.getPaymentDetail(userIdentification, updateAccountRequestDto.getActivePaymentDetailsId(), serviceContext);
                } catch (Exception ex) {
                    throw new FeignCommunicationException(ex.getMessage());
                }
                // validate that payment details exists
                if (!paymentDetail.isPresent()) {
                    throw new PaymentDetailNotFoundException(updateAccountRequestDto.getActivePaymentDetailsId());
                }
                updatedAccount.setActivePaymentDetailsId(updateAccountRequestDto.getActivePaymentDetailsId());
            }

            // update account
            accountRepository.save(updatedAccount);

            // update in auth service
            authServiceFeignClient.updateUser(updatedAccount.getUsername(), userUpdateRequestDto);

        } catch (final Exception e) {
            throw new GenericException("Error updating account", e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { InvalidActivationCodeException.class, UserAlreadyActiveException.class, UserNotFoundException.class,
            GenericException.class })
    public void activateAccount(ActivationCodeDto activationCodeDto) throws InvalidActivationCodeException, UserAlreadyActiveException, UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUsername(activationCodeDto.getUsername());

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(activationCodeDto.getUsername());
        }

        // validate if account is not active
        if (account.get().getActive()) {
            throw new UserAlreadyActiveException(activationCodeDto.getUsername());
        }

        // get bypass property value
        boolean skipExternalValidation = false;
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                skipExternalValidation = true;
        } else {
                skipExternalValidation = false;
        }

        Account updatedAccount = account.get();

        // get account type
        AccountType accountType = AccountType.valueOf(account.get().getAccountType()); 

        // internal validation
        if (skipExternalValidation) {
            // validate activation code
            if (!(account.get().getActivationCode().equals(activationCodeDto.getActivationCode()))) {
                throw new InvalidActivationCodeException(activationCodeDto.getActivationCode());
            }
            try {
                updatedAccount.setActive(true);
                accountRepository.save(updatedAccount);
            } catch (final Exception e) {
                throw new GenericException(e.getMessage(), e.getCause());
            }
        
        // validate in external service
        } else {
            switch (accountType) {
                case MOBILE:
                    // parse phone number
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    PhoneNumber phone;
                    try {
                        phone = phoneUtil.parse(activationCodeDto.getUsername(), "");
                    } catch (NumberParseException e) {
                        throw new GenericException("Error parsing phone number: " + activationCodeDto.getUsername(), e.getCause());
                    }
                    String countryCode = String.valueOf(phone.getCountryCode());
                    String number = String.valueOf(phone.getNationalNumber());
                    // verify token in authy external service
                    try {
                        if (authyApiValidator.verifyToken(countryCode, number, activationCodeDto.getActivationCode())) {
                            updatedAccount.setActive(true);
                            accountRepository.save(updatedAccount);
                        } else {
                            throw new Exception("Error verifying token in external service");
                        }
                    } catch (final Exception e) {
                        throw new InvalidActivationCodeException(activationCodeDto.getActivationCode(), e.getCause());
                    }
                    break;
                case WEB:
                    // TODO - validate activation code external for WEB
                    if (!(account.get().getActivationCode().equals(activationCodeDto.getActivationCode()))) {
                        throw new InvalidActivationCodeException(activationCodeDto.getActivationCode());
                    }
                    try {
                        updatedAccount.setActive(true);
                        accountRepository.save(updatedAccount);
                    } catch (final Exception e) {
                        throw new GenericException(e.getMessage(), e.getCause());
                    }
                    break;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = { UserAlreadyActiveException.class, UserNotFoundException.class, GenericException.class, RabbitException.class })
    public void resendActivationCode(ResendActivationCodeRequestDto resendActivationCodeRequestDto, ServiceContext serviceContext) throws RabbitException, UserAlreadyActiveException, UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUsername(resendActivationCodeRequestDto.getUsername());

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(resendActivationCodeRequestDto.getUsername());
        }

        // validate if account is not active
        if (account.get().getActive()) {
            throw new UserAlreadyActiveException(resendActivationCodeRequestDto.getUsername());
        }

        // get bypass property value
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                return;
        }

        // get account type
        AccountType accountType = AccountType.valueOf(account.get().getAccountType()); 

        // send new activation code
        switch (accountType) {
            case MOBILE:
                // send activation message to rabbitmq
                try {
                    this.activationCommunicationSender(serviceContext, AccountType.MOBILE, account.get());
                } catch (final Exception e) {
                    throw new RabbitException(e.getMessage(), e.getCause());
                }
                break;
            // TODO - send email with new activation code
            case WEB:
                break;
        }
    }

    @Override
    @Transactional(rollbackFor = { UserAlreadyActiveException.class, UserNotFoundException.class, GenericException.class })
    public void resendPasswordActivationCode(ResendActivationCodeRequestDto resendActivationCodeRequestDto, ServiceContext serviceContext) throws UserNotActiveException, UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUsername(resendActivationCodeRequestDto.getUsername());

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(resendActivationCodeRequestDto.getUsername());
        }

        // validate if account is active
        if (!account.get().getActive()) {
            throw new UserNotActiveException(resendActivationCodeRequestDto.getUsername());
        }

        // get bypass property value
        if (accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).isPresent()
            && accountPropertiesRepository.findByEnvironmentAndProperty(environment, BYPASS_EXTERNAL_ACCOUNT_VALIDATION_PROPERTY).get().getValue().equals(Constants.YES_VALUE)) {
                return;
        }

        // get account type
        AccountType accountType = AccountType.valueOf(account.get().getAccountType()); 

        // send new activation code
        switch (accountType) {
            case MOBILE:
                // send activation message to rabbitmq
                try {
                    this.activationCommunicationSender(serviceContext, AccountType.MOBILE, account.get());
                } catch (final Exception e) {
                    throw new GenericException(e.getMessage(), e.getCause());
                }
                break;
            // TODO - send email with new activation code
            case WEB:
                break;
        }
    }

    @Override
    @Transactional(rollbackFor = { UserNotFoundException.class, GenericException.class })
    public void updateAccountRole(PatchAccountRoleRequestDto accountRoleRequestDto, String userIdentification)
            throws UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUserIdentification(userIdentification);
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();

        // validate if account exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        try {

            Account updatedAccount = account.get();

            if (!updatedAccount.getRole().equals(Authorities.ROLE_ADMIN.toString())) {
                updatedAccount.setRole(accountRoleRequestDto.getRole());
                accountRepository.save(updatedAccount);

                // update in auth service
                userUpdateRequestDto.setRole(Authorities.valueOf(accountRoleRequestDto.getRole()));
                authServiceFeignClient.updateUser(updatedAccount.getUsername(), userUpdateRequestDto);
            }

        } catch (final Exception e) {

            throw new GenericException("Error updating account role", e.getCause());

        }

    }

    @Override
    @Transactional(rollbackFor = {ExternalServerException.class, UserNotFoundException.class, GenericException.class})
    public void upsertProfilePicture(String userIdentification, MultipartFile profilePicture) throws ExternalServerException, UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUserIdentification(userIdentification);

        // validate that user exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        String documentPath = PROFILE_PICTURES_S3_PATH + userIdentification + "/";
        Account newAccount = account.get();

        // delete from aws profile picture
        if (null != newAccount.getProfilePicture() && !newAccount.getProfilePicture().isEmpty()) {
            if (!awsS3Client.deleteFile(documentPath + newAccount.getProfilePicture())) {
                throw new ExternalServerException("Error deleting file from AWS S3");
            }
        }

        // upload new profile picture to aws
        String profilePictureId = UUID.randomUUID().toString().replace("-", "") + "." + FilenameUtils.getExtension(profilePicture.getOriginalFilename());

        // upload to AWS
        if (!awsS3Client.uploadFile(profilePicture, documentPath + profilePictureId)){
            throw new ExternalServerException("Error uploading file to AWS S3");
        }

        newAccount.setProfilePicture(profilePictureId);

        try {
            accountRepository.save(newAccount);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = {DocumentNotFoundException.class, ExternalServerException.class, UserNotFoundException.class, GenericException.class})
    public void deleteProfilePicture(String userIdentification) throws DocumentNotFoundException, ExternalServerException, UserNotFoundException, GenericException {

        Optional<Account> account = accountRepository.findByUserIdentification(userIdentification);

        // validate that user exists
        if (!account.isPresent()) {
            throw new UserNotFoundException(userIdentification);
        }

        String documentPath = PROFILE_PICTURES_S3_PATH + userIdentification + "/";
        Account newAccount = account.get();

        // validate that document exists
        if (null == newAccount.getProfilePicture() || newAccount.getProfilePicture().isEmpty()) {
            throw new DocumentNotFoundException(userIdentification);
        }

        // delete from aws
        if (!awsS3Client.deleteFile(documentPath + newAccount.getProfilePicture())) {
            throw new ExternalServerException("Error deleting file from AWS S3");
        }

        try {
            newAccount.setProfilePicture(null);
            accountRepository.save(newAccount);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }
    
    @Override
    public void sendPushNotification(String userIdentification, CreatePushNotificationRequestDto createPushNotificationRequestDto, ServiceContext serviceContext) throws RabbitException, DeviceNotFoundException {

        Optional<AccountDevice> accountDevice = accountDeviceRepository.findByUserIdentification(userIdentification);

        // validate if account exists
        if (!accountDevice.isPresent()) {
            throw new DeviceNotFoundException(userIdentification);
        }

        String notificationName = createPushNotificationRequestDto.getNotificationName() == null ? "" : createPushNotificationRequestDto.getNotificationName();
        String notificationImageUrl = createPushNotificationRequestDto.getNotificationImageUrl() == null ? "" : createPushNotificationRequestDto.getNotificationImageUrl();
        Map<String,String> notificationData = createPushNotificationRequestDto.getData() == null ? Collections.emptyMap() : createPushNotificationRequestDto.getData();

        // publish message to rabbitmq
        try {
            eventPublisher.sendPushNotification(serviceContext,
                                                createPushNotificationRequestDto.getNotificationType(),
                                                PushNotificationsExternalEntities.valueOf(accountDevice.get().getExternalEntity()),
                                                userIdentification,
                                                accountDevice.get().getExternalEntityToken(),
                                                createPushNotificationRequestDto.getNotificationTitle(),
                                                createPushNotificationRequestDto.getNotificationText(),
                                                notificationName,
                                                notificationImageUrl,
                                                notificationData);
        } catch (final Exception e) {
            throw new RabbitException(e.getMessage(), e.getCause());
        }
    }

    private void activationCommunicationSender(ServiceContext context, AccountType accountType, Account account) throws NumberParseException {

        switch (accountType) {

            case WEB:
                //TODO
                //Map<String, Object> model = new HashMap<String, Object>();
                //model.put("message", serviceProperties.getActivateAccount() + account.getAccountActivationToken());
                //model.put("nome", account.getProfile().getName());
                //eventPublisher.sendEmailMessage(context, serviceProperties.getAccountActivationEmailEvent(),
                //    ACCOUNT_ACTIVATION_TEMPLATE, EMAIL_FROM, account.getEmail(), CONFIRMATION_SUBJECT, model);
                break;

            case MOBILE:
                Map<String, Object> smsModel = new HashMap<String, Object>();
                smsModel.put(codeLength, codeLengthKey);
                eventPublisher.sendSmsMessage(context, accountActivationSmsEvent, accountActivationTemplate, account.getPhoneNumber(), smsModel);
                break;

            default:
                break;

        }
    }
}