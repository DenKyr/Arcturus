package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomCreatedComposer extends MessageComposer {

    private final Room room;

    public RoomCreatedComposer(Room room) {
        this.room = room;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomCreatedComposer);
        this.response.appendInt32(this.room.getId());
        this.response.appendString(this.room.getName());
        return this.response;
    }
}
