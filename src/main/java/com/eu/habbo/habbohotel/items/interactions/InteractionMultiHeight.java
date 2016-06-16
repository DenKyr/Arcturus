package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMultiHeight extends HabboItem {

    public InteractionMultiHeight(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    public InteractionMultiHeight(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    @Override
    public boolean isWalkable() {
        return this.getBaseItem().allowWalk() || this.getBaseItem().allowSit();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (objects.length > 0) {
            if (objects[0] instanceof Integer && room != null) {
                if (this.getExtradata().length() == 0) {
                    this.setExtradata("0");
                }

                if (this.getBaseItem().getStateCount() > 0) {
                    this.setExtradata("" + (Integer.valueOf(this.getExtradata()) + 1) % this.getBaseItem().getStateCount());
                    this.needsUpdate(true);
                    room.updateItem(this);
                    room.sendComposer(new UpdateStackHeightComposer(this.getX(), this.getY(), this.getBaseItem().getMultiHeights()[Integer.valueOf(this.getExtradata())] * 256.0D).compose());
                }

                if (this.isWalkable()) {
                    THashSet<Habbo> habbos = room.getHabbosOnItem(this);
                    THashSet<RoomUnit> updatedUnits = new THashSet<RoomUnit>();
                    for (Habbo habbo : habbos) {
                        if (habbo.getRoomUnit() == null) {
                            continue;
                        }

                        if (habbo.getRoomUnit().getStatus().containsKey("mv")) {
                            continue;
                        }

                        if (this.getBaseItem().allowSit()) {
                            habbo.getRoomUnit().getStatus().put("sit", this.getBaseItem().getMultiHeights()[Integer.valueOf(this.getExtradata())] * 1.0D + "");
                        } else {
                            habbo.getRoomUnit().setZ(this.getZ() + this.getBaseItem().getMultiHeights()[Integer.valueOf(this.getExtradata())]);
                        }

                        updatedUnits.add(habbo.getRoomUnit());
                    }
                    room.sendComposer(new RoomUserStatusComposer(updatedUnits, true).compose());
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
    }
}
