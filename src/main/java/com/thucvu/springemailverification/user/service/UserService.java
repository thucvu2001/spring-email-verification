package com.thucvu.springemailverification.user.service;

import com.thucvu.springemailverification.exception.UserAlreadyExistsException;
import com.thucvu.springemailverification.registration.model.RegistrationRequest;
import com.thucvu.springemailverification.registration.token.VerificationToken;
import com.thucvu.springemailverification.registration.token.VerificationTokenRepository;
import com.thucvu.springemailverification.user.entity.User;
import com.thucvu.springemailverification.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> user = userRepository.findByEmail(request.email());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        User curUser = new User();
        curUser.setFirstName(request.firstName());
        curUser.setLastName(request.lastName());
        curUser.setEmail(request.email());
        curUser.setPassword(passwordEncoder.encode(request.password()));
        curUser.setRole(request.role());

        return userRepository.save(curUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(String token, User curUser) {
        var verificationToken = new VerificationToken(token, curUser);
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String curToken) {
        VerificationToken token = tokenRepository.findByToken(curToken);
        if (token == null) {
            return "Invalid Token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if (token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            tokenRepository.delete(token);
            return "Token already expired (Token het han)";
        }
        user.setEnable(true);
        userRepository.save(user);
        return "Valid";
    }
}
