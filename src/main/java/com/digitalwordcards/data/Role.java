package com.digitalwordcards.data;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public enum Role implements GrantedAuthority {
    ADMIN, TEACHER, STUDENT;

    public Collection<Role> authorities() {
        final Role role = this;
        return new ArrayList<>() {{
            add(role);
        }};
    }

    @Override
    public String getAuthority() {
        return this.name();
    }

    public boolean canBeGrantedBy(GrantedAuthority grantedAuthority) {
        switch (getAuthority()) {
            case "ADMIN":
            case "TEACHER":
                return grantedAuthority.getAuthority().equals("ADMIN");
            case "STUDENT":
                return grantedAuthority.getAuthority().equals("ADMIN") || grantedAuthority.getAuthority().equals("TEACHER");
            default:
                return false;
        }
    }
}
