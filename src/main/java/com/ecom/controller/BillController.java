package com.ecom.controller;

import com.ecom.dto.ResponseDTO;
import com.ecom.dto.request.CreateBillRequest;
import com.ecom.entity.Bill;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.UserNotFoundException;
import com.ecom.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class BillController extends BaseController {
    private final BillService billService;

    @PostMapping("/user/bill/create")
    public ResponseDTO<Bill> createBill(@RequestBody CreateBillRequest request) {
        Bill bill;
        try {
            bill = billService.createBill(request, getUser());
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (CannotFoundItemException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseDTO.successResponse(bill);
    }

    @GetMapping("/user/pay_bill")
    public ResponseDTO<String> paymentBill(HttpServletRequest request, @RequestParam("id") Long idBill,
                                           @RequestParam("code") String bankCode) {
        String responseUrl = getStringUrl(request) + "/user/bill_success/" + idBill;

        try {
            String urlPayment = billService.paymentBill(request, getUser(), idBill,
                    bankCode, responseUrl);
            return ResponseDTO.successResponse(urlPayment);
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (CannotFoundItemException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            return ResponseDTO.failedResponse("Payment to vnpay something wrong, please wait to fix!", HttpStatus.BAD_REQUEST);
        }
    }

    private String getStringUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    @GetMapping("/user/bill_success/{id}")
    public String responsePayment(@PathVariable("id") Long idBill) {
        return billService.paymentSuccess(idBill);
    }
}
