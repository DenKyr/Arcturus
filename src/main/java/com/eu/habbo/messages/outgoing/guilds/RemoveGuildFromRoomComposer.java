package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RemoveGuildFromRoomComposer extends MessageComposer {

    private final int guildId;

    public RemoveGuildFromRoomComposer(int guildId) {
        this.guildId = guildId;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RemoveGuildFromRoomComposer);
        this.response.appendInt32(this.guildId);
        return this.response;
    }
}
