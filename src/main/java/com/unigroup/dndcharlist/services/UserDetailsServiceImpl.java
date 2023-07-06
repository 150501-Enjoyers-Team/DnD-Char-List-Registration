package com.unigroup.dndcharlist.services;

import com.unigroup.dndcharlist.entities.User;
import com.unigroup.dndcharlist.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByName(username);

        if (userOpt.isEmpty()) throw new UsernameNotFoundException("User not found");
        User user = userOpt.get();

        return user;
    }
}
