package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.models.Argument;
import com.coderintuition.CoderIntuition.models.Language;
import com.coderintuition.CoderIntuition.models.ReturnType;
import com.coderintuition.CoderIntuition.models.UnderlyingType;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CodeTemplateFiller {
    private static CodeTemplateFiller instance = null;
    private String pythonTemplate;
    private String pythonTree;
    private String pythonLinkedList;
    private String pythonString;
    private String javaTemplate;
    private String javaList;
    private String javaArray2D;
    private String javaListOfLists;
    private String javaDictionary;
    private String javaTree;
    private String javaLinkedList;
    private String javaString;
    private String javaScriptTemplate;

    public static CodeTemplateFiller getInstance() {
        if (instance == null) {
            instance = new CodeTemplateFiller();
        }
        return instance;
    }

    private CodeTemplateFiller() {
        try {
            pythonTemplate = fileToString("python/pythonTestRun.txt");
            pythonTree = fileToString("python/pythonTree.txt");
            pythonLinkedList = fileToString("python/pythonLinkedList.txt");
            pythonString = fileToString("python/pythonString.txt");
            javaTemplate = fileToString("java/javaTestRun.txt");
            javaList = fileToString("java/javaList.txt");
            javaArray2D = fileToString("java/javaArray2D.txt");
            javaListOfLists = fileToString("java/javaListOfLists.txt");
            javaDictionary = fileToString("java/javaDictionary.txt");
            javaTree = fileToString("java/javaTree.txt");
            javaLinkedList = fileToString("java/javaLinkedList.txt");
            javaString = fileToString("java/javaString.txt");
            javaScriptTemplate = fileToString("javascript/javascriptTestRun.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String fillTestRun(Language language, String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) {
        switch (language) {
            case PYTHON:
                return fillPython(userCode, solutionCode, functionName, args, returnType);
            case JAVA:
                return fillJava(userCode, solutionCode, functionName, args, returnType);
            case JAVASCRIPT:
                return fillJavaScript(userCode, solutionCode, functionName, args, returnType);
            default:
                return "";
        }
    }

    private String fileToString(String fileName) throws IOException {
        Resource resource = new ClassPathResource("templates/" + fileName);
        File file = resource.getFile();
        return FileUtils.readFileToString(file, "UTF-8");
    }

    private String fillPython(String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) {
        boolean usingTree = false;
        boolean usingLinkedList = false;
        boolean usingString = false;
        StringBuilder argsStr = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            Argument arg = args.get(i);
            switch (arg.getType()) {
                case TREE:
                    argsStr.append("___list_to_tree(ast.literal_eval(input[").append(i).append("]))");
                    usingTree = true;
                    break;
                case LINKED_LIST:
                    argsStr.append("___list_to_linked_list(ast.literal_eval(input[").append(i).append("]))");
                    usingLinkedList = true;
                    break;
                case STRING:
                    argsStr.append("___string_to_string(ast.literal_eval(input[").append(i).append("]))");
                    usingString = true;
                    break;
                case INTEGER:
                case FLOAT:
                case LIST:
                case ARRAY_2D:
                case DICTIONARY:
                    argsStr.append("ast.literal_eval(input[").append(i).append("])");
                    break;
            }
            if (i < args.size() - 1) {
                argsStr.append(", ");
            }
        }

        StringBuilder definitionCode = new StringBuilder();
        if (usingTree) {
            definitionCode.append(pythonTree);
        }
        if (usingLinkedList) {
            definitionCode.append(pythonLinkedList);
        }
        if (usingString) {
            definitionCode.append(pythonString);
        }

        StringBuilder equalsCode = new StringBuilder();
        StringBuilder userResultFormatCode = new StringBuilder();
        StringBuilder solResultFormatCode = new StringBuilder();
        switch (returnType.getType()) {
            case LIST:
                if (returnType.getOrderMatters()) {
                    equalsCode.append("if user_result == sol_result:");
                } else {
                    equalsCode.append("if len(user_result) == len(sol_result) and sorted(user_result) == sorted(sol_result):");
                }
                userResultFormatCode.append("user_result_str = user_result");
                solResultFormatCode.append("sol_result_str = sol_result");
                break;
            case TREE:
                equalsCode.append("if ___tree_same(user_result, sol_result):");
                userResultFormatCode.append("user_result_str = ___tree_to_list(user_result)");
                solResultFormatCode.append("sol_result_str = ___tree_to_list(sol_result)");
                break;
            case LINKED_LIST:
                equalsCode.append("if ___linked_list_same(user_result, sol_result):");
                userResultFormatCode.append("user_result_str = ___linked_list_to_list(user_result)");
                solResultFormatCode.append("sol_result_str = ___linked_list_to_list(sol_result)");
                break;
            case STRING:
                equalsCode.append("if user_result == sol_result:");
                userResultFormatCode.append("user_result_str = \"\\\"\" + user_result + \"\\\"\"");
                solResultFormatCode.append("sol_result_str = \"\\\"\" + sol_result + \"\\\"\"");
                break;
            case INTEGER:
                // fall through
            case FLOAT:
                // fall through
            case ARRAY_2D:
                // fall through
            case DICTIONARY:
                equalsCode.append("if user_result == sol_result:");
                userResultFormatCode.append("user_result_str = user_result");
                solResultFormatCode.append("sol_result_str = sol_result");
                break;
        }

        return pythonTemplate
                .replaceAll("\\$\\{definitionCode}", definitionCode.toString())
                .replaceAll("\\$\\{userCode}", userCode)
                .replaceAll("\\$\\{solutionCode}", solutionCode.replace(functionName, functionName + "_sol"))
                .replaceAll("\\$\\{functionName}", functionName)
                .replaceAll("\\$\\{args}", argsStr.toString())
                .replaceAll("\\$\\{userResultFormatCode}", userResultFormatCode.toString())
                .replaceAll("\\$\\{solResultFormatCode}", solResultFormatCode.toString())
                .replaceAll("\\$\\{equalsCode}", equalsCode.toString());
    }

    private String fillJava(String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) {
        boolean usingTree = false;
        boolean usingLinkedList = false;
        boolean usingString = false;
        StringBuilder setupCode = new StringBuilder();
        StringBuilder argsCode = new StringBuilder();

        for (int i = 0; i < args.size(); i++) {
            Argument arg = args.get(i);
            switch (arg.getType()) {
                case LIST:
                    setupCode.append(getJavaListCode(arg, i));
                    argsCode.append("stringToList").append(i).append("(input.get(").append(i).append("))");
                    break;
                case ARRAY_2D:
                    setupCode.append(getJavaArray2DCode(arg, i));
                    argsCode.append("stringToArray2D").append(i).append("(input.get(").append(i).append("))");
                    break;
                case LIST_OF_LISTS:
                    setupCode.append(getJavaListOfListsCode(arg, i));
                    argsCode.append("stringToListOfLists").append(i).append("(input.get(").append(i).append("))");
                    break;
                case DICTIONARY:
                    setupCode.append(getJavaDictionaryCode(arg, i));
                    argsCode.append("stringToDictionary").append(i).append("(input.get(").append(i).append("))");
                    break;
                case TREE:
                    setupCode.append(getJavaListCode(arg, i));
                    argsCode.append("TreeNode.listToTree(stringToList").append(i).append("(input.get(").append(i).append("))");
                    usingTree = true;
                    break;
                case LINKED_LIST:
                    setupCode.append(getJavaListCode(arg, i));
                    argsCode.append("ListNode.listToLinkedList(stringToList").append(i).append("(input.get(").append(i).append("))");
                    usingLinkedList = true;
                    break;
                case STRING:
                    argsCode.append("stringToString(input.get(").append(i).append("))");
                    usingString = true;
                    break;
                case INTEGER:
                    argsCode.append("Integer.parseInt(input.get(").append(i).append("))");
                    break;
                case FLOAT:
                    argsCode.append("Double.parseDouble(input.get(").append(i).append("))");
                    break;
                case BOOLEAN:
                    argsCode.append("Boolean.parseBoolean(input.get(").append(i).append("))");
                    break;
            }
            if (i < args.size() - 1) {
                argsCode.append(", ");
            }
        }

        StringBuilder definitionCode = new StringBuilder();
        if (usingTree) {
            definitionCode.append(javaTree);
        }
        if (usingLinkedList) {
            definitionCode.append(javaLinkedList);
        }
        if (usingString) {
            setupCode.append(javaString);
        }

        StringBuilder equalsCode = new StringBuilder();
        StringBuilder userResultFormatCode = new StringBuilder();
        StringBuilder solResultFormatCode = new StringBuilder();
        switch (returnType.getType()) {
            case LIST:
                if (returnType.getOrderMatters()) {
                    equalsCode.append("if (userResult.equals(solResult)) {").append("\n");
                } else {
                    equalsCode.append("if (userResult.size() == solResult.size() && Collections.sort(userResult).equals(Collections.sort(solResult))) {").append("\n");
                }
                userResultFormatCode.append("String userResultStr = userResult == null ? \"null\" : userResult.toString();").append("\n");
                solResultFormatCode.append("String solResultStr = solResult == null ? \"null\" : solResult.toString();").append("\n");
                break;
            case ARRAY_2D:
                equalsCode.append("if (Arrays.deepEquals(userResult, solResult)) {").append("\n");
                userResultFormatCode.append("String userResultStr = Arrays.deepToString(userResult);").append("\n");
                solResultFormatCode.append("String solResultStr = Arrays.deepToString(solResult);").append("\n");
                break;
            case LIST_OF_LISTS:
                equalsCode.append("if (userResult.equals(solResult)) {").append("\n");
                userResultFormatCode.append("String userResultStr = userResult == null ? \"null\" : userResult.toString();").append("\n");
                solResultFormatCode.append("String solResultStr = solResult == null ? \"null\" : solResult.toString();").append("\n");
                break;
            case DICTIONARY:
                equalsCode.append("if (userResult.equals(solResult)) {").append("\n");
                userResultFormatCode.append("String userResultStr = userResult.keySet().stream()").append("\n");
                solResultFormatCode.append("String solResultStr = solResult.keySet().stream()").append("\n");
                if (returnType.getUnderlyingType() == UnderlyingType.STRING) {
                    userResultFormatCode.append("\t.map(key -> \"\\\"\" + key + \"\\\": \"");
                    solResultFormatCode.append("\t.map(key -> \"\\\"\" + key + \"\\\": \"");
                } else {
                    userResultFormatCode.append("\t.map(key -> key + \": \"");
                    solResultFormatCode.append("\t.map(key -> key + \": \"");
                }
                if (returnType.getUnderlyingType2() == UnderlyingType.STRING) {
                    userResultFormatCode.append("+ \"\\\"\" + map.get(key) + \"\\\"\")").append("\n");
                    solResultFormatCode.append("+ \"\\\"\" + map.get(key) + \"\\\"\")").append("\n");
                } else {
                    userResultFormatCode.append("+ map.get(key))").append("\n");
                    solResultFormatCode.append("+ map.get(key))").append("\n");
                }
                userResultFormatCode.append(".collect(Collectors.joining(\", \", \"{\", \"}\"));").append("\n");
                solResultFormatCode.append(".collect(Collectors.joining(\", \", \"{\", \"}\"));").append("\n");
                break;
            case TREE:
                equalsCode.append("if (TreeNode.same(userResult, solResult)) {").append("\n");
                userResultFormatCode.append("String userResultStr = TreeNode.treeToList(userResult).toString();").append("\n");
                solResultFormatCode.append("String solResultStr = TreeNode.treeToList(solResult).toString();").append("\n");
                break;
            case LINKED_LIST:
                equalsCode.append("if (ListNode.same(userResult, solResult)) {").append("\n");
                userResultFormatCode.append("String userResultStr = ListNode.linkedListToList(userResult).toString();").append("\n");
                solResultFormatCode.append("String solResultStr = ListNode.linkedListToList(solResult).toString();").append("\n");
                break;
            case STRING:
                equalsCode.append("if (userResult.equals(solResult)) {").append("\n");
                userResultFormatCode.append("String userResultStr = \"\\\"\" + userResult + \"\\\"\";").append("\n");
                solResultFormatCode.append("String solResultStr = \"\\\"\" + solResult + \"\\\"\";").append("\n");
                break;
            case INTEGER:
                // fall through
            case FLOAT:
                // fall through
            case BOOLEAN:
                equalsCode.append("if (userResult == solResult) {").append("\n");
                userResultFormatCode.append("String userResultStr = \"\" + userResult;").append("\n");
                solResultFormatCode.append("String solResultStr = \"\" + solResult;").append("\n");
                break;
        }

        return javaTemplate
                .replaceAll("\\$\\{definitionCode}", definitionCode.toString())
                .replaceAll("\\$\\{userCode}", userCode)
                .replaceAll("\\$\\{solutionCode}", solutionCode)
                .replaceAll("\\$\\{setupCode}", setupCode.toString())
                .replaceAll("\\$\\{functionName}", functionName)
                .replaceAll("\\$\\{args}", argsCode.toString())
                .replaceAll("\\$\\{retType}", getJavaType(returnType))
                .replaceAll("\\$\\{userResultFormatCode}", userResultFormatCode.toString())
                .replaceAll("\\$\\{solResultFormatCode}", solResultFormatCode.toString())
                .replaceAll("\\$\\{equalsCode}", equalsCode.toString());
    }

    private String fillJavaScript(String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) {
        return "";
    }

    private String getJavaType(UnderlyingType type, boolean primitive) {
        switch (type) {
            case STRING:
                return "String";
            case INTEGER:
                return primitive ? "int" : "Integer";
            case FLOAT:
                return primitive ? "double" : "Double";
            case BOOLEAN:
                return primitive ? "boolean" : "Boolean";
            default:
                return "";
        }
    }

    private String getJavaType(ReturnType returnType) {
        switch (returnType.getType()) {
            case STRING:
                return "String";
            case INTEGER:
                return "int";
            case FLOAT:
                return "double";
            case BOOLEAN:
                return "boolean";
            case LIST:
                return "List<" + getJavaType(returnType.getUnderlyingType(), false) + ">";
            case ARRAY_2D:
                return getJavaType(returnType.getUnderlyingType(), true) + "[][]";
            case LIST_OF_LISTS:
                return "List<List<" + getJavaType(returnType.getUnderlyingType(), false) + ">>";
            case DICTIONARY:
                return "Map<" + getJavaType(returnType.getUnderlyingType(), false) + ", "
                        + getJavaType(returnType.getUnderlyingType2(), false) + ">";
            case TREE:
                return "TreeNode";
            case LINKED_LIST:
                return "ListNode";
            default:
                return "";
        }
    }

    private String getJavaListCode(Argument arg, int i) {
        String fillListFunction = javaList
                .replaceAll("\\$\\{i}", Integer.toString(i))
                .replaceAll("\\$\\{underlyingType}", getJavaType(arg.getUnderlyingType(), false));
        StringBuilder parseUnderlyingTypeCode = new StringBuilder();
        switch (arg.getUnderlyingType()) {
            case STRING:
                parseUnderlyingTypeCode.append("if (item.length() < 2 || !item.startsWith(\"\\\"\") || !item.endsWith(\"\\\"\")) {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception();").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                parseUnderlyingTypeCode.append("String val = item.substring(1, item.length() - 1);").append("\n");
                break;
            case INTEGER:
                parseUnderlyingTypeCode.append("Integer val = Integer.parseInt(item);").append("\n");
                break;
            case FLOAT:
                parseUnderlyingTypeCode.append("Double val = Double.parseDouble(item);").append("\n");
                break;
            case BOOLEAN:
                parseUnderlyingTypeCode.append("Boolean val = Boolean.parseBoolean(item);").append("\n");
                break;
        }
        return fillListFunction.replaceAll("\\$\\{parseUnderlyingTypeCode}", parseUnderlyingTypeCode.toString());
    }

    private String getJavaArray2DCode(Argument arg, int i) {
        String fillArray2DFunction = javaArray2D
                .replaceAll("\\$\\{i}", Integer.toString(i))
                .replaceAll("\\$\\{underlyingType}", getJavaType(arg.getUnderlyingType(), true));
        StringBuilder parseUnderlyingTypeCode = new StringBuilder();
        switch (arg.getUnderlyingType()) {
            case STRING:
                parseUnderlyingTypeCode.append("String[][] convertedArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))").append("\n");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\"))").append("\n");
                parseUnderlyingTypeCode.append("\t.toArray(String[]::new)).toArray(String[][]::new);").append("\n");
                break;
            case INTEGER:
                parseUnderlyingTypeCode.append("int[][] convertedArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))").append("\n");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\")).map(i -> Integer.parseInt(i))").append("\n");
                parseUnderlyingTypeCode.append("\t.mapToInt(x -> x).toArray()).toArray(int[][]::new);").append("\n");
                break;
            case FLOAT:
                parseUnderlyingTypeCode.append("double[][] convertedArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))").append("\n");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\")).map(i -> Double.parseDouble(i))").append("\n");
                parseUnderlyingTypeCode.append("\t.mapToDouble(x -> x).toArray()).toArray(double[][]::new);").append("\n");
                break;
            case BOOLEAN:
                parseUnderlyingTypeCode.append("Double[][] tempArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))").append("\n");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\")).map(i -> Double.parseDouble(i))").append("\n");
                parseUnderlyingTypeCode.append("\t.toArray(Double[]::new)).toArray(Double[][]::new);").append("\n");
                parseUnderlyingTypeCode.append("double[][] convertedArray2D = new double[tempArray2D.length][tempArray2D[0].length];").append("\n");
                parseUnderlyingTypeCode.append("for (int row = 0; row < convertedArray2D.length; row++) {").append("\n");
                parseUnderlyingTypeCode.append("\tfor (int col = 0; col < convertedArray2D[0].length; col++) {").append("\n");
                parseUnderlyingTypeCode.append("\t\tconvertedArray2D[row][col] = (double) tempArray2D[row][col];").append("\n");
                parseUnderlyingTypeCode.append("\t}").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                break;
        }
        return fillArray2DFunction.replaceAll("\\$\\{parseUnderlyingTypeCode}", parseUnderlyingTypeCode.toString());
    }

    private String getJavaListOfListsCode(Argument arg, int i) {
        String fillListOfListsFunction = javaListOfLists
                .replaceAll("\\$\\{i}", Integer.toString(i))
                .replaceAll("\\$\\{underlyingType}", getJavaType(arg.getUnderlyingType(), true));
        StringBuilder parseUnderlyingTypeCode = new StringBuilder();
        switch (arg.getUnderlyingType()) {
            case STRING:
                parseUnderlyingTypeCode.append("if (innerLstStr.charAt(0) != '\\\"' || innerLstStr.charAt(innerLstStr.length() - 1) != '\\\"') {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception(\"Input \" + str + \" is not a valid list of lists (missing quotes around strings)\");").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                parseUnderlyingTypeCode.append("innerLst.add(innerLstStr.substring(1, innerLstStr.length() - 1));").append("\n");
                break;
            case INTEGER:
                parseUnderlyingTypeCode.append("try {").append("\n");
                parseUnderlyingTypeCode.append("\tint i = Integer.parseInt(innerLstStr)").append("\n");
                parseUnderlyingTypeCode.append("\tinnerList.add(i)").append("\n");
                parseUnderlyingTypeCode.append("} catch (Exception ex) {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception(\"Input \" + str + \" is not a valid list of lists (invalid ints)\");").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                break;
            case FLOAT:
                parseUnderlyingTypeCode.append("try {").append("\n");
                parseUnderlyingTypeCode.append("\tdouble d = Double.parseDouble(innerLstStr)").append("\n");
                parseUnderlyingTypeCode.append("\tinnerList.add(d)").append("\n");
                parseUnderlyingTypeCode.append("} catch (Exception ex) {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception(\"Input \" + str + \" is not a valid list of lists (invalid doubles)\");").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                break;
            case BOOLEAN:
                parseUnderlyingTypeCode.append("try {").append("\n");
                parseUnderlyingTypeCode.append("\tboolean b = Boolean.parseBoolean(innerLstStr)").append("\n");
                parseUnderlyingTypeCode.append("\tinnerList.add(b)").append("\n");
                parseUnderlyingTypeCode.append("} catch (Exception ex) {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception(\"Input \" + str + \" is not a valid list of lists (invalid booleans)\");").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                break;
        }
        return fillListOfListsFunction.replaceAll("\\$\\{parseUnderlyingTypeCode}", parseUnderlyingTypeCode.toString());
    }

    private String getJavaDictionaryCode(Argument arg, int i) {
        String fillDictionaryFunction = javaDictionary
                .replaceAll("\\$\\{i}", Integer.toString(i))
                .replaceAll("\\$\\{underlyingType}", getJavaType(arg.getUnderlyingType(), false))
                .replaceAll("\\$\\{underlyingType2}", getJavaType(arg.getUnderlyingType2(), false));
        StringBuilder parseUnderlyingTypeCode = new StringBuilder();
        switch (arg.getUnderlyingType()) {
            case STRING:
                parseUnderlyingTypeCode.append("if (keyStr.length() < 2 || keyStr.charAt(0) != '\"' || keyStr.charAt(keyStr.length() - 1) != '\"') {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception();").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                parseUnderlyingTypeCode.append("String key = keyStr.substring(1, keyStr.length() - 1);").append("\n");
                break;
            case INTEGER:
                parseUnderlyingTypeCode.append("Integer key = Integer.parseInt(keyStr);").append("\n");
                break;
            case FLOAT:
                parseUnderlyingTypeCode.append("Double key = Double.parseDouble(keyStr);").append("\n");
                break;
            case BOOLEAN:
                parseUnderlyingTypeCode.append("Boolean key = Boolean.parseBoolean(keyStr);").append("\n");
                break;
        }
        StringBuilder parseUnderlyingType2Code = new StringBuilder();
        switch (arg.getUnderlyingType()) {
            case STRING:
                parseUnderlyingType2Code.append("if (valStr.length() < 2 || valStr.charAt(0) != '\"' || valStr.charAt(valStr.length() - 1) != '\"') {").append("\n");
                parseUnderlyingType2Code.append("\tthrow new Exception();").append("\n");
                parseUnderlyingType2Code.append("}").append("\n");
                parseUnderlyingType2Code.append("String val = valStr.substring(1, valStr.length() - 1);").append("\n");
                break;
            case INTEGER:
                parseUnderlyingType2Code.append("Integer val = Integer.parseInt(valStr);").append("\n");
                break;
            case FLOAT:
                parseUnderlyingType2Code.append("Double val = Double.parseDouble(valStr);").append("\n");
                break;
            case BOOLEAN:
                parseUnderlyingType2Code.append("Boolean val = Boolean.parseBoolean(valStr);").append("\n");
                break;
        }
        return fillDictionaryFunction
                .replaceAll("\\$\\{parseUnderlyingTypeCode}", parseUnderlyingTypeCode.toString())
                .replaceAll("\\$\\{parseUnderlyingType2Code}", parseUnderlyingType2Code.toString());
    }
}
