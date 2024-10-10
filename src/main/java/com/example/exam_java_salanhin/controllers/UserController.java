package com.example.exam_java_salanhin.controllers;

import com.example.exam_java_salanhin.models.User;
import com.example.exam_java_salanhin.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/public/user/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/public/user/login")
    public ModelAndView loginUser(@RequestParam("login") String login,
                                  @RequestParam("password") String password) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.authenticateUser(login, password);
        if (user == null) {
            modelAndView.addObject("error", "Invalid login or password");
            modelAndView.setViewName("user/login");
            return modelAndView;
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("User authenticated: " + authentication.getName());

        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }
//    @PostMapping("/public/user/login")
//    public ModelAndView loginUser(@RequestParam("login") String login,
//                                  @RequestParam("password") String password) {
//        ModelAndView modelAndView = new ModelAndView();
//
//        User user = userService.authenticateUser(login, password);
//        if (user == null) {
//            modelAndView.addObject("error", "Invalid login or password");
//            modelAndView.setViewName("user/login");
//            return modelAndView;
//        }
//
//        //Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        modelAndView.setViewName("redirect:/");
//        return modelAndView;
//    }




    @GetMapping("/public/user/create")
    public String home() {
        return "user/createUser";
    }

    @PostMapping("/public/user/create")
    public String createUser(@ModelAttribute User user) {

        System.out.println("Received user: " + user);

        user.setRole(User.Role.USER);
        userService.saveUser(user);
        return "redirect:/";
    }

    @GetMapping("/public/user/profileUser")
    public ModelAndView profileUser(@RequestParam(required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView("user/profileUser");
        User user;

        if (id != null) {
            user = userService.getUserById(id).orElse(null);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = (User) authentication.getPrincipal(); // Получаем текущего пользователя
        }

        if (user != null) {
            modelAndView.addObject("user", user);
        } else {
            modelAndView.addObject("error", "User not found");
            modelAndView.setViewName("error"); // или другая страница ошибки
        }
        return modelAndView;
    }

    @GetMapping("/public/user/update")
    public ModelAndView updateUserForm(@RequestParam(required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView("user/updateUser");
        User user;

        if (id != null) {
            user = userService.getUserById(id).orElse(null);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = (User) authentication.getPrincipal();
        }

        if (user != null) {
            modelAndView.addObject("user", user);
        } else {
            modelAndView.addObject("error", "User not found");
            modelAndView.setViewName("user/updateUser");
        }
        return modelAndView;
    }

    @PostMapping("/public/user/update")
    public ModelAndView updateUser(@ModelAttribute User updatedUser) {
        return userService.updateUser(updatedUser.getUserId(), updatedUser);
    }

}