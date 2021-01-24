package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Reading;
import com.coderintuition.CoderIntuition.pojos.response.ProblemsResponse;
import com.coderintuition.CoderIntuition.pojos.response.ReadingsResponse;
import com.coderintuition.CoderIntuition.pojos.response.SimpleProblemDto;
import com.coderintuition.CoderIntuition.pojos.response.SimpleReadingDto;
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
import java.util.Optional;

@RestController
public class ReadingController {

    @Autowired
    ReadingRepository readingRepository;

    private List<SimpleReadingDto> simplifyReadings(List<Reading> readings) {
        List<SimpleReadingDto> simpleReadingDtos = new ArrayList<>();
        for (Reading reading : readings) {
            SimpleReadingDto simpleReadingDto = new SimpleReadingDto();
            simpleReadingDto.setId(reading.getId());
            simpleReadingDto.setName(reading.getName());
            simpleReadingDto.setUrlName(reading.getUrlName());
            simpleReadingDto.setPlusOnly(reading.getPlusOnly());
            simpleReadingDtos.add(simpleReadingDto);
        }
        return simpleReadingDtos;
    }

    @GetMapping(value = "/readings", params = {"page", "size"})
    public ReadingsResponse getReadings(@RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reading> readings = readingRepository.findAll(pageable);
        return new ReadingsResponse(readings.getTotalPages(), (int) readings.getTotalElements(), simplifyReadings(readings.toList()));
    }

    @GetMapping("/reading/id/{id}")
    public Optional<Reading> getReadingById(@PathVariable Long id) {
        return readingRepository.findById(id);
    }

    @GetMapping("/reading/{urlName}")
    public Optional<Reading> getReadingByUrlname(@PathVariable String urlName) {
        return readingRepository.findByUrlName(urlName);
    }
}
