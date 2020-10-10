package com.coderintuition.CoderIntuition.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static int getLanguageId(String language) {
        if (language.equalsIgnoreCase("python")) {
            return 71;
        } else if (language.equalsIgnoreCase("java")) {
            return 62;
        } else if (language.equalsIgnoreCase("javascript")) {
            return 63;
        }
        return -1;
    }

    public static String getFunctionName(String defaultCode) {
        return defaultCode.substring(4, defaultCode.indexOf("("));
    }

    public static String formatParam(String param, String language) {
        if (language.equalsIgnoreCase("java")) {

        } else if (language.equalsIgnoreCase("python")) {

        }
        return param;
    }
}
