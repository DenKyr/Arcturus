package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUnitUpdateUsernameComposer extends MessageComposer {

    private final RoomUnit roomUnit;
    private final String name;

    public RoomUnitUpdateUsernameComposer(RoomUnit roomUnit, String name) {
        this.roomUnit = roomUnit;
        this.name = name;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomUnitUpdateUsernameComposer);
        this.response.appendInt32(this.roomUnit.getId());
        this.response.appendInt32(this.roomUnit.getId());
        this.response.appendString(this.name);
        return this.response;
    }
}
