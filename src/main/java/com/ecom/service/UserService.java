package com.ecom.service;

import com.ecom.dto.request.LoginForm;
import com.ecom.dto.request.RoleToUser;
import com.ecom.entity.User;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.ItemIsExistException;
import com.ecom.exception.UploadFileErrorException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface UserService {
    User getUserByUsername(String username);
    String signUp(User user, String siteUrl) throws ItemIsExistException, MessagingException, UnsupportedEncodingException;
    String signIn(LoginForm form) throws UsernameNotFoundException;
    User addRoleForUser(RoleToUser roleToUser) throws CannotFoundItemException, ItemIsExistException;
    String verifyUser(String code);
    String changeAvatar(MultipartFile file, User user) throws UploadFileErrorException;
}
