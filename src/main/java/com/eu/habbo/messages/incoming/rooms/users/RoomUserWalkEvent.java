package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTask;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.util.pathfinding.Tile;

public class RoomUserWalkEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int x = this.packet.readInt();
        int y = this.packet.readInt();

        Habbo habbo = this.client.getHabbo();
        RoomUnit roomUnit = this.client.getHabbo().getRoomUnit();

        if (roomUnit.isTeleporting) {
            return;
        }

        if (roomUnit.getCacheable().get("control") != null) {
            habbo = (Habbo) roomUnit.getCacheable().get("control");

            if (habbo.getHabboInfo().getCurrentRoom() != this.client.getHabbo().getHabboInfo().getCurrentRoom()) {
                habbo.getRoomUnit().getCacheable().remove("controller");
                this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                habbo = this.client.getHabbo();
            }
        }

        roomUnit = habbo.getRoomUnit();

        try {
            if (roomUnit != null && roomUnit.isInRoom() && roomUnit.canWalk()) {
                if (!roomUnit.cmdTeleport) {
                    if (habbo.getHabboInfo().getRiding() != null && habbo.getHabboInfo().getRiding().getTask().equals(PetTask.JUMP)) {
                        return;
                    }

                    if (x == roomUnit.getX() && y == roomUnit.getY()) {
                        return;
                    }

                    roomUnit.setGoalLocation(x, y);
                } else {
                    roomUnit.stopWalking();
                    Tile t = new Tile(x, y, habbo.getHabboInfo().getCurrentRoom().getTopHeightAt(x, y));
                    habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUnitOnRollerComposer(roomUnit, null, t, habbo.getHabboInfo().getCurrentRoom()).compose());

                    if (habbo.getHabboInfo().getRiding() != null) {
                        habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUnitOnRollerComposer(habbo.getHabboInfo().getRiding().getRoomUnit(), null, t, habbo.getHabboInfo().getCurrentRoom()).compose());
                    }
                }
            }
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }
}
