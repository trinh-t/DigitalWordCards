package com.digitalwordcards.data.requests;

import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class CardModificationRequest {
    private UUID id;
    private Optional<Integer> module = Optional.empty();
    private Optional<String> text = Optional.empty();
    private Optional<byte[]> data = Optional.empty();
}
