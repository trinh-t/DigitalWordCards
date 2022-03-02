package com.digitalwordcards.data.requests;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CardDto {

    private UUID id;
    private String text;
    private String data;
    private int module;
    private LocalDate displayDate = LocalDate.now();
}
