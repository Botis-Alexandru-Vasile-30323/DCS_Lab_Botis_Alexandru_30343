package lab.scd.net.socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class CalculatorClientGUI extends JFrame implements ActionListener {
    private JTextField txtNum1;
    private JTextField txtNum2;
    private JTextField txtResult;
    private JButton btnAdd, btnSub, btnMul, btnDiv;

    private static final String SERVER_HOST = "10.132.72.78";
    private static final int SERVER_PORT = 1901;

    public CalculatorClientGUI() {
        setTitle("TCP Network Calculator");
        setSize(360, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Input & Result Form Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        inputPanel.add(new JLabel("First Number:"));
        txtNum1 = new JTextField();
        inputPanel.add(txtNum1);

        inputPanel.add(new JLabel("Second Number:"));
        txtNum2 = new JTextField();
        inputPanel.add(txtNum2);

        inputPanel.add(new JLabel("Server Result:"));
        txtResult = new JTextField();
        txtResult.setEditable(false);
        txtResult.setBackground(Color.LIGHT_GRAY);
        inputPanel.add(txtResult);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        btnAdd = new JButton("+");
        btnSub = new JButton("-");
        btnMul = new JButton("*");
        btnDiv = new JButton("/");

        btnAdd.addActionListener(this);
        btnSub.addActionListener(this);
        btnMul.addActionListener(this);
        btnDiv.addActionListener(this);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnSub);
        buttonPanel.add(btnMul);
        buttonPanel.add(btnDiv);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String num1Str = txtNum1.getText().trim();
        String num2Str = txtNum2.getText().trim();

        if (num1Str.isEmpty() || num2Str.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String operator = ((JButton) e.getSource()).getText();
        sendCalculationRequest(num1Str, num2Str, operator);
    }

    private void sendCalculationRequest(String num1, String num2, String op) {
        // Open connection, send request, receive response, and close
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Message format: num1,num2,op
            String payload = num1 + "," + num2 + "," + op;
            out.println(payload);

            String response = in.readLine();
            txtResult.setText(response);

            // Signal termination for this session
            out.println("END");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CalculatorClientGUI().setVisible(true);
        });
    }
}