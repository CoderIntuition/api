package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.models.Reading;
import com.coderintuition.CoderIntuition.pojos.response.ReadingsResponse;
import com.coderintuition.CoderIntuition.pojos.response.SimpleReadingResponse;
import com.coderintuition.CoderIntuition.repositories.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReadingController {

    @Autowired
    ReadingRepository readingRepository;

    private List<SimpleReadingResponse> simplifyReadings(List<Reading> readings) {
        List<SimpleReadingResponse> simpleReadingResponses = new ArrayList<>();
        for (Reading reading : readings) {
            simpleReadingResponses.add(SimpleReadingResponse.fromReading(reading));
        }
        return simpleReadingResponses;
    }

    @GetMapping(value = "/readings", params = {"page", "size"})
    public ReadingsResponse getReadings(@RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reading> readings = readingRepository.findAll(pageable);
        return new ReadingsResponse(readings.getTotalPages(), (int) readings.getTotalElements(), simplifyReadings(readings.toList()));
    }

    @GetMapping("/reading/id/{id}")
    public Reading getReadingById(@PathVariable Long id) {
        return readingRepository.findById(id).orElseThrow();
    }

    @GetMapping("/reading/{urlName}")
    public Reading getReadingByUrlname(@PathVariable String urlName) {
        return readingRepository.findByUrlName(urlName).orElseThrow();
    }
}
