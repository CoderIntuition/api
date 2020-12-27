package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.models.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    void pythonFunctionName() {
        String code = "\n\n\tdef test_test_test():";
        String functionName = Utils.getFunctionName(Language.PYTHON, code);
        assertEquals(functionName, "test_test_test");
    }

    @Test
    void javaFunctionName() {
        String code = "class Solution {\n\tvoid testTestTest() {";
        String functionName = Utils.getFunctionName(Language.JAVA, code);
        assertEquals(functionName, "testTestTest");
    }
}
