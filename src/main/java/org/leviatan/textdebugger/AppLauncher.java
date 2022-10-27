package org.leviatan.textdebugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppLauncher {

    private JTextArea textArea;
    private JButton button;

    public static void main(String[] args) {

        AppLauncher appLauncher = new AppLauncher();
        appLauncher.showGUI();
    }

    public AppLauncher() {

        textArea = new JTextArea();
        textArea.setSize(850, 600);
        button = new JButton("Debug!");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = textArea.getText();
                MainTextEngineProcessor.generaInforme(text);
            }
        });
    }

    public void showGUI() {

        JFrame frame = new JFrame("Text Debugger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());
        frame.add(textArea, BorderLayout.CENTER);
        frame.add(button, BorderLayout.SOUTH);

        frame.setSize(900, 700);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
