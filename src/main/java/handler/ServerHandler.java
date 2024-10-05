package handler;

import base.SignalBus;
import core.ConversationStorage;
import core.MediaModel;
import data.ServerRequestCode;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;

import static handler.XMLHandler.deserializeVisualObject;

public class ServerHandler implements Runnable {
    
    private final Socket socket;
    
    @Getter private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    
    private final ConversationStorage conversationStorage;
    
    public ServerHandler(ConversationStorage conversationStorage, 
                         Socket socket) throws IOException {
        this.conversationStorage = conversationStorage;
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                handleRead();
            }
            catch (IOException e) {
                try {
                    System.out.printf("Client %s disconnect", Client.getId());
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                    break;
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void sendToServer(String visualID, String conversationID, String visualData)
            throws IOException {
        var data = MessageFormat.format("{0}|{1}|{2}",
                ServerRequestCode.visualUpload, visualID, visualData);
        conversationStorage.addToLoad(visualID, conversationID);
        var storage = conversationStorage.stream()
                .filter(item -> item.getClientID().equals(conversationID)).findFirst();
        
        if (storage.isPresent()) {
            if (storage.get().stream().anyMatch(item -> item.id().equals(visualID))) {
                SignalBus.fire(ServerRequestCode.visualSend, 
                        String.format("%s|%s", visualID, conversationID));
                return;
            }
            var visualObject = deserializeVisualObject(visualData);
            storage.get().add(new MediaModel(visualID, visualObject));
            outputStream.writeUTF(data);
        }
    }

    public void sendToClient(String mediaID, String receiverID)
            throws IOException {
        var data = MessageFormat.format("{0}|{1}|{2}",
                ServerRequestCode.visualSend, mediaID, receiverID);
        outputStream.writeUTF(data);
    }
    
    private void handleRead() throws IOException {
        if (inputStream.available() > 0) {
            String response = inputStream.readUTF();
            var msg = MessageFormat.format("Got response from server: {0}", response);
            System.out.println(msg);
    
            if (response.isEmpty()) return;
            var args = Arrays.stream(response.split("\\|")).toList();
            if (args.isEmpty()) return;
            var request = args.getFirst();
            var responseHandler = new ServerResponseProcessor(conversationStorage);
            responseHandler.process(request, args.stream().skip(1).toList());
        }
    }
}
