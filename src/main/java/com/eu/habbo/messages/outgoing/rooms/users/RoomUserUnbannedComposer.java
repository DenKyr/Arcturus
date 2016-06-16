package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUserUnbannedComposer extends MessageComposer {

    private final Room room;
    private final int userId;

    public RoomUserUnbannedComposer(Room room, int userId) {
        this.room = room;
        this.userId = userId;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomUserUnbannedComposer);
        this.response.appendInt32(this.room.getId());
        this.response.appendInt32(this.userId);
        return this.response;
    }
}
