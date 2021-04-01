package com.coderintuition.CoderIntuition.pojos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedActivitiesResponse {
    List<String> completedProblems;
    List<String> completedReadings;
}
