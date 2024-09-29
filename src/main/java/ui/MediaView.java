package ui;

import core.MediaModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MediaView extends JPanel {
    private final MediaModel mediaModel;
    
    private final BufferedImage bufferedImage;

    public MediaView(MediaModel mediaModel) throws IOException {
        this.mediaModel = mediaModel;
        bufferedImage = ImageIO.read(new File(mediaModel.getPath()));
        setPreferredSize(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2d = (Graphics2D) g;
        g2d.drawImage(bufferedImage, 0, 0, null);
    }
}
