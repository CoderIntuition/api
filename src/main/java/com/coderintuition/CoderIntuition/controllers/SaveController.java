package com.coderintuition.CoderIntuition.controllers;

import com.coderintuition.CoderIntuition.enums.Language;
import com.coderintuition.CoderIntuition.exceptions.RecordNotFoundException;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Save;
import com.coderintuition.CoderIntuition.models.User;
import com.coderintuition.CoderIntuition.pojos.request.RunRequestDto;
import com.coderintuition.CoderIntuition.pojos.request.SaveRequest;
import com.coderintuition.CoderIntuition.pojos.response.SaveResponse;
import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
import com.coderintuition.CoderIntuition.repositories.SaveRepository;
import com.coderintuition.CoderIntuition.repositories.UserRepository;
import com.coderintuition.CoderIntuition.security.CurrentUser;
import com.coderintuition.CoderIntuition.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.Optional;

@RestController
@Slf4j
public class SaveController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SaveRepository saveRepository;

    @MessageMapping("/secured/{userId}/save")
    public void createSave(@DestinationVariable Long userId, Message<SaveRequest> message) {
        SaveRequest saveRequest = message.getPayload();

        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User with ID" + userId + " not found."));
        Problem problem = problemRepository.findById(saveRequest.getProblemId()).orElseThrow();

        Optional<Save> optionalSave = saveRepository.findByUserAndProblem(user, problem);
        Save save;
        if (optionalSave.isPresent()) {
            save = optionalSave.get();
        } else {
            save = new Save();
            save.setUser(user);
            save.setProblem(problem);
        }
        save.setPythonCode(saveRequest.getPythonCode());
        save.setJavaCode(saveRequest.getJavaCode());
        save.setJavascriptCode(saveRequest.getJavascriptCode());
        saveRepository.save(save);
    }

    @GetMapping("/save/{problemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SaveResponse getSave(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long problemId) {
        User user = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RecordNotFoundException("User with ID" + userPrincipal.getId() + " not found."));
        Problem problem = problemRepository.findById(problemId).orElseThrow();
        Save save = saveRepository.findByUserAndProblem(user, problem).orElseThrow();

        return SaveResponse.fromSave(save);
    }
}
