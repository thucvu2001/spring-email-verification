package com.thucvu.springemailverification.user.controller;

import com.thucvu.springemailverification.user.entity.User;
import com.thucvu.springemailverification.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get-all")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
