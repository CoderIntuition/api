package com.coderintuition.CoderIntuition.pojos.response;

import com.coderintuition.CoderIntuition.enums.ActivityType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDto {
    private ActivityType activityType;
    private String problemName;
    private String problemUrl;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date createdDate;
}
