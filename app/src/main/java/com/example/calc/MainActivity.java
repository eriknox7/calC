package com.example.calc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    // Text editing buttons.
    Button btn_allClear, btn_delete;
    // Operator Buttons.
    Button btn_divide, btn_multiply, btn_add, btn_minus, btn_modulo, btn_power;
    // Digit Buttons.
    Button btn_one, btn_two, btn_three, btn_four, btn_five, btn_six, btn_seven, btn_eight, btn_nine, btn_zero;
    // Equal Button
    Button btn_isEqualTo;
    // EditText with soft Keyboard disabled to manipulate and display result.
    NoKeyboardEditText answer;
    // TextView to show previous question.
    TextView previousQuestion;
    // A temporary string used throughout to manipulate user input and show the previous question.
    StringBuffer displayString = new StringBuffer();
    // String the save the answer to the last question. (For the next time the app opens)
    String lastAnswer = "";
    // String to save the last question. (For the next time the app opens)
    String lastQuestion = "";
    // For properly appending operators and operands.
    String currentOperator, currentOperand;
    // Unicode code point for minus sign (−)
    int codePoint = 0x2212;
    // Convert the code point to a char or char array
    char[] chars = Character.toChars(codePoint);
    // If it's a single character (which it is in this case)
    char minusSign = chars[0];
    // Multiplication Sign.
    String multiplicationSign = "×";
    // String to pass for evaluation.
    StringBuffer solvableExpression = new StringBuffer();
    // String to save 'displayString' after replacing arithmetic operators (graphical) in it with computer recognized arithmetic operators.
    // minusSign and multiplicationSign to be precise.
    StringBuffer expression;
    // Store the final answer and append it to answer(EditText to show the result).
    String finalAnswer;
    // String containing error message to show as and when needed.
    String error = "Error";
    // Manage evaluation as the next question is entered.
    boolean isPreviousQuestionSolved = true;
    // Is true when app starts. (For managing input).
    boolean appReopen = true;
    // Used while operator is clicked to manipulate input.
    boolean canContinueFurther = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checks the android version. If it is pie or above full screen will be used.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }

        // Enable immersive mode
        enableImmersiveMode();

        // find the btn_power component.
        btn_power = findViewById(R.id.btn_power);

        // Create a SpannableString for "xⁱʸ" with superscript 'y' to show the exponent sign on btn_power button.
        SpannableString spannableString = new SpannableString("xⁱʸ");
        spannableString.setSpan(new SuperscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set up the exponent sign on the button.
        btn_power.setText(spannableString);

        // Find the answer(EditText) used for displaying result.
        answer = findViewById(R.id.answer);
        //  Get the focus on answer as the main activity starts.
        answer.requestFocus();
        // Instantly hide the system keyboard. As it should not enable the soft keyboard when touched the answer field.
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // If clicked, keep the cursor visible while hiding the soft keyboard.
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer.setCursorVisible(true);      // Ensure cursor is visible
                hideSystemKeyboard();               // Block system keyboard
            }
        });

        // find TextView for showing previous question.
        previousQuestion = findViewById(R.id.previousQuestion);

        // Show the previous question and answer to it. (Saved value from last time).
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        lastAnswer = sharedPreferences.getString("lastAnswerString", "");
        lastQuestion = sharedPreferences.getString("lastQuestionString", "");
        previousQuestion.setText(lastQuestion);
        answer.setText("");
        answer.append(lastAnswer);

        // find text editing buttons.
        btn_allClear = findViewById(R.id.btn_allClear);
        btn_delete = findViewById(R.id.btn_delete);

        // find operator buttons.
        btn_add = findViewById(R.id.btn_add);
        btn_minus = findViewById(R.id.btn_minus);
        btn_multiply = findViewById(R.id.btn_multiply);
        btn_divide = findViewById(R.id.btn_divide);
        btn_modulo = findViewById(R.id.btn_modulo);

        // find digit buttons.
        btn_one = findViewById(R.id.btn_one);
        btn_two = findViewById(R.id.btn_two);
        btn_three = findViewById(R.id.btn_three);
        btn_four = findViewById(R.id.btn_four);
        btn_five = findViewById(R.id.btn_five);
        btn_six = findViewById(R.id.btn_six);
        btn_seven = findViewById(R.id.btn_seven);
        btn_eight = findViewById(R.id.btn_eight);
        btn_nine = findViewById(R.id.btn_nine);
        btn_zero = findViewById(R.id.btn_zero);

        // find Equal button.
        btn_isEqualTo = findViewById(R.id.btn_isEqualTo);

        // Common on click listener for digit buttons.
        View.OnClickListener commonDigitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPreviousQuestionSolved) {
                    if(answer.length() > 0) {
                        reInitializeDisplayString();
                        resetAnswer();
                    }
                }
                isPreviousQuestionSolved = false;
                int viewId = v.getId();
                if (viewId == btn_one.getId()) {
                    currentOperand = "1";
                } else if (viewId == btn_two.getId()) {
                    currentOperand = "2";
                } else if (viewId == btn_three.getId()) {
                    currentOperand = "3";
                } else if (viewId == btn_four.getId()) {
                    currentOperand = "4";
                } else if (viewId == btn_five.getId()) {
                    currentOperand = "5";
                } else if (viewId == btn_six.getId()) {
                    currentOperand = "6";
                } else if (viewId == btn_seven.getId()) {
                    currentOperand = "7";
                } else if (viewId == btn_eight.getId()) {
                    currentOperand = "8";
                } else if (viewId == btn_nine.getId()) {
                    currentOperand = "9";
                } else {
                    currentOperand = "0";
                }
                answer.append(currentOperand);
                displayString.append(currentOperand);
            }
        };

        // Set onClickListener for digits buttons.
        btn_one.setOnClickListener(commonDigitClickListener);
        btn_two.setOnClickListener(commonDigitClickListener);
        btn_three.setOnClickListener(commonDigitClickListener);
        btn_four.setOnClickListener(commonDigitClickListener);
        btn_five.setOnClickListener(commonDigitClickListener);
        btn_six.setOnClickListener(commonDigitClickListener);
        btn_seven.setOnClickListener(commonDigitClickListener);
        btn_eight.setOnClickListener(commonDigitClickListener);
        btn_nine.setOnClickListener(commonDigitClickListener);
        btn_zero.setOnClickListener(commonDigitClickListener);

        // Common on click listener for edit text buttons.
        View.OnClickListener commonEditTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPreviousQuestionSolved) {
                    if(answer.length() > 0) {
                        reInitializeDisplayString();
                        resetAnswer();
                    }
                    isPreviousQuestionSolved = false;
                } else {
                    int viewId = v.getId();
                    if (viewId == btn_allClear.getId()) {
                        if(displayString.length() > 0) {
                            answer.setText("");
                            displayString.setLength(0);
                        }
                    } else {
                        if (displayString.length() > 0) {
                            displayString.deleteCharAt(displayString.length() - 1);
                            answer.setText("");
                            answer.append(displayString);
                        }
                    }
                }
            }
        };

        // Set onClickListener for text edit buttons.
        btn_allClear.setOnClickListener(commonEditTextClickListener);
        btn_delete.setOnClickListener(commonEditTextClickListener);

        // Common onClickListener for operator buttons.
        View.OnClickListener commonOperatorOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPreviousQuestionSolved || appReopen) {
                    if(appReopen) {
                       if(answer.length() > 0) {
                           if(isPreviousQuestionSolved) {
                               canContinueFurther = false;
                           }
                       }
                    }
                    if(canContinueFurther) {
                        isPreviousQuestionSolved = false;
                        int viewId = v.getId();
                        String lastChar;
                        if (viewId == btn_add.getId()) {
                            currentOperator = "+";
                        } else if (viewId == btn_minus.getId()) {
                            currentOperator = Character.toString(minusSign);
                        } else if (viewId == btn_multiply.getId()) {
                            currentOperator = multiplicationSign;
                        } else if (viewId == btn_divide.getId()) {
                            currentOperator = "/";
                        } else if (viewId == btn_modulo.getId()) {
                            currentOperator = "%";
                        } else {
                            currentOperator = "^";
                        }
                        if (displayString.length() > 0) {
                            lastChar = Character.valueOf(displayString.charAt(displayString.length() - 1)).toString();
                        } else {
                            lastChar = "";
                        }
                        if (!isOperator(lastChar)) {
                            answer.append(currentOperator);
                            displayString.append(currentOperator);
                        } else {
                            displayString.deleteCharAt(displayString.length() - 1);
                            displayString.append(currentOperator);
                            answer.setText("");
                            answer.append(displayString);
                        }
                    }
                    appReopen = false;
                    canContinueFurther = true;
                }
            }
        };

        // Set on click listener for operator buttons.
        btn_add.setOnClickListener(commonOperatorOnClickListener);
        btn_minus.setOnClickListener(commonOperatorOnClickListener);
        btn_multiply.setOnClickListener(commonOperatorOnClickListener);
        btn_divide.setOnClickListener(commonOperatorOnClickListener);
        btn_modulo.setOnClickListener(commonOperatorOnClickListener);
        btn_power.setOnClickListener(commonOperatorOnClickListener);

        // OnClickListener for equal button.
        btn_isEqualTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpressionValid(displayString)) {
                    terminateExtraExpression(displayString);
                    if(displayString.length() > 0) {
                        String tempExpression = new String(displayString);
                        tempExpression = tempExpression.replace(multiplicationSign, "*");
                        tempExpression = tempExpression.replace(Character.toString(minusSign), "-");
                        expression = new StringBuffer(tempExpression);
                        buildSolvableExpression(expression);
                        finalAnswer = PostfixEvaluator.evaluate(InfixToPostfix.convert(solvableExpression));
                        previousQuestion.setText(displayString);
                        answer.setText("");
                        answer.append(finalAnswer);
                    } else {
                        answer.setText("");
                        answer.append(error);
                    }
               } else {
                    if((displayString.length() > 0)) {
                        answer.setText("");
                        answer.append(error);
                    }
                }
                reInitializeData();
                isPreviousQuestionSolved = true;
            }
        });
    }

    // Function to enable immersive mode.
    private void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        // Re-enable immersive mode when the user interacts with the screen
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                );
            }
        });
    }

    // Function to hide soft keyboard.
    private void hideSystemKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(answer.getWindowToken(), 0);
    }

    // Retain the last data.
    @Override
    protected void onStop() {
        super.onStop();
        lastQuestion = previousQuestion.getText().toString();
        lastAnswer = Objects.requireNonNull(answer.getText()).toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastQuestionString", lastQuestion);
        editor.putString("lastAnswerString", lastAnswer);
        editor.apply();
    }

    // Function to check whether the current character is an operator or not.
    boolean isOperator(String operator) {
        if(operator.equals("+")) return true;
        if(operator.equals(Character.toString(minusSign))) return true;
        if(operator.equals(multiplicationSign)) return true;
        if(operator.equals("/")) return true;
        if(operator.equals("%")) return true;
        return operator.equals("^");
    }

    // Checks if the length of user entered question is greater than zero and does not have any sign other than '-' and '+' at the beginning.
    boolean isExpressionValid(StringBuffer expression) {
        if(expression.length() > 0) {
            switch (Character.toString(expression.charAt(0))) {
                case "/":
                case "%":
                case "^":
                case "×": return false;
                default: return true;
            }
        }
        return false;
    }

    // Exclude the last character of the question string if it is an operator (is of no use).
    void terminateExtraExpression(StringBuffer expression) {
        if(isOperator(Character.toString(expression.charAt(expression.length() - 1)))) {
            expression.deleteCharAt(expression.length() - 1);
        }
    }

    // Get the valid, extra-part deleted question string and insert spaces between every operator and operand. (Formatting question string)
    void buildSolvableExpression(StringBuffer expression) {
        int i = 0;
        boolean giveSpace = false;
        while(i < expression.length()) {
            while(i < expression.length() && (!(expression.charAt(i) < '0' || expression.charAt(i) > '9'))) {
                giveSpace = true;
                solvableExpression.append(expression.charAt(i));
                i++;
            }
            if(i < expression.length()) {
                if(giveSpace) {
                    solvableExpression.append(' ');
                }
            }
            if(i < expression.length()) {
                solvableExpression.append(expression.charAt(i));
                i++;
                if(i < expression.length()) {
                    solvableExpression.append(' ');
                }
            }
        }
    }

    // Re-Initialize the data if next question is entered.
    void reInitializeData() {
        solvableExpression.setLength(0);
        displayString.setLength(0);
    }

    // Re-Initialize the displayString when the any button is pressed after evaluating a question.
    void reInitializeDisplayString() {
        displayString.setLength(0);
    }

    // Reset answer field if any button is pressed after evaluating last question.
    void resetAnswer() {
        answer.setText("");
    }
}