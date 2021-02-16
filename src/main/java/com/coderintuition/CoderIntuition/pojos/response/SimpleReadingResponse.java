package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.models.Reading;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SimpleReadingResponse {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String urlName;

    @NotNull
    private Boolean plusOnly;

    @NotNull
    private Boolean isQuiz;

    public static SimpleReadingResponse fromReading(Reading reading) {
        SimpleReadingResponse simpleReadingResponse = new SimpleReadingResponse();
        simpleReadingResponse.setId(reading.getId());
        simpleReadingResponse.setName(reading.getName());
        simpleReadingResponse.setUrlName(reading.getUrlName());
        simpleReadingResponse.setPlusOnly(reading.getPlusOnly());
        simpleReadingResponse.setIsQuiz(reading.getIsQuiz());
        return simpleReadingResponse;
    }
}
