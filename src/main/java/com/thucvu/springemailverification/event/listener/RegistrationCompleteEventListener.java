package com.thucvu.springemailverification.event.listener;

import com.thucvu.springemailverification.event.RegistrationCompleteEvent;
import com.thucvu.springemailverification.user.entity.User;
import com.thucvu.springemailverification.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private User theUser;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1. Get the newly registered user: lấy người dùng mới đăng ký
        User curUser = event.getUser();
        // 2. Create a verification token for the user: tạo 1 token xác minh cho người dùng
        String verificationToken = UUID.randomUUID().toString();
        // 3. Save the verification token for the user: lưu token của user đó vào database
        userService.saveUserVerificationToken(verificationToken, curUser);
        // 4. Build the verification url to be sent to the user: tạo url để gửi cho user
        String url = event.getApplicationUrl() + "/register/verifyEmail?token=" + verificationToken;
        // 5. Send the email: gửi mail
        log.info("Click the link to verify your registration: {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + theUser.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us," + "" +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("thucvu.dev@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
