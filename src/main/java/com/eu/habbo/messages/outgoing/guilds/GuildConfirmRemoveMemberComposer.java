package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildConfirmRemoveMemberComposer extends MessageComposer {

    private final int userId;
    private final int furniCount;

    public GuildConfirmRemoveMemberComposer(int userId, int furniCount) {
        this.userId = userId;
        this.furniCount = furniCount;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.GuildConfirmRemoveMemberComposer);
        this.response.appendInt32(this.userId);
        this.response.appendInt32(this.furniCount);
        return this.response;
    }
}
