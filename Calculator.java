import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder currentInput;
    private double firstNumber = 0.0;
    private char operator = '\0';
    private boolean justCalculated = false; // if we just showed a result

    public Calculator() {
        setTitle("Basic Calculator");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setLocationRelativeTo(null);

        // Top panel with display and Clear button
        JPanel top = new JPanel(new BorderLayout(6, 6));
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        top.add(display, BorderLayout.CENTER);

        JButton clearBtn = new JButton("C");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 20));
        clearBtn.addActionListener(this);
        top.add(clearBtn, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Buttons panel (4x4)
        JPanel panel = new JPanel(new GridLayout(4, 4, 8, 8));
        add(panel, BorderLayout.CENTER);

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.addActionListener(this);
            panel.add(btn);
        }

        currentInput = new StringBuilder();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // Digit
        if (command.matches("\\d")) {
            if (justCalculated) { // start new input after a result
                currentInput.setLength(0);
                operator = '\0';
                firstNumber = 0.0;
                justCalculated = false;
            }
            currentInput.append(command);
            display.setText(currentInput.toString());
            return;
        }

        // Decimal point
        if (command.equals(".")) {
            if (justCalculated) { // start new input
                currentInput.setLength(0);
                operator = '\0';
                firstNumber = 0.0;
                justCalculated = false;
            }
            // Prevent multiple decimals in same number
            if (currentInput.indexOf(".") == -1) {
                if (currentInput.length() == 0) currentInput.append("0"); // ".5" -> "0.5"
                currentInput.append(".");
                display.setText(currentInput.toString());
            }
            return;
        }

        // Clear
        if (command.equals("C")) {
            currentInput.setLength(0);
            display.setText("");
            firstNumber = 0.0;
            operator = '\0';
            justCalculated = false;
            return;
        }

        // Equals
        if (command.equals("=")) {
            if (operator != '\0' && currentInput.length() > 0) {
                double secondNumber = Double.parseDouble(currentInput.toString());
                double result = calculate(firstNumber, secondNumber, operator);
                display.setText(formatResult(result));
                // prepare so user can continue calculations with this result
                firstNumber = result;
                currentInput.setLength(0);
                operator = '\0';
                justCalculated = true;
            }
            return;
        }

        // Operator (+ - * /)
        if ("+-*/".contains(command)) {
            // If there's a current input, either set firstNumber (if no pending operator)
            // or compute pending operation and store it as firstNumber (so chaining works).
            if (currentInput.length() > 0) {
                double thisNum = Double.parseDouble(currentInput.toString());
                if (operator == '\0') {
                    firstNumber = thisNum;
                } else {
                    // compute pending operation
                    firstNumber = calculate(firstNumber, thisNum, operator);
                }
                display.setText(formatResult(firstNumber));
                currentInput.setLength(0);
            } else if (justCalculated) {
                // user wants to continue with the last result
                justCalculated = false;
            }
            operator = command.charAt(0);
        }
    }

    private double calculate(double a, double b, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return b == 0 ? Double.NaN : a / b;
            default: return 0;
        }
    }

    private String formatResult(double v) {
        if (Double.isNaN(v)) return "Error";
        // If integer-valued, show no decimal point
        if (v == (long) v) return String.valueOf((long) v);
        // Otherwise, trim trailing zeros by using BigDecimal-like formatting
        String s = String.valueOf(v);
        // Remove trailing zeros and possible trailing decimal dot
        if (s.contains("E")) return s; // scientific notation: return as is
        if (s.indexOf('.') >= 0) {
            while (s.endsWith("0")) s = s.substring(0, s.length() - 1);
            if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static void main(String[] args) {
        // Ensure UI uses OS look-and-feel (optional)
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Calculator::new);
    }
}
