package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public class RecyclerLogicComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RecyclerLogicComposer);
        this.response.appendInt32(Emulator.getGameEnvironment().getCatalogManager().prizes.size());
        for (Map.Entry<Integer, THashSet<Item>> map : Emulator.getGameEnvironment().getCatalogManager().prizes.entrySet()) {
            this.response.appendInt32(map.getKey());
            this.response.appendInt32(Integer.valueOf(Emulator.getConfig().getValue("hotel.ecotron.rarity.chance." + map.getKey())));
            this.response.appendInt32(map.getValue().size());
            for (Item item : map.getValue()) {
                this.response.appendString(item.getName());
                this.response.appendInt32(1);
                this.response.appendString(item.getType().toLowerCase());
                this.response.appendInt32(item.getSpriteId());
            }
        }
        return this.response;
    }
}
