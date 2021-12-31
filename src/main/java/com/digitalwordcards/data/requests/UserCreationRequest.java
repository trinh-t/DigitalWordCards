package com.digitalwordcards.data.requests;

import lombok.Data;

@Data
public class UserCreationRequest {
    private String name, email, clazz, password, role;
}
