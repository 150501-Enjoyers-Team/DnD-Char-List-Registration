package com.unigroup.dndcharlist.dao;

import com.unigroup.dndcharlist.entities.Role;
import com.unigroup.dndcharlist.entities.User;
import com.unigroup.dndcharlist.repositories.RoleRepository;
import com.unigroup.dndcharlist.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDAO{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean isExsist(String username)
    {
        return userRepository.findByName(username).isPresent();
    }

    public void save(User user) throws Exception
    {
        if (isExsist(user.getName())) throw new Exception("Error!");

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        userRepository.save(user);

    }


}
