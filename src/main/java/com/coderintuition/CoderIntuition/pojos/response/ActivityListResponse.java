package com.coderintuition.CoderIntuition.pojos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityListResponse {
    private int totalPages;
    private List<ActivityResponse> activities;
}
