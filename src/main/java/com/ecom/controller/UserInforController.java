package com.ecom.controller;

import com.ecom.dto.ResponseDTO;
import com.ecom.dto.response.AddressDTO;
import com.ecom.entity.Address;
import com.ecom.entity.PhoneNumber;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.UserNotFoundException;
import com.ecom.service.UserInforService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserInforController extends BaseController {
    private final UserInforService userInforService;

    @PostMapping("/address/create")
    public ResponseDTO<Address> createPhoneNumber(@RequestBody Address address) {
        try {
            return ResponseDTO.successResponse(userInforService.createNewAddressForUser(address, getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/address/delete/{idAddress}")
    public ResponseDTO<String> deleteAddress(@PathVariable Long idAddress) {
        try {
            return ResponseDTO.successResponse(userInforService.deleteAddress(idAddress, getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (CannotFoundItemException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/address/all_address")
    public ResponseDTO<List<AddressDTO>> getAllAddress() {
        try {
            return ResponseDTO.successResponse(userInforService.getAllUserAddress(getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/phone/create")
    public ResponseDTO<PhoneNumber> createPhoneNumber(@RequestBody PhoneNumber phoneNumber) {
        try {
            return ResponseDTO.successResponse(userInforService.createNewPhoneNumber(phoneNumber, getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/phone/verify")
    public ResponseDTO<String> verifyPhoneNumber(@PathParam("id") Long idPhone, @PathParam("code") String verifyCode) {
        try {
            Boolean responseVerify = userInforService.verifyPhoneNumber(idPhone, verifyCode, getUser());
            if (responseVerify)
                return ResponseDTO.successResponse("Verify phonenumber success");
            else
                return ResponseDTO.failedResponse("Phone is wrong or code isn't matches", HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/phone/delete/{idPhone}")
    public ResponseDTO<String> deletePhoneNumber(@PathVariable Long idPhone) {
        try {
            return ResponseDTO.successResponse(userInforService.deletePhoneNumber(idPhone, getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (CannotFoundItemException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
