package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotFurniHaveFurni extends InteractionWiredCondition {

    public static final WiredConditionType type = WiredConditionType.NOT_FURNI_HAVE_FURNI;

    private boolean all;
    private final THashSet<HabboItem> items;

    public WiredConditionNotFurniHaveFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredConditionNotFurniHaveFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.refresh();

        if (this.items.isEmpty()) {
            return true;
        }

        for (HabboItem item : this.items) {
            THashSet<HabboItem> things = room.getItemsAt(item.getX(), item.getY());

            for (HabboItem i : things) {
                if (i == item) {
                    continue;
                }

                if (i.getZ() >= item.getZ()) {
                    if (this.all) {
                        return false;
                    }
                } else {
                    if (!this.all) {
                        return true;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData() {
        this.refresh();

        String data = (all ? "1" : "0") + ":";

        for (HabboItem item : this.items) {
            data += item.getId() + ";";
        }

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();

        String[] data = set.getString("wired_data").split(":");

        if (data.length >= 1) {
            this.all = (data[0].equals("1"));

            if (data.length == 2) {
                String[] itemsl = data[1].split(";");

                for (String s : itemsl) {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if (item != null) {
                        this.items.add(item);
                    }
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.all = false;
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
        message.appendInt32(1);
        message.appendInt32(this.all ? 1 : 0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        this.items.clear();

        int count;
        packet.readInt();

        this.all = packet.readInt() == 1;

        packet.readString();

        count = packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room != null) {
            for (int i = 0; i < count; i++) {
                HabboItem item = room.getHabboItem(packet.readInt());

                if (item != null) {
                    this.items.add(item);
                }
            }

            return true;
        }
        return false;
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
