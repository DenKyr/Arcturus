package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionHabboHasHandItem extends InteractionWiredCondition {

    public static final WiredConditionType type = WiredConditionType.ACTOR_HAS_HANDITEM;

    private int handItem;

    public WiredConditionHabboHasHandItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionHabboHasHandItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
        message.appendString("");
        message.appendInt32(1);
        message.appendInt32(this.handItem);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        this.handItem = packet.readInt();

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return roomUnit.getHandItem() == this.handItem;
    }

    @Override
    protected String getWiredData() {
        return this.handItem + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        try {
            this.handItem = Integer.valueOf(set.getString("wired_data"));
        } catch (SQLException e) {
            Emulator.getLogging().logErrorLine(e);
        } catch (NumberFormatException e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void onPickUp() {
        this.handItem = 0;
    }
}
