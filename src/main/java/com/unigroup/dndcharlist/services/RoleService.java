package com.unigroup.dndcharlist.services;

import com.unigroup.dndcharlist.entities.Role;
import com.unigroup.dndcharlist.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }
}
