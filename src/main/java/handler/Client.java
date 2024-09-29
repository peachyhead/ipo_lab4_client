package handler;

import base.SignalBus;
import base.SignalListener;
import core.ConversationStorage;
import data.ClientCode;
import data.ServerRequestCode;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

public class Client {
    
    @Getter private static String id;
    
    private Socket socket;
    private ServerHandler handler;
    
    private final ConversationStorage conversationStorage;
    
    public Client(ConversationStorage conversationStorage) {
        this.conversationStorage = conversationStorage;
    }
    
    private final Action connectionListener = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    
    public void launch(InetAddress address, int port) throws IOException {
        try {
            bindSignals();
            socket = new Socket(address, port);
            handler = new ServerHandler(conversationStorage, socket);
            new Thread(handler).start();
            connectionListener.putValue("status", 200);
            System.out.println("Client connected");
        }
        catch (ConnectException connectException) {
            connectionListener.putValue("status", 404);
            System.out.printf("Cant resolve host - %s : %s.", address.toString(), String.valueOf(port));
        }
        catch (IOException e) {
            System.out.printf("Disconnected from %s : %s.", address.toString(), String.valueOf(port));
            try {
                if (socket == null) return;
                socket.close();
                connectionListener.putValue("status", 499);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void bindSignals() {
        SignalBus.subscribe(new SignalListener<String>(ServerRequestCode.clientHandshake, 
                value -> id = value));

        SignalBus.subscribe(new SignalListener<String>(ServerRequestCode.imageSend, 
                inline -> {
                    var args = Arrays.stream(inline.split("\\|")).toList();
                    try {
                        handler.sendToClient(args.getFirst(), args.getLast());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ));

        SignalBus.subscribe(new SignalListener<String>(ClientCode.imageUpload, inline -> {
            var id = UUID.randomUUID().toString();
            try {
                var conversationID = inline.split("\\|")[0];
                var path = inline.split("\\|")[1];
                handler.sendToServer(id, conversationID, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void subscribeOnConnection(PropertyChangeListener listener) {
        connectionListener.addPropertyChangeListener(listener);
    }
}
