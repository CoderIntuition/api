package com.coderintuition.CoderIntuition.pojos.response;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDto {
    private String name;
    private List<SimpleProblemResponse> results;
}
