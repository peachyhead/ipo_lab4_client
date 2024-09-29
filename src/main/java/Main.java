import base.SignalBus;
import core.ConversationStorage;
import data.ClientCode;
import handler.Client;
import handler.UIService;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;

public class Main {
    
    private final static ConversationStorage conversationStorage = new ConversationStorage();
    private static JPanel connectionPanel;
    private static MainFrame mainFrame;

    public static void main(String[] args) {
        mainFrame = new MainFrame(conversationStorage);
        var uiService = new UIService(mainFrame);
        uiService.initialize();
        mainFrame.initialize();
        
        setupClient(mainFrame);
    }

    private static void setupClient(MainFrame mainFrame) {
        connectionPanel = new JPanel();

        connectionPanel.setLayout(new GridBagLayout());
        var contentPane = mainFrame.getContentPane();
        var c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        var addressField = new JTextField();
        var portField = new JTextField();
        var connectionButton = new JButton("Connect");

        contentPane.add(connectionPanel);
        connectionPanel.setBackground(Color.GRAY);
        connectionPanel.add(addressField, c);
        c.gridy += 1;
        connectionPanel.add(portField, c);
        c.gridy += 1;
        connectionPanel.add(connectionButton, c);
        connectionPanel.revalidate();

        var client = getClient(connectionButton);

        connectionButton.addActionListener(e -> {
            InetAddress address;
            try {
                address = InetAddress.getByName(addressField.getText());
                client.launch(address, Integer.parseInt(portField.getText()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static Client getClient(JButton connectionButton) {
        var client = new Client(conversationStorage);

        client.subscribeOnConnection(evt -> {
            var status = (int) evt.getNewValue();
            switch (status) {
                case 404: {
                    connectionButton.setText("❌ No such host");
                    connectionButton.setEnabled(true);
                    break;
                }
                case 200: {
                    connectionButton.setEnabled(false);
                    connectionButton.setText("⏳ Resolved...");
                    mainFrame.remove(connectionPanel);
                    mainFrame.revalidate();
                    SignalBus.fire(ClientCode.openConversationPanel, "");
                    break;
                }
            }
        });
        return client;
    }
}