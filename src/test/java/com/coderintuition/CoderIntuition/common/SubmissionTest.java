//package com.coderintuition.CoderIntuition.common;
//
//import com.coderintuition.CoderIntuition.config.AppProperties;
//import com.coderintuition.CoderIntuition.enums.Language;
//import com.coderintuition.CoderIntuition.models.Problem;
//import com.coderintuition.CoderIntuition.models.Solution;
//import com.coderintuition.CoderIntuition.models.TestCase;
//import com.coderintuition.CoderIntuition.pojos.request.JZSubmissionRequestDto;
//import com.coderintuition.CoderIntuition.pojos.response.JzSubmissionCheckResponse;
//import com.coderintuition.CoderIntuition.repositories.ProblemRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Ignore;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.util.List;
//
//@Ignore
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Slf4j
//public class SubmissionTest {
//
//    @Autowired
//    private AppProperties appProperties;
//
//    @Autowired
//    private ProblemRepository problemRepository;
//
//    @Test
//    void testSubmittingProblems() throws IOException {
//        List<Problem> problems = problemRepository.findAll();
//        for (Problem problem : problems) {
//            for (Language language : Language.values()) {
//                // wrap the code into the submission template
//                CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
//                String functionName = Utils.getFunctionName(language, problem.getCode(language));
//                String primarySolution = problem.getSolutions().stream().filter(Solution::getIsPrimary).findFirst().orElseThrow().getCode(language);
//                // fill in the submission template with the arguments/return type for this test run
//                String code = filler.getSubmissionCode(language, primarySolution, primarySolution,
//                    functionName, problem.getOrderedArguments(), problem.getReturnType());
//
//                // setup stdin
//                StringBuilder stdin = new StringBuilder();
//                for (TestCase testCase : problem.getTestCases()) {
//                    stdin.append(testCase.getInput()).append("\n");
//                    stdin.append(Constants.IO_SEPARATOR);
//                }
//
//                // create request to JudgeZero
//                JZSubmissionRequestDto jzSubmissionRequestDto = new JZSubmissionRequestDto();
//                jzSubmissionRequestDto.setSourceCode(code);
//                jzSubmissionRequestDto.setLanguageId(Utils.getLanguageId(language));
//                jzSubmissionRequestDto.setStdin(stdin.toString());
//
//                JzSubmissionCheckResponse jzSubmissionCheckResponse = Utils.submitToJudgeZeroSync(jzSubmissionRequestDto, appProperties);
//                log.info(jzSubmissionCheckResponse.toString());
//            }
//        }
//    }
//}
