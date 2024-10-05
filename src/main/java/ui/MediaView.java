package ui;

import core.MediaModel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MediaView extends JPanel {
    
    private final MediaModel mediaModel;

    public MediaView(MediaModel mediaModel) throws IOException {
        this.mediaModel = mediaModel;
        var bounds = mediaModel.visualObject().shape().getBounds();
        setPreferredSize(new Dimension(bounds.width, bounds.height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;
        if (mediaModel == null) return;
        var visualObject = mediaModel.visualObject();
        if (visualObject == null) return;
        
        g2d.setColor(visualObject.color());
        if (visualObject.stroke() != null)
            g2d.setStroke(visualObject.stroke());
        g2d.draw(visualObject.shape());
    }
}
