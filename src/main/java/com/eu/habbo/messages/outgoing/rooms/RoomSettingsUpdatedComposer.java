package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomSettingsUpdatedComposer extends MessageComposer {

    private final Room room;

    public RoomSettingsUpdatedComposer(Room room) {
        this.room = room;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomSettingsUpdatedComposer);
        this.response.appendInt32(this.room.getId());
        return this.response;
    }
}
