package com.unigroup.dndcharlist.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
@Entity
public class Role implements GrantedAuthority {
    @Id
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
    @Override
    public String getAuthority() {
        return null;
    }
    public Role(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }
}
