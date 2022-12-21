package com.ecom.service;

import com.ecom.dto.request.CreateBillRequest;
import com.ecom.entity.Bill;
import com.ecom.entity.User;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface BillService {
    Bill createBill(CreateBillRequest createBillRequest, User user) throws UserNotFoundException, CannotFoundItemException;
    String paymentBill(HttpServletRequest request, User user, Long idBill, String bankCode, String responseUrl)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, CannotFoundItemException;
    String paymentSuccess(Long idBill);
}
