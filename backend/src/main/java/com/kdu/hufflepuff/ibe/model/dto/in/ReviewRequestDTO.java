package com.kdu.hufflepuff.ibe.model.dto.in;


import lombok.Data;

@Data
public class ReviewRequestDTO {

    private int rating;
    private String comment;
}

