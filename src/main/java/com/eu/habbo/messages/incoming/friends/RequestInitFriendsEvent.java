package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.util.ArrayList;

public class RequestInitFriendsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();
//
//        messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
//        messages.add(new FriendsComposer(this.client.getHabbo()).compose());
        this.client.sendResponses(messages);
    }
}
