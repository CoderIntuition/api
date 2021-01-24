package com.coderintuition.CoderIntuition.pojos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadingsResponse {
    private int totalPages;
    private int totalElements;
    private List<SimpleReadingDto> readings;
}
