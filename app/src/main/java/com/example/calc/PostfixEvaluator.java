package com.example.calc;
import java.util.Stack;
public class PostfixEvaluator {
    static boolean reachedInfinity;
    public static String evaluate(StringBuffer expression) {
        Stack<Double> S = new Stack<>();
        reachedInfinity = false;
        int expressionLength = expression.length();
        try {
            if (isOperator(expression.charAt(0))) {
                return "Error in expression.";
            }
            for (int i = 0; i < expressionLength; i++) {
                char ch = expression.charAt(i);
                if(isNumericDigit(ch)) {
                    int operand = 0;
                    while(i < expressionLength && isNumericDigit(expression.charAt(i))) {
                        operand = (operand * 10) + (expression.charAt(i) - '0');
                        i++;
                    }
                    i--;
                    S.push((double)operand);
                } else {
                    if(isOperator(ch)) {
                        double operand1 = 0, operand2;
                        operand2 = S.pop();
                        if (!S.isEmpty()) {
                            operand1 = S.pop();
                        }
                        double result = performOperation(ch, operand1, operand2);
                        S.push(result);
                    }
                }
            }

            double finalResult = S.peek();
            if(reachedInfinity && (Double.isFinite(finalResult))) {
                return "Error";
            }
            if(Double.isFinite(finalResult)) {
                if(finalResult == (long) finalResult) {
                    return Long.toString((long) finalResult);
                } else {
                    return Double.toString(finalResult);
                }
            }
            if(Double.isNaN(finalResult)) {
                return "NaN";
            }
            return "Result too large";

        } catch(Exception e) {
            return "Error";
        }
    }

    private static boolean isNumericDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^';
    }

    private static double performOperation(char operation, double operand1, double operand2) {
        double result;
        switch(operation) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if(operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = operand1 / operand2;
                break;
            case '%':
                if(operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = operand1 % operand2;
                break;
            default:
                result = Math.pow(operand1, operand2);
        }
        if(!reachedInfinity && Double.isInfinite(result)) {
            reachedInfinity = true;
        }
        return result;
    }
}