package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomAccessDeniedComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomAccessDeniedComposer);

        return this.response;
    }
}
