package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectForwardToRoom extends InteractionWiredEffect {

    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    private int roomId = 0;

    public WiredEffectForwardToRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectForwardToRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message) {
        message.appendBoolean(false);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.roomId + "");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(type.code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        try {
            this.roomId = Integer.valueOf(packet.readString());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo == null) {
            return false;
        }

        if (this.roomId > 0) {
            habbo.getClient().sendResponse(new ForwardToRoomComposer(this.roomId));
        }

        return true;
    }

    @Override
    protected String getWiredData() {
        return this.getDelay() + "\t" + this.roomId;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wireData = set.getString("wired_data");
        this.roomId = 0;

        if (wireData.split("\t").length >= 2) {
            super.setDelay(Integer.valueOf(wireData.split("\t")[0]));

            try {
                this.roomId = Integer.valueOf(getWiredData().split("\t")[1]);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onPickUp() {
        this.roomId = 0;
    }
}
