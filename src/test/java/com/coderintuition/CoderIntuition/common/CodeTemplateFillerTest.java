package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.models.Language;
import com.coderintuition.CoderIntuition.pojos.general.Argument;
import com.coderintuition.CoderIntuition.pojos.general.ArgumentType;
import com.coderintuition.CoderIntuition.pojos.general.ReturnType;
import com.coderintuition.CoderIntuition.pojos.general.UnderlyingType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

public class CodeTemplateFillerTest {
    @Test
    void javaArgString() {
        CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
        Language language = Language.JAVA;
        String userCode = "";
        String solutionCode = "";
        String functionName = "test";
        Argument arg1 = new Argument(ArgumentType.STRING, UnderlyingType.NONE, "\"hi\"");
        List<Argument> args = Collections.singletonList(arg1);
        ReturnType returnType = new ReturnType(ArgumentType.STRING, UnderlyingType.NONE);
        try {
            String code = filler.fill(language, userCode, solutionCode, functionName, args, returnType);
            assertTrue(code.contains("String userResult = new Solution().test(\"hi\");"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void javaArgEmptyString() {
        CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
        Language language = Language.JAVA;
        String userCode = "";
        String solutionCode = "";
        String functionName = "test";
        Argument arg1 = new Argument(ArgumentType.STRING, UnderlyingType.NONE, "\"\"");
        List<Argument> args = Collections.singletonList(arg1);
        ReturnType returnType = new ReturnType(ArgumentType.STRING, UnderlyingType.NONE);
        try {
            String code = filler.fill(language, userCode, solutionCode, functionName, args, returnType);
            assertTrue(code.contains("String userResult = new Solution().test(\"\");"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void javaArgStringList() {
        CodeTemplateFiller filler = CodeTemplateFiller.getInstance();
        Language language = Language.JAVA;
        String userCode = "";
        String solutionCode = "";
        String functionName = "test";
        Argument arg1 = new Argument(ArgumentType.LIST, UnderlyingType.STRING, "[\"hi\", \"there\"]");
        List<Argument> args = Collections.singletonList(arg1);
        ReturnType returnType = new ReturnType(ArgumentType.STRING, UnderlyingType.NONE);
        try {
            String code = filler.fill(language, userCode, solutionCode, functionName, args, returnType);
            System.out.println(code);
            assertTrue(code.contains("switch (\"String\") {"));
            assertTrue(code.contains("String userResult = new Solution().test(stringToList1D0([\"hi\", \"there\"]));"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
