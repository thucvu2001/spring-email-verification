package com.thucvu.springemailverification.registration.controller;


import com.thucvu.springemailverification.event.RegistrationCompleteEvent;
import com.thucvu.springemailverification.registration.model.RegistrationRequest;
import com.thucvu.springemailverification.user.entity.User;
import com.thucvu.springemailverification.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/create")
    public String registerUser(RegistrationRequest registrationRequest, final HttpServletRequest request) {
        User user = userService.registerUser(registrationRequest);

        // public registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request))); // gửi mail kèm url xác minh
        return "Success! Please check your email to complete your registration";
    }

    public String applicationUrl(HttpServletRequest request) {
        return MessageFormat.format("http://{0}:{1}{2}", request.getServerName(), request.getServerPort(), request.getContextPath());
    }
}
