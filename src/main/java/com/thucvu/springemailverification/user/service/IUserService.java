package com.thucvu.springemailverification.user.service;

import com.thucvu.springemailverification.registration.model.RegistrationRequest;
import com.thucvu.springemailverification.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getUsers();

    User registerUser(RegistrationRequest request);

    Optional<User> findByEmail(String email);

    void saveUserVerificationToken(String token, User curUser);
}
