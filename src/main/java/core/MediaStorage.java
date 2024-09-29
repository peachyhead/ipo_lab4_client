package core;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MediaStorage extends ArrayList<MediaModel> {
    @Getter
    private final String clientID;

    private final Action addAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    
    public MediaStorage(String clientID) {
        this.clientID = clientID;
    }
    
    @Override
    public boolean add(MediaModel model) {
        var succeed = super.add(model);
        if (succeed)
            addAction.putValue("add", model);
        return succeed;
    }

    public void subscribeOnAdd(PropertyChangeListener listener) {
        addAction.addPropertyChangeListener(listener);
    }
}
