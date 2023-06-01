package com.lastmile.addressservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lastmile.addressservice.domain.Address;
import com.lastmile.addressservice.dto.CreateAddressRequestDto;
import com.lastmile.addressservice.dto.CreateAddressResponseDto;
import com.lastmile.addressservice.dto.GetAddressResponseDto;
import com.lastmile.addressservice.dto.UpdateAddressRequestDto;
import com.lastmile.addressservice.repository.AddressRepository;
import com.lastmile.addressservice.service.AddressService;
import com.lastmile.addressservice.service.exception.GenericException;
import com.lastmile.addressservice.service.exception.AddressForbiddenException;
import com.lastmile.addressservice.service.exception.AddressNotFoundException;
import com.lastmile.utils.context.ServiceContext;
import com.lastmile.utils.validations.Validator;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Configuration
public class AddressServiceImpl implements AddressService {

    private static final Integer DEFAULT_VALUE_LIMIT = 10;
    private static final Integer DEFAULT_VALUE_OFFSET = 0;

    private final AddressRepository addressRepository;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public AddressServiceImpl(final AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(rollbackFor = { GenericException.class })
    public CreateAddressResponseDto createAddress(CreateAddressRequestDto addressDto, ServiceContext serviceContext) throws GenericException {

        ModelMapper modelMapper = new ModelMapper();
        // map from DTO
        Address address = modelMapper.map(addressDto, Address.class);

        try {

            // set address id
            address.setAddressIdentification(UUID.randomUUID().toString());

            // save address
            addressRepository.save(address);

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        CreateAddressResponseDto createAddressResponseDto = new CreateAddressResponseDto(address.getAddressIdentification());

        return createAddressResponseDto;

    }

    @Override
    public List<GetAddressResponseDto> getAddresses(Optional<Integer> limit, Optional<Integer> offset, Optional<String> addressIdentification, Optional<String> entityIdentification, Optional<String> entityType, ServiceContext serviceContext) throws AddressForbiddenException, GenericException {

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, entityIdentification, entityType)) {
            throw new AddressForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        List<Address> addresses;

        Pageable pageable = PageRequest.of(offset.orElse(DEFAULT_VALUE_OFFSET), limit.orElse(DEFAULT_VALUE_LIMIT));

        try {
            // try to fetch addresses
            addresses = addressRepository.findAllAddresses(entityIdentification.orElse(""), addressIdentification.orElse(""), pageable);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return addresses.stream().map(address -> modelMapper.map(address, GetAddressResponseDto.class)).collect(Collectors.toList());

    }

    @Override
    public GetAddressResponseDto getAddress(String addressIdentification, ServiceContext serviceContext) throws AddressForbiddenException, AddressNotFoundException, GenericException {

        Optional<Address> address = addressRepository.findByAddressIdentification(addressIdentification);

        // validate if address exists
        if (!address.isPresent()) {
            throw new AddressNotFoundException(addressIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(address.get().getEntityIdentification()), Optional.of(address.get().getEntityType()))) {
            throw new AddressForbiddenException();
        }

        ModelMapper modelMapper = new ModelMapper();
        GetAddressResponseDto getAddressResponseDto = new GetAddressResponseDto();

        try {
            // try to map address to dto
            getAddressResponseDto = modelMapper.map(address.get(), GetAddressResponseDto.class);
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

        return getAddressResponseDto;
    }

    @Override
    @Transactional(rollbackFor = { AddressNotFoundException.class, GenericException.class })
    public void deleteAddress(String addressIdentification, ServiceContext serviceContext) throws AddressForbiddenException, AddressNotFoundException, GenericException {

        Optional<Address> address = addressRepository.findByAddressIdentification(addressIdentification);

        // validate if address exists
        if (!address.isPresent()) {
            throw new AddressNotFoundException(addressIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(address.get().getEntityIdentification()), Optional.of(address.get().getEntityType()))) {
            throw new AddressForbiddenException();
        }

        try {
            addressRepository.deleteById(address.get().getId());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }

    @Override
    @Transactional(rollbackFor = { AddressForbiddenException.class, AddressNotFoundException.class, GenericException.class })
    public void updateAddress(String addressIdentification, UpdateAddressRequestDto updateAddressRequestDto, ServiceContext serviceContext) throws AddressForbiddenException, AddressNotFoundException, GenericException {

        Optional<Address> address = addressRepository.findByAddressIdentification(addressIdentification);

        // validate if address exists
        if (!address.isPresent()) {
            throw new AddressNotFoundException(addressIdentification);
        }

        // validate access
        if (!Validator.isAdmin(serviceContext) && !Validator.isEntityAllowed(serviceContext, Optional.of(address.get().getEntityIdentification()), Optional.of(address.get().getEntityType()))) {
            throw new AddressForbiddenException();
        }

        try {
            Address updatedAdress = address.get();

            // address type
            if (updateAddressRequestDto.getAddressType() != null && !updateAddressRequestDto.getAddressType().toString().isEmpty()) {
                updatedAdress.setAddressType(updateAddressRequestDto.getAddressType().toString());
            }
            // address line 1
            if (updateAddressRequestDto.getAddressLine1() != null && !updateAddressRequestDto.getAddressLine1().isEmpty()) {
                updatedAdress.setAddressLine1(updateAddressRequestDto.getAddressLine1());
            }
            // address line 2
            if (updateAddressRequestDto.getAddressLine2() != null && !updateAddressRequestDto.getAddressLine2().isEmpty()) {
                updatedAdress.setAddressLine2(updateAddressRequestDto.getAddressLine2());
            }
            // street number
            if (updateAddressRequestDto.getStreetNumber() != null && !updateAddressRequestDto.getStreetNumber().isEmpty()) {
                updatedAdress.setStreetNumber(updateAddressRequestDto.getStreetNumber());
            }
            // floor
            if (updateAddressRequestDto.getFloor() != null && !updateAddressRequestDto.getFloor().isEmpty()) {
                updatedAdress.setFloor(updateAddressRequestDto.getFloor());
            }
            // zip code
            if (updateAddressRequestDto.getZipCode() != null && !updateAddressRequestDto.getZipCode().isEmpty()) {
                updatedAdress.setZipCode(updateAddressRequestDto.getZipCode());
            }
            // city
            if (updateAddressRequestDto.getCity() != null && !updateAddressRequestDto.getCity().isEmpty()) {
                updatedAdress.setCity(updateAddressRequestDto.getCity());
            }
            // country
            if (updateAddressRequestDto.getCountry() != null && !updateAddressRequestDto.getCountry().isEmpty()) {
                updatedAdress.setCountry(updateAddressRequestDto.getCountry());
            }
            // address notes
            if (updateAddressRequestDto.getAddressNotes() != null && !updateAddressRequestDto.getAddressNotes().isEmpty()) {
                updatedAdress.setAddressNotes(updateAddressRequestDto.getAddressNotes());
            }

            // update account
            addressRepository.save(updatedAdress);

        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), e.getCause());
        }

    }
}