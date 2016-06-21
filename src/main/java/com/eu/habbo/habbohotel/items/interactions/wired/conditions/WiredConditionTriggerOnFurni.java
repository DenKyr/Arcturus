package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.util.pathfinding.PathFinder;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionTriggerOnFurni extends InteractionWiredCondition {

    public static final WiredConditionType type = WiredConditionType.TRIGGER_ON_FURNI;

    private final THashSet<HabboItem> items = new THashSet<HabboItem>();

    public WiredConditionTriggerOnFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionTriggerOnFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.refresh();

        if (this.items.isEmpty()) {
            return true;
        }

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo == null) {
            return true;
        }

        for (HabboItem item : this.items) {
            if (PathFinder.getSquare(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()).contains(roomUnit.getX(), roomUnit.getY())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getWiredData() {
        this.refresh();

        String data = "";

        for (HabboItem item : this.items) {
            data += item.getId() + ";";
        }

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();

        String[] data = set.getString("wired_data").split(";");

        for (String s : data) {
            this.items.add(room.getHabboItem(Integer.valueOf(s)));
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message) {
        this.refresh();

        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(this.items.size());

        for (HabboItem item : this.items) {
            message.appendInt32(item.getId());
        }

        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        this.items.clear();

        packet.readInt();
        packet.readString();

        int count = packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room != null) {
            for (int i = 0; i < count; ++i) {
                this.items.add(room.getHabboItem(packet.readInt()));
            }
        }

        return true;
    }

    private void refresh() {
        THashSet<HabboItem> itemsl = new THashSet<HabboItem>();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            itemsl.addAll(this.items);
        } else {
            for (HabboItem item : this.items) {
                if (room.getHabboItem(item.getId()) == null) {
                    itemsl.add(item);
                }
            }
        }

        for (HabboItem item : itemsl) {
            this.items.remove(item);
        }
    }
}
