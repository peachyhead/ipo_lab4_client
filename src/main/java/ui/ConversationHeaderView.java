package ui;

import base.SignalBus;
import data.ClientCode;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
public class ConversationHeaderView extends JPanel {
    private final String id;
    
    public ConversationHeaderView(String id) {
        this.id = id;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        var label = new JLabel(id);
        add(label, c);
    }
    
    public void initialize() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                SignalBus.fire(ClientCode.openMediaPanel, id);
            }
        });
    }
}
