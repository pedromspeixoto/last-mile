package com.lastmile.paymentservice.client.easypay;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lastmile.paymentservice.client.easypay.dto.CreditCardFrequentDetailsResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.FrequentResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.OutPaymentResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayCaptureRequestDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayCaptureResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayFrequentPaymentDetailsResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayFrequentPaymentRequestDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayFrequentPaymentResponseDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayOutPaymentRequestDto;
import com.lastmile.paymentservice.client.easypay.dto.external.EasypayOutPaymentResponseDto;
import com.lastmile.paymentservice.enums.OutPaymentType;
import com.lastmile.paymentservice.enums.PaymentDetailType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Configuration
public class EasyPayAPIConnector {

    @Value("${easypay.api.base-url}")
    private String EASYPAY_API_BASEURL;

    @Value("${easypay.api.frequent-sufix}")
    private String EASYPAY_API_FREQUENT_SUFIX;

    @Value("${easypay.api.out-payment-sufix}")
    private String EASYPAY_API_OUT_PAYMENT_SUFIX;

    @Value("${easypay.api.capture-sufix}")
    private String EASYPAY_API_CAPTURE_SUFIX;    

    @Value("${easypay.api.headers.account-id.name}")
    private String EASYPAY_API_ACCOUNTID_NAME;

    @Value("${easypay.api.headers.account-id.value}")
    private String EASYPAY_API_ACCOUNTID_VALUE;

    @Value("${easypay.api.headers.api-key.name}")
    private String EASYPAY_API_APIKEY_NAME;

    @Value("${easypay.api.headers.api-key.value}")
    private String EASYPAY_API_APIKEY_VALUE;

    @Value("${easypay.api.headers.signature.name}")
    private String EASYPAY_API_SIGNATURE_NAME;

    @Value("${easypay.api.headers.signature.value}")
    private String EASYPAY_API_SIGNATURE_VALUE;

    Logger logger = LoggerFactory.getLogger(EasyPayAPIConnector.class);

    public FrequentResponseDto createFrequentPayment(String entityIdentification,
                                                     String name,
                                                     String email,
                                                     String phoneNumber,
                                                     String fiscalNumber,
                                                     String paymentDetailIdentification,
                                                     PaymentDetailType paymentDetailType) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = this.getHeaders();

        EasypayFrequentPaymentRequestDto easypayRequestDto = new EasypayFrequentPaymentRequestDto(entityIdentification,
                                                                                                  name,
                                                                                                  email,
                                                                                                  phoneNumber,
                                                                                                  fiscalNumber,
                                                                                                  paymentDetailIdentification,
                                                                                                  paymentDetailType);

        HttpEntity<EasypayFrequentPaymentRequestDto> request = new HttpEntity<EasypayFrequentPaymentRequestDto>(
                easypayRequestDto, headers);
        
        logger.info("headers: " + request.getHeaders().toString());
        logger.info("body:" + request.getBody());
        
        EasypayFrequentPaymentResponseDto easypayFrequentResponseDto = new EasypayFrequentPaymentResponseDto();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        ResponseEntity<String> httpResponse = null;
        try {
            httpResponse = restTemplate.exchange(EASYPAY_API_BASEURL + EASYPAY_API_FREQUENT_SUFIX,
                    HttpMethod.POST, request, String.class);
            easypayFrequentResponseDto = objectMapper.readValue(httpResponse.getBody(), EasypayFrequentPaymentResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }

        logger.info("Easypay response status: " + httpResponse.getStatusCode());
        logger.info("Easypay response body: " + httpResponse.getBody());

        return new FrequentResponseDto(easypayFrequentResponseDto);

    }

    public CreditCardFrequentDetailsResponseDto getCreditCardFrequentPaymentDetails(String externalPaymentIdentification) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = this.getHeaders();

        HttpEntity<?> request = new HttpEntity<>(headers);

        EasypayFrequentPaymentDetailsResponseDto easypayFrequentDetailsResponseDto = new EasypayFrequentPaymentDetailsResponseDto();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        ResponseEntity<String> httpResponse = null;

        try {
            httpResponse = restTemplate.exchange(EASYPAY_API_BASEURL + EASYPAY_API_FREQUENT_SUFIX + externalPaymentIdentification,
                    HttpMethod.GET, request, String.class);
            easypayFrequentDetailsResponseDto = objectMapper.readValue(httpResponse.getBody(), EasypayFrequentPaymentDetailsResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e.getCause());
        }

