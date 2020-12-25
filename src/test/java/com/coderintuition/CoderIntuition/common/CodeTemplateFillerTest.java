package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.models.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
        Argument arg1 = new Argument(ArgumentType.STRING, UnderlyingType.NONE, UnderlyingType.NONE);
        List<Argument> args = Collections.singletonList(arg1);
        ReturnType returnType = new ReturnType(ArgumentType.STRING, UnderlyingType.NONE, UnderlyingType.NONE, true);
        try {
            String code = filler.getTestRunCode(language, userCode, solutionCode, functionName, args, returnType);
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
        Argument arg1 = new Argument(ArgumentType.STRING, UnderlyingType.NONE, UnderlyingType.NONE);
        List<Argument> args = Collections.singletonList(arg1);
        ReturnType returnType = new ReturnType(ArgumentType.STRING, UnderlyingType.NONE, UnderlyingType.NONE, true);
        try {
            String code = filler.getTestRunCode(language, userCode, solutionCode, functionName, args, returnType);
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
        Argument arg1 = new Argument(ArgumentType.LIST, UnderlyingType.STRING, UnderlyingType.NONE);
        List<Argument> args = Collections.singletonList(arg1);
        ReturnType returnType = new ReturnType(ArgumentType.STRING, UnderlyingType.NONE, UnderlyingType.NONE, true);
        try {
            String code = filler.getTestRunCode(language, userCode, solutionCode, functionName, args, returnType);
            System.out.println(code);
            assertTrue(code.contains("switch (\"String\") {"));
            assertTrue(code.contains("String userResult = new Solution().test(stringToList1D0([\"hi\", \"there\"]));"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String convertString(String str) throws Exception {
        if (str.charAt(0) != '\"' || str.charAt(str.length() - 1) != '\"') throw new Exception();
        return str.substring(1, str.length() - 1);
    }

    @Test
    void javaDictionary() {
        String str1 = "[[true], [false , true], [false ,true, true]]";
        String str = "[[\"\"], [\"b\" , \"c\"], [\"d\" ,\"e\", \"f\"]]";
        try {
            List<List<String>> convertedListOfLists = new ArrayList<>();
            String[] lstStrs = str.substring(2, str.length() - 2).split("] *, *\\[");
            for (String lstStr : lstStrs) {
                String[] innerLstStrs = lstStr.split("\\s* *, *\\s*");
                List<String> innerLst = new ArrayList<>();
                for (String innerLstStr : innerLstStrs) {
                    if (innerLstStr.charAt(0) != '\"' || innerLstStr.charAt(innerLstStr.length() - 1) != '\"') {
                        throw new Exception();
                    }
                    innerLst.add(innerLstStr.substring(1, innerLstStr.length() - 1));
                }
                convertedListOfLists.add(innerLst);
            }
            System.out.println(convertedListOfLists);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
