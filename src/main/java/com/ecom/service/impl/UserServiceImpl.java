package com.ecom.service.impl;

import com.ecom.dto.request.LoginForm;
import com.ecom.dto.request.RoleToUser;
import com.ecom.entity.Cart;
import com.ecom.entity.User;
import com.ecom.enumuration.EUserRole;
import com.ecom.enumuration.EUserStatus;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.ItemIsExistException;
import com.ecom.exception.UploadFileErrorException;
import com.ecom.repository.CartRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CloudinaryService;
import com.ecom.service.UserService;
import com.ecom.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;

    @Override
    public String signUp(User user, String siteUrl) throws ItemIsExistException, MessagingException, UnsupportedEncodingException {
        if(checkIfUsernameOrEmailExist(user.getUsername(), user.getEmail())) {
            throw new ItemIsExistException("Username or email is register");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerifyCode(generateRandomVerifyCode());
        user.setUserStatus(EUserStatus.PENDING);
        user.setRole(EUserRole.CUSTOMER.name());
        userRepository.save(user);
        sendVerificationEmail(user, siteUrl);
        return "Register success, please check email to verify!";
    }

    private void sendVerificationEmail(User user, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "drakesan86@gmail.com";
        String senderName = "Ecommerce";
        String subject = "Please verify your registration";
        String content = "Dear [[name]], <br>" +
                "Please click the link below to verify your registration:<br>" +
                "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>" +
                "Thank you,<br>" +
                "Ecommerce Application.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getName());
        String verifyUrl = siteUrl + "/verify?code=" + user.getVerifyCode();
        content = content.replace("[[URL]]", verifyUrl);

        helper.setText(content, true);
        mailSender.send(message);
    }

    private boolean checkIfUsernameOrEmailExist(String username, String email) {
        List<User> users = userRepository.findByUsernameOrEmail(username, email);
        return users.size() > 0;
    }

    @Override
    public String verifyUser(String code) {
        User user = userRepository.findByVerifyCode(code);
        long expiration = 30 * 60 * 1000;
        if (user == null || !user.getVerifyCode().equals(code))
            return "Can't not verify because code is diffirent";
        long updateMilitime = Timestamp.valueOf(user.getUpdatedAt()).getTime();
        if (System.currentTimeMillis() - updateMilitime > expiration) {
            user.setVerifyCode(generateRandomVerifyCode());
            user.setUserStatus(EUserStatus.INACTIVE);
            userRepository.save(user);
            return "Can't not verify because code is expired, please send another email";
        }
        user.setUserStatus(EUserStatus.ACTIVE);
        createCartForUser(user);
        userRepository.save(user);
        return "Verify success";
    }

    private void createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
     }

    private String generateRandomVerifyCode() {
        return RandomString.make(64);
    }

    @Override
    public String signIn(LoginForm form) throws UsernameNotFoundException {
        User user = getUserByUsername(form.getUsername());
        if (user == null || !passwordEncoder.matches(form.getPassword(), user.getPassword()))
            throw new UsernameNotFoundException("Error username or password!");
        if (!user.getUserStatus().equals(EUserStatus.ACTIVE))
            throw new UsernameNotFoundException("Gmail is not verify!");
        UserDetails userDetail = convertUserToUserDetail(user, form.getUsername());
        return jwtUtil.generateToken(userDetail);
    }

    private org.springframework.security.core.userdetails.User convertUserToUserDetail(User user, String username) {
        if (user == null) return null;
        String[] roles = user.getRole().split("\\|");
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (int i = 0; i < roles.length; i++) {
            grantedAuthorities.add(new SimpleGrantedAuthority(roles[i]));
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
                grantedAuthorities);
    }

    @Override
    public User addRoleForUser(RoleToUser roleToUser) throws CannotFoundItemException, ItemIsExistException {
        User user = findUserById(roleToUser.getIdUser());
        String role = roleToUser.getRole();
        if (user == null || role == null)
            throw new CannotFoundItemException("Can't find user or role in db");
        String userRole = user.getRole();
        if (userRole.indexOf(role) == -1) {
            userRole = userRole + "|" + role;
            user.setRole(userRole);
            return userRepository.save(user);
        }
        throw new ItemIsExistException("User has been role!");
    }

    private User findUserById(Long userId) {
        Optional<User> userOption = userRepository.findById(userId);
        return (userOption.isPresent()) ? userOption.get() : null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        return convertUserToUserDetail(user, username);
    }

    @Override
    public User getUserByUsername(String username) {
        List<User> users = userRepository.findByUsernameOrEmail(username, username);
        return (users.size() == 1) ? users.get(0) : null;
    }

    @Override
    public String changeAvatar(MultipartFile fileImg, User user) throws UploadFileErrorException {
        String nameFile = generateFilename(user.getId(), user.getUsername());
        String urlImage = cloudinaryService.uploadFile(fileImg, nameFile);
        if (!StringUtils.isBlank(urlImage)) {
            user.setImageUrl(urlImage);
            userRepository.save(user);
        }
        return urlImage;
    }

    private String generateFilename(Long id, String name) {
        return id + "_" + name + "_avatar";
    }
}
