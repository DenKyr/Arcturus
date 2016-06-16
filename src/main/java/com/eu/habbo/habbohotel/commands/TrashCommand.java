package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.threading.runnables.RoomTrashing;

public class TrashCommand extends Command {

    public TrashCommand() {
        super("cmd_trash", Emulator.getTexts().getValue("commands.keys.cmd_trash").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().isPublicRoom()) {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_trash.public_room"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }

            if (RoomTrashing.INSTANCE != null) {
                if (RoomTrashing.INSTANCE.getHabbo() == gameClient.getHabbo() && RoomTrashing.INSTANCE.getRoom() == gameClient.getHabbo().getHabboInfo().getCurrentRoom()) {
                    RoomTrashing.INSTANCE.setHabbo(null);

                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage("Lulz mode de-activated |", gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

                    return true;
                }
            }

            RoomTrashing roomTrashing = new RoomTrashing(gameClient.getHabbo(), gameClient.getHabbo().getHabboInfo().getCurrentRoom());

            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage("Lulz mode activated |", gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

            return true;
        }

        return false;
    }
}
