package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class BlockAlertCommand extends Command {

    public BlockAlertCommand() {
        super("cmd_blockalert", Emulator.getTexts().getValue("commands.keys.cmd_blockalert").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            gameClient.getHabbo().getHabboStats().blockStaffAlerts = !gameClient.getHabbo().getHabboStats().blockStaffAlerts;
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_blockalert").replace("%state%", (gameClient.getHabbo().getHabboStats().blockStaffAlerts ? Emulator.getTexts().getValue("generic.on") : Emulator.getTexts().getValue("generic.off"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

            return true;
        }

        return false;
    }
}
