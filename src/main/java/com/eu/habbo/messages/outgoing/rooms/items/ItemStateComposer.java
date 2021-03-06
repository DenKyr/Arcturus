package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ItemStateComposer extends MessageComposer {

    private final HabboItem item;

    public ItemStateComposer(HabboItem item) {
        this.item = item;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.ItemStateComposer);
        this.response.appendInt32(this.item.getId());
        try {
            int state = Integer.valueOf(this.item.getExtradata());
            this.response.appendInt32(state);
        } catch (Exception e) {
            this.response.appendInt32(0);
        }

        return this.response;
    }
}
