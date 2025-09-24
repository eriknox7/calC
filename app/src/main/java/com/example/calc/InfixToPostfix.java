package com.example.calc;
import java.util.Stack;

public class InfixToPostfix {
    public static StringBuffer convert(StringBuffer expression) {
        Stack<Character> S = new Stack<>();
        StringBuilder result = new StringBuilder();
        int resultIndex = 0;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') {
                if (result.length() > 0 && result.charAt(resultIndex - 1) != ' ') {
                    result.append(ch);
                    resultIndex++;
                }
            } else if (isOperand(ch)) {
                result.append(ch);
                resultIndex++;
            } else if (isOperator(ch)) {
                while (!S.isEmpty() && hasHigherPrec(S.peek(), ch) && !isOpeningParenthesis(S.peek())) {
                    if (result.length() > 0 && result.charAt(resultIndex - 1) != ' ') {
                        result.append(' ');
                        resultIndex++;
                    }
                    result.append(S.pop());
                    resultIndex++;
                }
                S.push(ch);
            } else if (isOpeningParenthesis(ch)) {
                S.push(ch);
            } else if (isClosingParenthesis(ch)) {
                while (!S.isEmpty() && !isOpeningParenthesis(S.peek())) {
                    if (result.length() > 0 && result.charAt(resultIndex - 1) != ' ') {
                        result.append(' ');
                        resultIndex++;
                    }
                    result.append(S.pop());
                    resultIndex++;
                }
                S.pop();
            }
        }

        while (!S.isEmpty()) {
            result.append(' ');
            result.append(S.pop());
        }

        return new StringBuffer(result);
    }

    private static boolean isOperand(char operand) {
        return (operand >= 'a' && operand <= 'z') ||
                (operand >= 'A' && operand <= 'Z') ||
                (operand >= '0' && operand <= '9');
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^';
    }

    private static boolean hasHigherPrec(char operator1, char operator2) {
        int op1Weight = getOperatorWeight(operator1);
        int op2Weight = getOperatorWeight(operator2);

        if (op1Weight == op2Weight) {
            return !isRightAssociative(operator1);
        }
        return op1Weight > op2Weight;
    }

    private static int getOperatorWeight(char operator1) {
        int weight = 0;
        switch (operator1) {
            case '+':
            case '-':
                weight = 1;
                break;
            case '*':
            case '/':
            case '%':
                weight = 2;
                break;
            case '^':
                weight = 3;
                break;
        }
        return weight;
    }

    private static boolean isRightAssociative(char operator1) {
        return operator1 == '^';
    }

    private static boolean isOpeningParenthesis(char parenthesis) {
        return parenthesis == '(' || parenthesis == '[' || parenthesis == '{';
    }

    private static boolean isClosingParenthesis(char parenthesis) {
        return parenthesis == ')' || parenthesis == ']' || parenthesis == '}';
    }
}