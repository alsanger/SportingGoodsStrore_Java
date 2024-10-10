package com.example.exam_java_salanhin.services.user;

import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserValidationService userValidationService;

    public User authenticateUser(String login, String password) {
        User user = userRepository.findByLogin(login);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public ModelAndView saveUser(User user) {
        ModelAndView modelAndView = new ModelAndView();

        if (userValidationService.isUserExists(user)) {
            modelAndView.addObject("error", "A user with this login, email or phone number already exists.");
            modelAndView.setViewName("user/createUser");
            return modelAndView;
        }

        String validationError = userValidationService.validateUserData(user);
        if (validationError != null) {
            modelAndView.addObject("error", validationError);
            modelAndView.setViewName("registration");
            return modelAndView;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public ModelAndView updateUser(Long id, User updatedUser) {
        ModelAndView modelAndView = new ModelAndView();

        if (userValidationService.isUserExists(updatedUser)) {
            modelAndView.addObject("error", "A user with this email or phone number already exists.");
            modelAndView.setViewName("user/editUser");
            return modelAndView;
        }

        String validationError = userValidationService.validateUserData(updatedUser);
        if (validationError != null) {
            modelAndView.addObject("error", validationError);
            modelAndView.setViewName("user/editUser");
            return modelAndView;
        }

        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            existingUser.setCity(updatedUser.getCity());
            existingUser.setCountry(updatedUser.getCountry());

            userRepository.save(existingUser);
            modelAndView.setViewName("redirect:/user/profileUser");
        } else {
            modelAndView.addObject("error", "Failed to update user.");
            modelAndView.setViewName("user/editUser");
        }

        return modelAndView;
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User changeUserRole(Long id, User.Role newRole) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(newRole);
            return userRepository.save(user);
        } else {
            return null;
        }
    }
}