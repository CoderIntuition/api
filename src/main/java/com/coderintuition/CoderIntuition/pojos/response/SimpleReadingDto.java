package com.coderintuition.CoderIntuition.pojos.response;

import lombok.Data;

@Data
public class SimpleReadingDto {
    private Long id;
    private String name;
    private String urlName;
    private Boolean plusOnly;
}
