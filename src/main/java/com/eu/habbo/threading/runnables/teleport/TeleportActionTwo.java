package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class TeleportActionTwo implements Runnable {

    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionTwo(HabboItem currentTeleport, Room room, GameClient client) {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run() {
        this.currentTeleport.setExtradata("2");
        this.room.updateItem(this.currentTeleport);

        if (((InteractionTeleport) this.currentTeleport).getTargetRoomId() > 0 && ((InteractionTeleport) this.currentTeleport).getTargetId() > 0) {
            HabboItem item = room.getHabboItem(((InteractionTeleport) this.currentTeleport).getTargetId());
            if (item == null) {
                ((InteractionTeleport) this.currentTeleport).setTargetRoomId(0);
                ((InteractionTeleport) this.currentTeleport).setTargetId(0);
            } else if (((InteractionTeleport) item).getTargetRoomId() != ((InteractionTeleport) this.currentTeleport).getTargetRoomId()) {
                ((InteractionTeleport) this.currentTeleport).setTargetId(0);
                ((InteractionTeleport) this.currentTeleport).setTargetRoomId(0);
                ((InteractionTeleport) item).setTargetId(0);
                ((InteractionTeleport) item).setTargetRoomId(0);
            }
        } else {
            ((InteractionTeleport) this.currentTeleport).setTargetRoomId(0);
            ((InteractionTeleport) this.currentTeleport).setTargetId(0);
        }
        if (((InteractionTeleport) this.currentTeleport).getTargetId() == 0) {
            try {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT items_teleports.*, A.room_id as a_room_id, A.id as a_id, B.room_id as b_room_id, B.id as b_id FROM items_teleports INNER JOIN items AS A ON items_teleports.teleport_one_id = A.id INNER JOIN items AS B ON items_teleports.teleport_two_id = B.id  WHERE (teleport_one_id = ? OR teleport_two_id = ?)");
                statement.setInt(1, this.currentTeleport.getId());
                statement.setInt(2, this.currentTeleport.getId());

                ResultSet set = statement.executeQuery();

                if (set.next()) {
                    if (set.getInt("a_id") != this.currentTeleport.getId()) {
                        ((InteractionTeleport) this.currentTeleport).setTargetId(set.getInt("a_id"));
                        ((InteractionTeleport) this.currentTeleport).setTargetRoomId(set.getInt("a_room_id"));
                    } else {
                        ((InteractionTeleport) this.currentTeleport).setTargetId(set.getInt("b_id"));
                        ((InteractionTeleport) this.currentTeleport).setTargetRoomId(set.getInt("b_room_id"));
                    }
                }

                set.close();
                statement.close();
                statement.getConnection().close();

            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }

        if (((InteractionTeleport) this.currentTeleport).getTargetRoomId() == 0) {
            Emulator.getThreading().run(new TeleportActionFour(this.currentTeleport, this.room, this.client), 500);
            return;
        }

        Emulator.getThreading().run(new TeleportActionThree(this.currentTeleport, this.room, this.client), 500);
    }
}
