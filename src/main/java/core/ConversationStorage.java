package core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConversationStorage extends ArrayList<MediaStorage> {
    
    private final Map<String, String> loadingMap = new HashMap<>();
    
    public final Action onConversationAdd = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    };
    
    public final Action onConversationRemove = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    };
    
    public void addToLoad(String mediaID, String conversationID) {
        loadingMap.put(mediaID, conversationID);
    }

    public String getLoading(String mediaID) {
        var conversationID = loadingMap.get(mediaID);
        loadingMap.remove(mediaID);
        return conversationID;
    }
    
    @Override
    public boolean add(MediaStorage e) {
        var succeed = super.add(e);
        if (succeed)
            onConversationAdd.putValue("add", e);
        return succeed;
    }

    @Override
    public boolean remove(Object o) {
        var succeed = super.remove(o);
        if (succeed)
            onConversationRemove.putValue("remove", o);
        return succeed;
    }
    
    public void subscribeOnAdd(PropertyChangeListener listener) {
        onConversationAdd.addPropertyChangeListener(listener);
    }

    public void subscribeOnRemove(PropertyChangeListener listener) {
        onConversationRemove.addPropertyChangeListener(listener);
    }
}
