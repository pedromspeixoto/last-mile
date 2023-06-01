package com.lastmile.accountservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.lastmile.accountservice.service.AccountService;
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
import com.lastmile.utils.logs.CustomLogging;
import com.lastmile.utils.logs.interceptor.PreHandleValidation;
import com.lastmile.utils.models.response.ErrorResponse;
import com.lastmile.utils.models.response.SuccessResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class AccountController {

    private final AccountService accountService;
    private final CustomLogging logger;

    public AccountController(final AccountService accountService,
                             final CustomLogging logger) {
        this.accountService = accountService;
        this.logger = logger;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewAccount(HttpServletRequest httpRequest,
                                              @Valid @RequestBody CreateAccountDto account) throws FeignCommunicationException, GenericException, UserAlreadyExistsException, MissingRequiredFieldException, RabbitException {

        logger.info("request body: " + account.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);
        CreateAccountResponseDto createAccountResponseDto = new CreateAccountResponseDto();

        try {
            createAccountResponseDto = accountService.createAccount(account, serviceContext);
        } catch (FeignCommunicationException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error in external service", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (MissingRequiredFieldException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Missing required field", ex.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (RabbitException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error sending activation token", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UserAlreadyExistsException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Account with this username already exists.", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + createAccountResponseDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.CREATED.value(), "Account created successfully", createAccountResponseDto), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @Valid @RequestBody UserLoginDto user) throws GenericException, UserNotFoundException, UserNotActiveException, InvalidLoginException {

        logger.info("request body: " + user.toString(), request);
        ReturnUserLoginDto loginUser = new ReturnUserLoginDto();

        try {
            loginUser = accountService.login(user);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserNotActiveException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Account is not active.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (InvalidLoginException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Invalid credentials.", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + loginUser.toString(), request);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Login successful", loginUser), HttpStatus.OK);

    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(HttpServletRequest request,
                                             @Valid @RequestBody UserRecoverPasswordRequestDto recoverPasswordDto) throws GenericException, UserNotFoundException, UserNotActiveException {

        logger.info("request body: " + recoverPasswordDto.toString(), request);
        ServiceContext serviceContext = new ServiceContext(request);

        try {
            accountService.recoverPassword(recoverPasswordDto, serviceContext);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserNotActiveException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Account is not active.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Request to recover password processed successfully"), HttpStatus.OK);

    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(HttpServletRequest request,
                                            @Valid @RequestBody UserUpdatePasswordRequestDto updatePasswordRequestDto) throws GenericException, UserNotFoundException, UserNotActiveException, InvalidActivationCodeException {

        logger.info("request body: " + updatePasswordRequestDto.toString(), request);
        ServiceContext serviceContext = new ServiceContext(request);

        try {
             accountService.updatePassword(updatePasswordRequestDto, serviceContext);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserNotActiveException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Account is not active.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (InvalidActivationCodeException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Invalid token.", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), request);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Password changed successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to get all registered users - protected for ADMINS only
     */
    @GetMapping
    public ResponseEntity<?> getAccounts(HttpServletRequest httpRequest,
                                         @RequestParam(value = "limit", required = false) Optional<Integer> limit,
                                         @RequestParam(value = "offset", required = false) Optional<Integer> offset,
                                         @RequestParam(value = "role", required = false) Optional<String> role,
                                         @RequestParam(value = "firstName", required = false) Optional<String> firstName,
                                         @RequestParam(value = "lastName", required = false) Optional<String> lastName,
                                         @RequestParam(value = "phoneNumber", required = false) Optional<String> phoneNumber,
                                         @RequestParam(value = "email", required = false) Optional<String> email,
                                         @RequestParam(value = "accountType", required = false) Optional<String> accountType) throws GenericException {

        if (!PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        List<GetAccountDto> accounts;

        try {
            accounts = accountService.getAccounts(limit, offset, role, firstName, lastName, phoneNumber, email, accountType);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching accounts", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Accounts retrieved successfully",accounts), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual user info
     */
    @GetMapping("/{userIdentification}")
    public ResponseEntity<?> getAccount(HttpServletRequest httpRequest, @PathVariable(value = "userIdentification") String userIdentification) throws UserNotFoundException, GenericException {

        GetAccountDto accountDto;

        if (!PreHandleValidation.checkSameUser(httpRequest, userIdentification) && !PreHandleValidation.hasAdminAuthority(httpRequest) && !PreHandleValidation.isFeignRequest(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."),
                    HttpStatus.FORBIDDEN);
        }

        try {
            accountDto = accountService.getAccount(userIdentification);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching account information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account retrieved successfully", accountDto), HttpStatus.OK);

    }

    /*
     * Endpoint to get individual user device info
     */
    @GetMapping("/{userIdentification}/device")
    public ResponseEntity<?> getAccountDevice(HttpServletRequest httpRequest,
                                              @PathVariable(value = "userIdentification") String userIdentification) throws UserNotFoundException, GenericException {

        GetAccountDeviceRequestDto accountDeviceDto;

        if (!PreHandleValidation.hasAdminAuthority(httpRequest) && !PreHandleValidation.isFeignRequest(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."),
                    HttpStatus.FORBIDDEN);
        }

        try {
            accountDeviceDto = accountService.getAccountDevice(userIdentification);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching account device information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("response body: " + accountDeviceDto.toString(), httpRequest);
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account device information retrieved successfully", accountDeviceDto), HttpStatus.OK);

    }

    /*
     * Endpoint to delete user by user_identification - protected for ADMINS only
     */
    @DeleteMapping("/{userIdentification}")
    public ResponseEntity<?> deleteAccount(HttpServletRequest httpRequest,
                                           @PathVariable(value = "userIdentification") String userIdentification) throws UserNotFoundException, GenericException {

        if (!PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        try {
            accountService.deleteAccount(userIdentification);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error fetching account information.", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account deleted successfully"), HttpStatus.OK);
    }

    @PutMapping("/{userIdentification}")
    public ResponseEntity<?> updateAccount(HttpServletRequest httpRequest,
                                           @PathVariable(value = "userIdentification") String userIdentification,
                                           @Valid @RequestBody UpdateAccountRequestDto updateAccountRequestDto) throws UserNotFoundException, GenericException, AddressNotFoundException, PaymentDetailNotFoundException {

        // validate if user has permission to perform this request - same user_id or is ADMIN
        if (!PreHandleValidation.checkSameUser(httpRequest, userIdentification) && !PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>( new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        logger.info("request body: " + updateAccountRequestDto.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            accountService.updateAccount(userIdentification, updateAccountRequestDto, serviceContext);
        } catch (AddressNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Address not found.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (PaymentDetailNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Payment detail not found.", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found.", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating account", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account updated successfully"), HttpStatus.OK);

    }

    @PostMapping("/resend-activation-code")
    public ResponseEntity<?> resendActivationCode(HttpServletRequest httpRequest,
                                                  @Valid @RequestBody ResendActivationCodeRequestDto resendActivationCodeRequestDto) throws RabbitException, UserNotFoundException, UserAlreadyActiveException, GenericException  {

        logger.info("request body: " + resendActivationCodeRequestDto.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            accountService.resendActivationCode(resendActivationCodeRequestDto, serviceContext);
        } catch (RabbitException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error sending activation token", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserAlreadyActiveException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Account is already active", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Activation code resent successfully"), HttpStatus.OK);

    }

    @PostMapping("/resend-password-activation-code")
    public ResponseEntity<?> resendPasswordActivationCode(HttpServletRequest httpRequest,
                                                          @Valid @RequestBody ResendActivationCodeRequestDto resendActivationCodeRequestDto) throws RabbitException, UserNotFoundException, UserAlreadyActiveException, GenericException  {

        logger.info("request body: " + resendActivationCodeRequestDto.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            accountService.resendPasswordActivationCode(resendActivationCodeRequestDto, serviceContext);
        } catch (RabbitException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error sending activation token", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserNotActiveException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Account is not active", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Activation code resent successfully"), HttpStatus.OK);

    }

    @PatchMapping("/activate")
    public ResponseEntity<?> activateAccount(@Valid @RequestBody ActivationCodeDto activationCodeDto) throws UserNotFoundException, UserAlreadyActiveException, GenericException, InvalidActivationCodeException  {

        logger.info("request body: " + activationCodeDto.toString());

        try {
            accountService.activateAccount(activationCodeDto);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserAlreadyActiveException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "Account is already active", ex.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (InvalidActivationCodeException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Invalid activation code", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account activated successfully"), HttpStatus.OK);

    }

    @PatchMapping("/{userIdentification}/role")
    public ResponseEntity<?> updateAccountRole(HttpServletRequest httpRequest,
                                               @PathVariable(value = "userIdentification") String userIdentification,
                                               @Valid @RequestBody PatchAccountRoleRequestDto accountRoleRequestDto) throws UserNotFoundException, GenericException  {

        // validate if user has permission to perform this request - same user_id or is ADMIN
        if (!PreHandleValidation.isFeignRequest(httpRequest) && !PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>( new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        logger.info("request body: " + accountRoleRequestDto.toString());

        try {
            accountService.updateAccountRole(accountRoleRequestDto, userIdentification);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error processing request", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Account role updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to upsert profile picture
     */
    @RequestMapping(value = "/{userIdentification}/profile-picture", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertProfilePicture(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "userIdentification") String userIdentification,
                                                  @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) throws ExternalServerException, UserNotFoundException, GenericException {

        // validate if user has permission to perform this request - same user_id or is ADMIN
        if (!PreHandleValidation.checkSameUser(httpRequest, userIdentification) && !PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>( new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        try {
            accountService.upsertProfilePicture(userIdentification, profilePicture);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error upserting profile picture", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Profile picture updated successfully"), HttpStatus.OK);

    }

    /*
     * Endpoint to delete profile picture
     */
    @DeleteMapping("/{userIdentification}/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "userIdentification") String userIdentification) throws DocumentNotFoundException, ExternalServerException, UserNotFoundException, GenericException {

        // validate if user has permission to perform this request - same user_id or is ADMIN
        if (!PreHandleValidation.checkSameUser(httpRequest, userIdentification) && !PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>( new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        try {
            accountService.deleteProfilePicture(userIdentification);
        } catch (ExternalServerException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Error uploading file to AWS S3 Bucket", ex.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DocumentNotFoundException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Document not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (UserNotFoundException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (GenericException ex) {
            logger.error("error message: " + ex.getMessage());
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting profile picture", ex.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.OK.value(), "Profile picture deleted successfully"), HttpStatus.OK);

    }

    @PostMapping("/{userIdentification}/push-notification")
    public ResponseEntity<?> sendPushNotification(HttpServletRequest httpRequest,
                                                  @PathVariable(value = "userIdentification") String userIdentification,
                                                  @Valid @RequestBody CreatePushNotificationRequestDto createPushNotificationRequestDto) throws RabbitException, DeviceNotFoundException  {

        // validate if user has permission to perform this request - same user_id or is ADMIN
        if (!PreHandleValidation.isFeignRequest(httpRequest) && !PreHandleValidation.hasAdminAuthority(httpRequest)) {
            logger.error("user does not have permission to perform this request", httpRequest);
            return new ResponseEntity<ErrorResponse>( new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden."), HttpStatus.FORBIDDEN);
        }

        logger.info("request body: " + createPushNotificationRequestDto.toString(), httpRequest);
        ServiceContext serviceContext = new ServiceContext(httpRequest);

        try {
            accountService.sendPushNotification(userIdentification, createPushNotificationRequestDto, serviceContext);
        } catch (RabbitException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Error sending activation token", ex.getMessage()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (DeviceNotFoundException ex) {
            logger.error("error message: " + ex.getMessage(), httpRequest);
            return new ResponseEntity<ErrorResponse>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Account not found", ex.getMessage()),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SuccessResponse>(
                new SuccessResponse(HttpStatus.ACCEPTED.value(), "Push notification message sent for processing"), HttpStatus.ACCEPTED);

    }

}