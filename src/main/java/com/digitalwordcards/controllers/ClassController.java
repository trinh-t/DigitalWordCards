package com.digitalwordcards.controllers;
import java.util.List;
import com.digitalwordcards.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/class")
@AllArgsConstructor
public class ClassController {

    private final UserService userService;

    @GetMapping("{clazz}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public List<String> getViewedPercentageByClass(@PathVariable String clazz) {
        return userService.getViewedPercentageByClass(clazz);
    }
}
