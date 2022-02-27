package com.digitalwordcards.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.digitalwordcards.data.requests.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@Table(name = "application_user")
public class User implements UserDetails {


    @Id
    private String email;

    private String name;
    private String clazz;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Card> viewedCards;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    public static User fromDto(UserDto userDto) {
        final var user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        user.setClazz(userDto.getClazz());

        return user;
    }

    @Transient
    public UserDto toDto() {

        final var userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setRole(role);
        userDto.setClazz(clazz);
        userDto.setPassword(password);

        return userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);

    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



}
