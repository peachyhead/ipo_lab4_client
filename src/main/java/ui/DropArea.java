package ui;

import handler.DragAndDropHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;

public class DropArea extends JPanel {
    
    private Color targetColor = Color.lightGray;
    private final Color enterColor = new Color(0.4f, 1f, 0.4f); 
    
    public DropArea(DragAndDropHandler dragAndDropHandler) {
        new DropTarget(this, dragAndDropHandler);
        var label = new JLabel("<html>Drop<br>image<br>here!</html>");
        label.setFont(new Font("Courier New", Font.PLAIN, 35));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
        
        dragAndDropHandler.subscribeOnDragEnter(evt -> targetColor = enterColor);
        dragAndDropHandler.subscribeOnDragExit(evt -> targetColor = Color.lightGray);
        dragAndDropHandler.subscribeOnDrop(evt -> targetColor = Color.lightGray);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;
        g2d.setColor(targetColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
