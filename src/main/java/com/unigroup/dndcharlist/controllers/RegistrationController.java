package com.unigroup.dndcharlist.controllers;

import com.unigroup.dndcharlist.dao.UserDAO;
import com.unigroup.dndcharlist.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    @Autowired
    private UserDAO userDAO;

    @GetMapping("/")
    public String hello() {
        return "Hello";
    }
    @GetMapping("/user")
    public String registration(@RequestParam String login, @RequestParam String password, @RequestParam String passwordConfrim) {
        if (!password.equals(passwordConfrim))
        {
            return "error";
        }

        try {
            User user = new User();
            user.setName(login);
            user.setPassword(password);
            userDAO.save(user);
        }
        catch (Exception excep)
        {
            return "error";
        }
        return "Correct";
    }
}
