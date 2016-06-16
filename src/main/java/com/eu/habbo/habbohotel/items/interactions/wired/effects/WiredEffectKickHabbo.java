package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.threading.runnables.RoomUnitKick;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectKickHabbo extends InteractionWiredEffect {

    public static final WiredEffectType type = WiredEffectType.KICK_USER;

    private String message = "";

    public WiredEffectKickHabbo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectKickHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (room == null) {
            return false;
        }

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            if (!habbo.hasPermission("acc_unkickable") && habbo.getHabboInfo().getId() != room.getOwnerId()) {
                room.giveEffect(habbo, 4);

                if (!this.message.isEmpty()) {
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(this.message, habbo, habbo, RoomChatMessageBubbles.ALERT)));
                }

                Emulator.getThreading().run(new RoomUnitKick(habbo, room, true), 2000);

                return true;
            }
        }

        return false;
    }

    @Override
    public String getWiredData() {
        return this.message;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.message = set.getString("wired_data");
    }

    @Override
    public void onPickUp() {
        this.message = "";
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message) {
        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.message);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        this.message = packet.readString();

        return true;
    }
}
