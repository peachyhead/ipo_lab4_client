package ui;

import base.SignalBus;
import data.ClientCode;
import data.VisualObject;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VisualTemplateView extends JPanel {
    
    public final int id;
    @Getter private final VisualObject visualObject;

    public VisualTemplateView(int id, VisualObject visualObject) {
        this.id = id;
        this.visualObject = visualObject;
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                SignalBus.fire(ClientCode.visualSelect, String.valueOf(id));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;

        g2d.setColor(visualObject.color());
        if (visualObject.stroke() != null)
            g2d.setStroke(visualObject.stroke());
        g2d.draw(visualObject.shape());
    }
}
