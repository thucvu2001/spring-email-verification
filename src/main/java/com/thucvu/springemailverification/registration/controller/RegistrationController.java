package com.thucvu.springemailverification.registration.controller;


import com.thucvu.springemailverification.event.RegistrationCompleteEvent;
import com.thucvu.springemailverification.registration.model.RegistrationRequest;
import com.thucvu.springemailverification.registration.token.VerificationToken;
import com.thucvu.springemailverification.registration.token.VerificationTokenRepository;
import com.thucvu.springemailverification.user.entity.User;
import com.thucvu.springemailverification.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping("/create")
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request) {
        User user = userService.registerUser(registrationRequest);

        // public registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request))); // gửi mail kèm url xác minh
        return "Success! Please check your email to complete your registration";
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GetMapping("/verifyEmail")
    public String applicationUrl (@RequestParam("token") String token) {
        VerificationToken curToken = tokenRepository.findByToken(token);
        if (curToken.getUser().isEnable()) {
            return "This account has already been verified, please login";
        }
        String verificationResult = userService.validateToken(curToken.getToken());
        if (verificationResult.equalsIgnoreCase("valid")) {
            return "Email verified successfully. Now you can login to account";
        }

        return "Invalid verification token";
    }
}
