package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectLowerFurni extends InteractionWiredEffect {

    public static final WiredEffectType type = WiredEffectType.TELEPORT;

    private THashSet<HabboItem> items = new THashSet<HabboItem>();

    private int offset = 0;

    public WiredEffectLowerFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectLowerFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message) {
        THashSet<HabboItem> itemsl = new THashSet<HabboItem>();

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null) {
                itemsl.add(item);
            }
        }

        for (HabboItem item : itemsl) {
            this.items.remove(item);
        }
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
        message.appendInt32(type.code);
        message.appendInt32(0);
        message.appendInt32(this.offset);
        message.appendInt32(0);
        message.appendString("");
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();
        packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for (int i = 0; i < count; i++) {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
        }

        this.offset = packet.readInt();

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        for (HabboItem item : this.items) {
            if (item.getRoomId() == 0) {
                continue;
            }

            if (item.getZ() > 0) {
                double z = (item.getZ() - (0.1) * (double) offset);
                double minZ = room.getLayout().getHeightAtSquare(item.getX(), item.getY());
                if (z < minZ) {
                    z = minZ;
                }

                room.sendComposer(new FloorItemOnRollerComposer(item, null, new Tile(item.getX(), item.getY(), z), room).compose());
            }
        }

        return true;
    }

    @Override
    protected String getWiredData() {
        String wiredData = offset + "\t";

        if (items != null && !items.isEmpty()) {
            for (HabboItem item : this.items) {
                wiredData += item.getId() + ";";
            }
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items = new THashSet<HabboItem>();
        String wiredData = set.getString("wired_data");

        if (wiredData.contains("\t")) {
            String[] data = wiredData.split("\t");

            try {
                this.offset = Integer.valueOf(data[0]);
            } catch (Exception e) {
            }

            if (data[1].contains(";")) {
                for (String s : data[1].split(";")) {
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
        this.offset = 0;
        this.items.clear();
    }
}
