package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserShoutComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class ShoutCommand extends Command {

    private static String idea = "Kudo's To Droppy for this idea!";

    public ShoutCommand() {
        super("cmd_shout", Emulator.getTexts().getValue("commands.keys.cmd_shout").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length < 2) {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_shout.forgot_username"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

        if (target == null) {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_shout.user_not_found"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        } else {
            if (target.getHabboInfo().getCurrentRoom() == null) {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_shout.hotel_view").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        }

        String message = "";
        if (params.length > 2) {
            for (int i = 2; i < params.length; i++) {
                message += params[i] + " ";
            }
        }

        target.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserShoutComposer(new RoomChatMessage(message, target, RoomChatMessageBubbles.NORMAL)).compose());
        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_shout").replace("%user%", params[1]).replace("%message%", message), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        return true;
    }
}
