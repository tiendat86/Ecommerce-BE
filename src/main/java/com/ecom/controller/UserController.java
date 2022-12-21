package com.ecom.controller;

import com.ecom.dto.ResponseDTO;
import com.ecom.dto.request.LoginForm;
import com.ecom.dto.request.RoleToUser;
import com.ecom.entity.User;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.ItemIsExistException;
import com.ecom.exception.UploadFileErrorException;
import com.ecom.exception.UserNotFoundException;
import com.ecom.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseDTO<String> signUp(@RequestBody User user, HttpServletRequest request) {
        try {
            return ResponseDTO.successResponse(userService.signUp(user, getStringUrl(request)));
        } catch (ItemIsExistException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (MessagingException | RuntimeException | UnsupportedEncodingException e) {
            throw new RuntimeException("Can't send email exception, please contact us");
        }
    }

    private String getStringUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public ResponseDTO<String> verifyUser(@RequestParam("code") String code) {
        return ResponseDTO.successResponse(userService.verifyUser(code));
    }

    @PostMapping("/signin")
    public ResponseDTO<String> signIn(@RequestBody LoginForm form) {
        try {
            return ResponseDTO.successResponse(userService.signIn(form));
        } catch (UsernameNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/logout_system")
    public void logOut() {
        SecurityContextHolder.clearContext();
    }

    @PostMapping("/user/change_avatar")
    public ResponseDTO<String> setAvatar(@RequestPart MultipartFile file) {
        try {
            return ResponseDTO.successResponse(userService.changeAvatar(file, getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UploadFileErrorException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/role/add")
    public ResponseDTO<User> addRoleToUser(@RequestBody RoleToUser roleToUser) {
        try {
            return ResponseDTO.successResponse(userService.addRoleForUser(roleToUser));
        } catch (CannotFoundItemException | ItemIsExistException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
