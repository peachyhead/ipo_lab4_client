package ui;

import core.ConversationStorage;
import core.MediaStorage;
import handler.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationPanel extends JPanel {
    
    private final List<ConversationHeaderView> views = new ArrayList<>();
    
    private final ConversationStorage conversationStorage;

    public ConversationPanel(ConversationStorage conversationStorage) {
        this.conversationStorage = conversationStorage;
        var layout = new GridLayout();
        layout.setColumns(1);
        layout.setVgap(20);
        setBackground(Color.DARK_GRAY);
    }
    
    public void initialize() {
        for (MediaStorage storage : conversationStorage) {
            if (storage.getClientID().equals(Client.getId())) continue;
            createHeader(storage);
        }
        
        conversationStorage.subscribeOnAdd(evt -> {
            var storage = (MediaStorage) evt.getNewValue();
            createHeader(storage);
        });

        conversationStorage.subscribeOnRemove(evt -> {
            var storage = (MediaStorage) evt.getNewValue();
            var match = views.stream()
                    .filter(item -> item.getId().equals(storage.getClientID())).findFirst();
            if (match.isEmpty()) return;
            views.remove(match.get());
            remove(match.get());
            revalidate();
        });
    }

    private void createHeader(MediaStorage storage) {
        var view = new ConversationHeaderView(storage.getClientID());
        views.add(view);
        add(view);
        revalidate();
        view.initialize();
    }
}
