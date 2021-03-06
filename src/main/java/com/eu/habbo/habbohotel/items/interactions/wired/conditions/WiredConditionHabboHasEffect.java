package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionHabboHasEffect extends InteractionWiredCondition {

    public static final WiredConditionType type = WiredConditionType.ACTOR_WEARS_EFFECT;

    private int effectId = 0;

    public WiredConditionHabboHasEffect(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionHabboHasEffect(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return roomUnit.getEffectId() == this.effectId;
    }

    @Override
    public String getWiredData() {
        return effectId + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.effectId = Integer.valueOf(set.getString("wired_data"));
    }

    @Override
    public void onPickUp() {
        this.effectId = 0;
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message) {
        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.effectId + "");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        this.effectId = packet.readInt();

        return true;
    }
}
