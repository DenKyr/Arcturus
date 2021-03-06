package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RemoveBotComposer extends MessageComposer {

    private final Bot bot;

    public RemoveBotComposer(Bot bot) {
        this.bot = bot;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RemoveBotComposer);
        this.response.appendInt32(this.bot.getId());
        return this.response;
    }
}
