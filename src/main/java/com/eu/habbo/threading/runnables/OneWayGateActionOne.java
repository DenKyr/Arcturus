package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

public class OneWayGateActionOne implements Runnable {

    private HabboItem oneWayGate;
    private Room room;
    private GameClient client;

    public OneWayGateActionOne(GameClient client, Room room, HabboItem item) {
        this.oneWayGate = item;
        this.room = room;
        this.client = client;
    }

    @Override
    public void run() {
        //this.room.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());

        Tile t = PathFinder.getSquareInFront(this.oneWayGate.getX(), this.oneWayGate.getY(), (this.oneWayGate.getRotation() + 4) % 8);

        if (this.room.tileWalkable(t)) {
            this.client.getHabbo().getRoomUnit().isTeleporting = false;
            this.client.getHabbo().getRoomUnit().setGoalLocation(t);

            Emulator.getThreading().run(new HabboItemNewState(this.oneWayGate, this.room, "0"), 1000);
        }
    }
}
