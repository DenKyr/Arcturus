package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.util.pathfinding.Tile;

public class RoomUnitTeleport implements Runnable {

    private final RoomUnit roomUnit;
    private final Room room;
    private final int x;
    private final int y;
    private final double z;

    private final int newEffect;

    public RoomUnitTeleport(RoomUnit roomUnit, Room room, int x, int y, double z, int newEffect) {
        this.roomUnit = roomUnit;
        this.room = room;
        this.x = x;
        this.y = y;
        this.z = z;
        this.newEffect = newEffect;
    }

    @Override
    public void run() {
        this.roomUnit.setGoalLocation(x, y);
        this.roomUnit.getStatus().remove("mv");

        Tile t = new Tile(x, y, z);

        this.room.sendComposer(new RoomUnitOnRollerComposer(this.roomUnit, null, t, this.room).compose());
        this.room.giveEffect(this.roomUnit, this.newEffect);
        this.room.updateHabbosAt(this.x, this.y);
    }
}
