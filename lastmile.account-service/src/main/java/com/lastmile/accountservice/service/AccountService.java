package com.lastmile.accountservice.service;

import java.util.List;
import java.util.Optional;

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
import com.lastmile.accountservice.service.exception.DeviceNotFoundException;
import com.lastmile.accountservice.service.exception.InvalidActivationCodeException;
import com.lastmile.accountservice.service.exception.InvalidLoginException;
import com.lastmile.accountservice.service.exception.UserAlreadyActiveException;
import com.lastmile.accountservice.service.exception.UserAlreadyExistsException;
import com.lastmile.accountservice.service.exception.UserNotActiveException;
import com.lastmile.accountservice.service.exception.UserNotFoundException;
import com.lastmile.utils.exceptions.AddressNotFoundException;
import com.lastmile.utils.exceptions.DocumentNotFoundException;
import com.lastmile.utils.exceptions.ExternalServerException;
import com.lastmile.utils.exceptions.FeignCommunicationException;
import com.lastmile.utils.exceptions.GenericException;
import com.lastmile.utils.exceptions.MissingRequiredFieldException;
import com.lastmile.utils.exceptions.PaymentDetailNotFoundException;
import com.lastmile.utils.exceptions.RabbitException;
import com.lastmile.utils.context.ServiceContext;

import org.springframework.web.multipart.MultipartFile;

public interface AccountService {

    // create a new account
    CreateAccountResponseDto createAccount(CreateAccountDto account, ServiceContext serviceContext) throws FeignCommunicationException, GenericException, UserAlreadyExistsException, MissingRequiredFieldException, RabbitException;

    // invoke auth server and login
    ReturnUserLoginDto login(UserLoginDto user) throws GenericException, UserNotFoundException, UserNotActiveException, InvalidLoginException;

    // recover password
    void recoverPassword(UserRecoverPasswordRequestDto recoverPasswordRequestDto, ServiceContext serviceContext) throws GenericException, UserNotFoundException, UserNotActiveException;

    // update password
    void updatePassword(UserUpdatePasswordRequestDto updatePasswordRequestDto, ServiceContext serviceContext) throws GenericException, UserNotFoundException, UserNotActiveException, InvalidActivationCodeException;

    // get all registered accounts
    List<GetAccountDto> getAccounts(Optional<Integer> limit, Optional<Integer> offset, Optional<String> role, Optional<String> firstName, Optional<String> lastName, Optional<String> phoneNumber, Optional<String> email, Optional<String> accountType) throws GenericException;

    // get account from user identification
    GetAccountDto getAccount(String userIdentification) throws UserNotFoundException, GenericException;

    // get account device details by user identification
    GetAccountDeviceRequestDto getAccountDevice(String userIdentification) throws UserNotFoundException, GenericException;

    // delete account by user identification
    void deleteAccount(String userIdentification) throws UserNotFoundException, GenericException;

    // update account by user identification
    void updateAccount(String userIdentification, UpdateAccountRequestDto updateAccountRequestDto, ServiceContext serviceContext) throws PaymentDetailNotFoundException, AddressNotFoundException, UserNotFoundException, GenericException;

    // activate account using activation token
    void activateAccount(ActivationCodeDto activationCodeDto) throws UserNotFoundException, UserAlreadyActiveException, GenericException, InvalidActivationCodeException;

    // resend activation code
    void resendActivationCode(ResendActivationCodeRequestDto resendActivationCodeRequestDto, ServiceContext serviceContext) throws RabbitException, UserNotFoundException, UserAlreadyActiveException, GenericException;

    // resend password activation code
    void resendPasswordActivationCode(ResendActivationCodeRequestDto resendActivationCodeRequestDto, ServiceContext serviceContext) throws RabbitException, UserNotFoundException, UserNotActiveException, GenericException;

    // update account role
    void updateAccountRole(PatchAccountRoleRequestDto accountRoleRequestDto, String userIdentification) throws UserNotFoundException, GenericException;

    // upsert profile picture by user identification
    void upsertProfilePicture(String userIdentification, MultipartFile profilePicture) throws ExternalServerException, UserNotFoundException, GenericException;

    // delete profile picture by user identification
    void deleteProfilePicture(String userIdentification) throws DocumentNotFoundException, ExternalServerException, UserNotFoundException, GenericException;

    // send push notification
    void sendPushNotification(String userIdentification, CreatePushNotificationRequestDto createPushNotificationRequestDto, ServiceContext serviceContext) throws RabbitException, DeviceNotFoundException;

}