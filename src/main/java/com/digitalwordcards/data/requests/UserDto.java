package com.digitalwordcards.data.requests;

import com.digitalwordcards.data.Role;
import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String name;
    private String clazz;
    private String password;
    private Role role;
}
