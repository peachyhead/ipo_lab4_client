package handler;

import base.SignalBus;
import core.ConversationStorage;
import core.MediaModel;
import core.MediaStorage;
import data.ServerRequestCode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static handler.XMLHandler.saveImageFromXMLData;

public class ServerResponseProcessor {
    
    private final ConversationStorage conversationStorage;

    public ServerResponseProcessor(ConversationStorage conversationStorage) {
        this.conversationStorage = conversationStorage;
    }

    public void process(String request, List<String> args) {
        if (request.equals(ServerRequestCode.imageReceive)) {
            if (onImageReceive(args)) return;
        }
        if (request.equals(ServerRequestCode.clientHandshake)) {
            if (args.isEmpty()) return;
            SignalBus.fire(ServerRequestCode.clientHandshake, args.getFirst());
        }
        if (request.equals(ServerRequestCode.imageLoadedOnServer)) {
            if (onImageLoadedOnServer(args)) return;
        }
        if (request.equals(ServerRequestCode.clientsUpdate)) {
            onClientsUpdate(args);
        }
    }

    private void onClientsUpdate(List<String> args) {
        var currentConversations = conversationStorage.stream().toList();
        var clients = Arrays.stream(args.getFirst().split(",")).toList();
        for (var conversation : currentConversations) {
            if (clients.stream().noneMatch(item -> item.equals(conversation.getClientID()))) {
                conversationStorage.remove(conversation);
            }
        }
        for (String id : clients) {
            if (currentConversations.stream().noneMatch(item -> item.getClientID().equals(id)) &&
                    !id.equals(Client.getId())) {
                conversationStorage.add(new MediaStorage(id));
            }
        }
    }

    private boolean onImageLoadedOnServer(List<String> args) {
        if (args.isEmpty()) return true;
        var mediaID = args.getFirst();
        if (mediaID.isEmpty()) return true;
        var receiver = conversationStorage.getLoading(mediaID);
        var storage = conversationStorage.stream()
                .filter(item -> item.getClientID().equals(receiver)).findFirst();
        if (storage.isEmpty()) return true;
        var media = storage.get().stream()
                .filter(item -> item.getId().equals(mediaID)).findFirst();
        if (media.isEmpty()) return true;
        media.get().setUploaded(true);
        SignalBus.fire(ServerRequestCode.imageSend, String.format("%s|%s", mediaID, receiver));
        return false;
    }

    private boolean onImageReceive(List<String> args) {
        if (args.isEmpty()) return true;
        var senderID = args.getFirst();
        var mediaID = args.stream().skip(1).findFirst();
        var mediaData = args.stream().skip(2).findFirst();
        if (mediaID.isEmpty() || mediaData.isEmpty()) return true;
        var storage = conversationStorage.stream()
                .filter(item -> item.getClientID().equals(senderID)).findFirst();
        if (storage.isEmpty()) return true;
        saveImageFromXMLData(mediaData.get(), mediaID.get());
        var media = new MediaModel(mediaID.get(), mediaID.get());
        storage.get().add(media);
        return false;
    }
}
