package tech.codehunt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

import tech.codehunt.dao.UserDao;
import tech.codehunt.dto.UserRequestDto;
import tech.codehunt.model.User;

@Service
public class UserService {

    private static final String ROLE_PASSENGER = "PASSENGER";
    private static final String ROLE_DRIVER = "DRIVER";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Autowired
    private UserDao userDao;

    @Transactional
    public User registerUser(UserRequestDto request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (!email.matches(EMAIL_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email format is invalid");
        }
        if (request.getPassword() == null || request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role is required");
        }

        String role = request.getRole().trim().toUpperCase();

        if (userDao.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email is already registered: " + email);
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(request.getPassword());
        user.setRole(role);

        try {
            return userDao.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email is already registered: " + email);
        }
    }

    public User loginUser(String email, String password) {
        if (email == null || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }
        User user = userDao.findByEmail(email.trim().toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    // NAYA METHOD: Admin dwara user delete karne ke liye
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}