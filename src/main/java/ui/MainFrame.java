package ui;

import base.SignalBus;
import base.SignalListener;
import core.ConversationStorage;
import data.ServerRequestCode;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final ConversationStorage conversationStorage;
    
    private MediaPanel mediaPanel;
    private ConversationPanel conversationPanel;
    
    public MainFrame(ConversationStorage conversationStorage) {
        this.conversationStorage = conversationStorage;
        
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new GridBagLayout());

        var timer = new Timer(10, e -> repaint());
        timer.start();
    }
    
    public void initialize() {
        SignalBus.subscribe(new SignalListener<String>(
                ServerRequestCode.clientHandshake, value -> 
                setTitle("Unga Bunga's Client " + value)));
    }
    
    public void setupConversationPanel() {
        if (mediaPanel != null) 
            remove(mediaPanel);
        if (conversationPanel != null)
            remove(conversationPanel);
        
        conversationPanel = new ConversationPanel(conversationStorage);
        var c = getConstraints();
        conversationPanel.initialize();
        
        add(conversationPanel, c);
        revalidate();
    }

    public void setupMediaPanel(String id) {
        if (conversationPanel != null) 
            remove(conversationPanel);
        if (mediaPanel != null) 
            remove(mediaPanel);
        
        var storage = conversationStorage.stream()
                .filter(item -> item.getClientID().equals(id)).findFirst();
        if (storage.isEmpty()) return;
        mediaPanel = new MediaPanel(storage.get());
        var c = getConstraints();
        mediaPanel.initialize();

        add(mediaPanel, c);
        revalidate();
    }

    private static GridBagConstraints getConstraints() {
        var c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        return c;
    }
}
