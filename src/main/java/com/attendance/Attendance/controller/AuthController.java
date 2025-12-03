package com.attendance.Attendance.controller;

import com.attendance.Attendance.Repository.UserRepository;
import com.attendance.Attendance.entity.Users;
import com.attendance.Attendance.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String login(@RequestBody Users users) throws Exception {
        return authService.login(users.getEmail(), users.getPassword());
    }


    @PostMapping("/register")
    public String register(@RequestBody Users users) {
        Optional<Users> user = userRepository.findByEmail(users.getEmail());
        if(user.isPresent()) return "Email Already Registered";
        userRepository.save(users);
        return "Registration successful!";
    }
}
