package com.ecom.service;

import com.ecom.dto.response.AddressDTO;
import com.ecom.dto.response.PhoneDTO;
import com.ecom.entity.Address;
import com.ecom.entity.PhoneNumber;
import com.ecom.entity.User;
import com.ecom.exception.CannotFoundItemException;

import java.util.List;

public interface UserInforService {
    Address createNewAddressForUser(Address address, User user);
    String deleteAddress(Long idAddress, User user) throws CannotFoundItemException;
    List<AddressDTO> getAllUserAddress(User user);
    PhoneNumber createNewPhoneNumber(PhoneNumber phoneNumber, User user);
    Boolean verifyPhoneNumber(Long idPhoneNumber, String verifyCode, User user);
    String deletePhoneNumber(Long idPhoneNumber, User user) throws CannotFoundItemException;
    List<PhoneDTO> getAllUserPhone(User user);
}
