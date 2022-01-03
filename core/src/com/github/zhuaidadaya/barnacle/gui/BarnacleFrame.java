package com.github.zhuaidadaya.barnacle.gui;

import com.github.zhuaidadaya.barnacle.option.SimpleOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class BarnacleFrame {
    private static final JTextArea textArea = new JTextArea();
    private static final JTextArea tipArea = new JTextArea();
    private static JFrame frame = new JFrame();
    private static final JPanel gamePanel = new JPanel();
    private static final Set<Integer> keyMap = new HashSet<>();
    private static LinkedHashSet<BarnacleButton> buttons = new LinkedHashSet<>();

    public static void apply() {
        removeButton();
        int y = 0;

        for(SimpleOption option : optionsButton) {
            BarnacleButton button = new BarnacleButton(option);
            button.setBounds(600, y, 180, 40);
            y += 50;
            gamePanel.add(button);
            button.updateUI();
            button.addActionListener(e -> {
                SimpleOption option1 = button.getOption();
                trend.initTrend(option1.trendName(), option1.trend());
            });
            buttons.add(button);
        }
    }

    public static void removeButton() {
        for(BarnacleButton button : buttons) {
            gamePanel.remove(button);
            gamePanel.repaint();
        }
    }

    public static void setText(String text) {
        text = formatConstant(text);
        textArea.setText(text);
        if(text.startsWith("\""))
            textArea.setForeground(Color.BLUE);
        else
            textArea.setForeground(Color.BLACK);
    }

    public static void setTip(String text) {
        text = formatConstant(text);
        tipArea.setText(text);
        if(text.startsWith("-"))
            tipArea.setForeground(Color.ORANGE);
        else
            tipArea.setForeground(Color.BLACK);
    }

    public static void init() {
        frame.setAlwaysOnTop(true);

        frame.setSize(800, 520);
        frame.getContentPane().setBackground(Color.white);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        frame.setLocation(width / 2 - frame.getWidth() / 2, height / 2 - frame.getHeight() / 2);

        frame.setResizable(false);

        gamePanel.add(textArea);
//        gamePanel.add(tipArea);

        frame.add(gamePanel);

        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("", Font.PLAIN, 15));

        tipArea.setLineWrap(true);
        tipArea.setEditable(false);

        frame.setVisible(true);

        gamePanel.setBounds(0,0,800,520);

        gamePanel.setLayout(new LayoutManager() {
            @Override
            public void addLayoutComponent(String name, Component comp) {

            }

            @Override
            public void removeLayoutComponent(Component comp) {

            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return null;
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return null;
            }

            @Override
            public void layoutContainer(Container parent) {
                textArea.setBounds(0, 0, 600, 520);
//                tipArea.setBounds(0,0,600,420);
            }
        });

        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyMap.add(e.getKeyCode());

                String s = Arrays.toString(keyMap.toArray()).replace("[", "|").replace("]", "|").replace(", ","|");

                if(s.equals("|10|"))
                    trend.trendContinue();

                if(s.contains("|17|") & s.contains("|10|"))
                    trend.trendSkip();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyMap.remove(e.getKeyCode());
            }
        });
    }
}
