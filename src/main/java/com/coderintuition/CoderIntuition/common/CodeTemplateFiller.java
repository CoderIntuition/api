package com.coderintuition.CoderIntuition.common;

import com.coderintuition.CoderIntuition.models.Language;
import com.coderintuition.CoderIntuition.pojos.general.Argument;
import com.coderintuition.CoderIntuition.pojos.general.ArgumentType;
import com.coderintuition.CoderIntuition.pojos.general.ReturnType;
import com.coderintuition.CoderIntuition.pojos.general.UnderlyingType;
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
    private String javaTemplate;
    private String javaList;
    private String javaArray2D;
    private String javaTree;
    private String javaScriptTemplate;

    public static CodeTemplateFiller getInstance() {
        if (instance == null) {
            instance = new CodeTemplateFiller();
        }
        return instance;
    }

    private CodeTemplateFiller() {
        try {
            pythonTemplate = fileToString("pythonTestRun.txt");
            pythonTree = fileToString("pythonTree.txt");
            pythonLinkedList = fileToString("pythonLinkedList.txt");
            javaTemplate = fileToString("javaTestRun.txt");
            javaList = fileToString("javaList.txt");
            javaArray2D = fileToString("javaArray2D.txt");
            javaTree = fileToString("javaTree.txt");
            javaScriptTemplate = fileToString("javaScriptTestRun.txt");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String fill(Language language, String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) throws Exception {
        switch (language) {
            case PYTHON:
                return fillPython(userCode, solutionCode, functionName, args, returnType);
            case JAVA:
                return fillJava(userCode, solutionCode, functionName, args, returnType);
            case JAVASCRIPT:
                return fillJavaScript(userCode, solutionCode, functionName, args, returnType);
            default:
                throw new Exception("Test run language not recognized");
        }
    }

    private String fileToString(String fileName) throws IOException {
        Resource resource = new ClassPathResource(fileName);
        File file = resource.getFile();
        return FileUtils.readFileToString(file, "UTF-8");
    }

    private String fillPython(String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) {
        boolean tree = false;
        boolean linkedList = false;
        StringBuilder argsStr = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            Argument arg = args.get(i);
            switch (arg.getType()) {
                case TREE:
                    argsStr.append("___list_to_tree(").append(arg.getValue()).append(")");
                    tree = true;
                    break;
                case LINKED_LIST:
                    argsStr.append("___list_to_linked_list(").append(arg.getValue()).append(")");
                    linkedList = true;
                    break;
                case STRING:
                case INTEGER:
                case FLOAT:
                case LIST:
                case ARRAY_2D:
                case DICTIONARY:
                    argsStr.append(arg.getValue());
            }
            if (i < args.size() - 1) {
                argsStr.append(", ");
            }
        }

        String code = pythonTemplate;

        switch (returnType.getType()) {
            case TREE:
                code = code.replaceAll("\\$\\{userResultFormat}", "user_result = ___tree_to_list(user_result)");
                code = code.replaceAll("\\$\\{solResultFormat}", "sol_result = ___tree_to_list(sol_result)");
                break;
            case LINKED_LIST:
                code = code.replaceAll("\\$\\{userResultFormat}", "user_result = ___linked_list_to_list(user_result)");
                code = code.replaceAll("\\$\\{solResultFormat}", "sol_result = ___linked_list_to_list(sol_result)");
                break;
        }

        return code
                .replaceAll("\\$\\{tree}", tree ? pythonTree : "")
                .replaceAll("\\$\\{linkedList}", linkedList ? pythonLinkedList : "")
                .replaceAll("\\$\\{userCode}", userCode)
                .replaceAll("\\$\\{solutionCode}", solutionCode)
                .replaceAll("\\$\\{functionName}", functionName)
                .replaceAll("\\$\\{args}", argsStr.toString());
    }

    private String fillJava(String userCode, String solutionCode, String functionName, List<Argument> args, ReturnType returnType) throws Exception {
        boolean usingTree = false;
        boolean usingLinkedList = false;
        StringBuilder setupCode = new StringBuilder();
        StringBuilder argsCode = new StringBuilder();

        for (int i = 0; i < args.size(); i++) {
            Argument arg = args.get(i);
            switch (arg.getType()) {
                case TREE:
                    setupCode.append(getJavaListCode(arg, i));
                    argsCode.append("TreeNode.listToTree(stringToList").append(i).append("(").append(arg.getValue()).append("))");
                    usingTree = true;
                    break;
                case LINKED_LIST:
                    setupCode.append(getJavaListCode(arg, i));
                    argsCode.append("listToLinkedList(stringToList").append(i).append("(").append(arg.getValue()).append("))");
                    usingLinkedList = true;
                    break;
                case LIST:
                    setupCode.append(getJavaListCode(arg, i));
                    argsCode.append("stringToList").append(i).append("(").append(arg.getValue()).append(")");
                    break;
                case ARRAY_2D:
                    break;
                case DICTIONARY:
                    break;
                case STRING:
                case INTEGER:
                case FLOAT:
                    argsCode.append(arg.getValue());
            }
            if (i < args.size() - 1) {
                argsCode.append(", ");
            }
        }

        StringBuilder definitionCode = new StringBuilder();
        if (usingTree) {
            definitionCode.append(javaTree);
        }

        return javaTemplate
                .replaceAll("\\$\\{definitionCode}", definitionCode.toString())
                .replaceAll("\\$\\{userCode}", userCode)
                .replaceAll("\\$\\{solutionCode}", solutionCode)
                .replaceAll("\\$\\{setupCode}", setupCode.toString())
                .replaceAll("\\$\\{functionName}", functionName)
                .replaceAll("\\$\\{args}", argsCode.toString())
                .replaceAll("\\$\\{retType}", getJavaType(returnType));
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
            case LIST_OF_LISTS:
                return "List<List<" + getJavaType(returnType.getUnderlyingType(), false) + ">>";
            case LINKED_LIST:
                return "ListNode";
            case ARRAY_2D:
                return getJavaType(returnType.getUnderlyingType(), true) + "[][]";
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
                parseUnderlyingTypeCode.append("if (item.length() < 2 || item.charAt(0) != '\"' || item.charAt(item.length() - 1) != '\"') {").append("\n");
                parseUnderlyingTypeCode.append("\tthrow new Exception();").append("\n");
                parseUnderlyingTypeCode.append("}").append("\n");
                parseUnderlyingTypeCode.append("val = item.substring(1, item.length() - 1);").append("\n");
                break;
            case INTEGER:
                parseUnderlyingTypeCode.append("val = Integer.parseInt(item);\n");
                break;
            case FLOAT:
                parseUnderlyingTypeCode.append("val = Double.parseDouble(item);\n");
                break;
            case BOOLEAN:
                parseUnderlyingTypeCode.append("val = Boolean.parseBoolean(item);\n");
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
                parseUnderlyingTypeCode.append("String[][] convertedArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\"))");
                parseUnderlyingTypeCode.append("\t.toArray(String[]::new)).toArray(String[][]::new);");
                break;
            case INTEGER:
                parseUnderlyingTypeCode.append("int[][] convertedArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\")).map(i -> Integer.parseInt(i))");
                parseUnderlyingTypeCode.append("\t.mapToInt(x -> x).toArray()).toArray(int[][]::new);");
                break;
            case FLOAT:
                parseUnderlyingTypeCode.append("double[][] convertedArray2D = Arrays.stream(str.substring(2, str.length() - 2).split(\"\\\\],\\\\[\"))");
                parseUnderlyingTypeCode.append("\t.map(e -> Arrays.stream(e.split(\"\\\\s*,\\\\s*\")).map(i -> Double.parseDouble(i))");
                parseUnderlyingTypeCode.append("\t.mapToDouble(x -> x).toArray()).toArray(double[][]::new);");
                break;
            case BOOLEAN:

        }
    }
}
