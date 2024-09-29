package ui;

import base.SignalBus;
import core.MediaModel;
import core.MediaStorage;
import data.ClientCode;
import handler.DragAndDropHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MediaPanel extends JPanel {

    private final MediaStorage mediaStorage;
    private final JPanel imagePanel;

    public MediaPanel(MediaStorage mediaStorage) {
        this.mediaStorage = mediaStorage;

        setLayout(new BorderLayout());
        setBackground(Color.lightGray);
        
        var backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 0));  // Ограничиваем ширину кнопки
        backButton.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));  // Ограничиваем ширину, но растягиваем по высоте
        backButton.addActionListener(e -> SignalBus.fire(ClientCode.openConversationPanel, ""));
        add(backButton, BorderLayout.WEST);
        
        imagePanel = new JPanel();
        imagePanel.setLayout(new GridBagLayout()); 
        imagePanel.setBackground(Color.lightGray);
        
        var scrollPane = new JScrollPane(imagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        add(scrollPane, BorderLayout.CENTER);
        
        var handler = new DragAndDropHandler();
        var dropArea = new DropArea(handler);
        dropArea.setPreferredSize(new Dimension(300, 0));
        add(dropArea, BorderLayout.EAST);
        
        handler.subscribeOnDrop(evt -> {
            var path = (String) evt.getNewValue();
            var conversation = mediaStorage.getClientID();
            SignalBus.fire(ClientCode.imageUpload, String.format("%s|%s", conversation, path));
        });
    }

    public void initialize() {
        for (MediaModel mediaModel : mediaStorage) {
            createMedia(mediaModel);
        }

        mediaStorage.subscribeOnAdd(evt -> {
            var media = (MediaModel) evt.getNewValue();
            createMedia(media);
        });
    }

    private void createMedia(MediaModel media) {
        MediaView view;
        try {
            view = new MediaView(media);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        imagePanel.add(view, gbc);
        imagePanel.revalidate();
        imagePanel.repaint();
    }
}