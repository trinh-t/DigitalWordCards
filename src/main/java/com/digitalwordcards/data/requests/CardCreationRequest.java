package com.digitalwordcards.data.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class CardCreationRequest {
    private String text;
    private MultipartFile data = null;
    private int module;
    private LocalDate displayDate = LocalDate.now();
}
