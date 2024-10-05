package handler;

import base.SignalBus;
import core.ConversationStorage;
import core.MediaModel;
import core.MediaStorage;
import data.ServerRequestCode;

import java.util.Arrays;
import java.util.List;

import static handler.XMLHandler.deserializeVisualObject;

public class ServerResponseProcessor {
    
    private final ConversationStorage conversationStorage;

    public ServerResponseProcessor(ConversationStorage conversationStorage) {
        this.conversationStorage = conversationStorage;
    }

    public void process(String request, List<String> args) {
        if (request.equals(ServerRequestCode.visualReceive)) {
            if (onVisualReceive(args)) return;
        }
        if (request.equals(ServerRequestCode.clientHandshake)) {
            if (args.isEmpty()) return;
            SignalBus.fire(ServerRequestCode.clientHandshake, args.getFirst());
        }
        if (request.equals(ServerRequestCode.visualLoadedOnServer)) {
            if (onVisualLoadedOnServer(args)) return;
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

    private boolean onVisualLoadedOnServer(List<String> args) {
        if (args.isEmpty()) return true;
        var mediaID = args.getFirst();
        if (mediaID.isEmpty()) return true;
        var receiver = conversationStorage.getLoading(mediaID);
        var storage = conversationStorage.stream()
                .filter(item -> item.getClientID().equals(receiver)).findFirst();
        if (storage.isEmpty()) return true;
        var media = storage.get().stream()
                .filter(item -> item.id().equals(mediaID)).findFirst();
        if (media.isEmpty()) return true;
        SignalBus.fire(ServerRequestCode.visualSend, String.format("%s|%s", mediaID, receiver));
        return false;
    }

    private boolean onVisualReceive(List<String> args) {
        if (args.isEmpty()) return true;
        var senderID = args.getFirst();
        var mediaID = args.stream().skip(1).findFirst();
        var mediaData = args.stream().skip(2).findFirst();
        if (mediaID.isEmpty() || mediaData.isEmpty()) return true;
        var storage = conversationStorage.stream()
                .filter(item -> item.getClientID().equals(senderID)).findFirst();
        if (storage.isEmpty()) return true;
        var visualObject = deserializeVisualObject(mediaData.get());
        var media = new MediaModel(mediaID.get(), visualObject);
        storage.get().add(media);
        return false;
    }
}