        logger.info("Easypay response body: " + httpResponse.getBody());

        return new CreditCardFrequentDetailsResponseDto(easypayFrequentDetailsResponseDto);

    }

    public String capturePayment(String externalPaymentIdentification, String paymentIdentification, String transactionDescription, Double transactionValue)
            throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = this.getHeaders();

        EasypayCaptureRequestDto easypayCaptureRequestDto = new EasypayCaptureRequestDto(paymentIdentification, transactionDescription, transactionValue);

        HttpEntity<EasypayCaptureRequestDto> request = new HttpEntity<EasypayCaptureRequestDto>(
            easypayCaptureRequestDto, headers);

        EasypayCaptureResponseDto easypayCaptureResponseDto = new EasypayCaptureResponseDto();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        ResponseEntity<String> httpResponse = null;
        try {
            httpResponse = restTemplate.exchange(EASYPAY_API_BASEURL + EASYPAY_API_CAPTURE_SUFIX + externalPaymentIdentification, HttpMethod.POST, request, String.class);
            easypayCaptureResponseDto = objectMapper.readValue(httpResponse.getBody(), EasypayCaptureResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }

        logger.info("Easypay response (Payment ID: " + paymentIdentification + ") - " + httpResponse.getBody());

        return easypayCaptureResponseDto.getId();
    }

    public OutPaymentResponseDto createOutPayment(String outPaymentIdentification,
                                                  String sourceAccountIdentification,
                                                  String sourceAccountExternalIdentification,
                                                  String sourceAccountHolderName,
                                                  String sourceAccountEmail,
                                                  String sourceAccountPhoneNumber,
                                                  String sourceAccountFiscalNumber,
                                                  String targetAccountIdentification,
                                                  String targetAccountHolderName,
                                                  String targetAccountEmail,
                                                  String targetAccountPhoneNumber,
                                                  String targetAccountIban,
                                                  String countryCode,
                                                  Date scheduledDate,
                                                  OutPaymentType method,
                                                  Double value) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = this.getSignatureHeaders();

        EasypayOutPaymentRequestDto easypayOutRequestDto = new EasypayOutPaymentRequestDto(outPaymentIdentification,
                                                                                           sourceAccountIdentification,
                                                                                           sourceAccountExternalIdentification,
                                                                                           sourceAccountHolderName,
                                                                                           sourceAccountEmail,
                                                                                           sourceAccountPhoneNumber,
                                                                                           sourceAccountFiscalNumber,
                                                                                           targetAccountIdentification,
                                                                                           targetAccountHolderName,
                                                                                           targetAccountEmail,
                                                                                           targetAccountPhoneNumber,
                                                                                           targetAccountIban,
                                                                                           countryCode,
                                                                                           scheduledDate,
                                                                                           method,
                                                                                           value);

        HttpEntity<EasypayOutPaymentRequestDto> request = new HttpEntity<EasypayOutPaymentRequestDto>(easypayOutRequestDto, headers);
        
        logger.info("headers: " + request.getHeaders().toString());
        logger.info("body:" + request.getBody());
        
        EasypayOutPaymentResponseDto easypayOutPaymentResponseDto = new EasypayOutPaymentResponseDto();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        ResponseEntity<String> httpResponse = null;
        try {
            httpResponse = restTemplate.exchange(EASYPAY_API_BASEURL + EASYPAY_API_OUT_PAYMENT_SUFIX, HttpMethod.POST, request, String.class);
            easypayOutPaymentResponseDto = objectMapper.readValue(httpResponse.getBody(), EasypayOutPaymentResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }

        logger.info("Easypay response status: " + httpResponse.getStatusCode());
        logger.info("Easypay response body: " + httpResponse.getBody());

        return new OutPaymentResponseDto(easypayOutPaymentResponseDto);

    }

    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(EASYPAY_API_ACCOUNTID_NAME , EASYPAY_API_ACCOUNTID_VALUE);
        headers.set(EASYPAY_API_APIKEY_NAME , EASYPAY_API_APIKEY_VALUE);

        return headers;

    }

    private HttpHeaders getSignatureHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(EASYPAY_API_ACCOUNTID_NAME , EASYPAY_API_ACCOUNTID_VALUE);
        headers.set(EASYPAY_API_APIKEY_NAME , EASYPAY_API_APIKEY_VALUE);
        headers.set(EASYPAY_API_SIGNATURE_NAME , EASYPAY_API_SIGNATURE_VALUE);

        return headers;

    }
}