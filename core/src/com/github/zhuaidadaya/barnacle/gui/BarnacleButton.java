package com.github.zhuaidadaya.barnacle.gui;

import com.github.zhuaidadaya.barnacle.option.SimpleOption;

import javax.swing.*;
import java.awt.*;

import static com.github.zhuaidadaya.barnacle.storage.Variables.formatConstant;

public class BarnacleButton extends JButton {
    private SimpleOption option;

    public BarnacleButton(SimpleOption option) {
        super(formatConstant(option.formatInvoke()), null);
        this.option = option;
        super.setBackground(Color.lightGray);
        super.setBorderPainted(false);
        super.setFocusPainted(false);
    }

    public SimpleOption getOption() {
        return option;
    }
}
