package com.coderintuition.CoderIntuition.pojos.request.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateReadingRequest {
    private Long id;
    private ReadingDto reading;
}
