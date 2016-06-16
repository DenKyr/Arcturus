package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserShoutComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;

import java.util.NoSuchElementException;

public class ShoutAllCommand extends Command {

    public ShoutAllCommand() {
        super("cmd_shout_all", Emulator.getTexts().getValue("commands.keys.cmd_shout_all").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length < 2) {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_shout_all.forgot_message"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        String message = "";
        if (params.length > 1) {
            for (int i = 1; i < params.length; i++) {
                message += params[i] + " ";
            }
        }

        TIntObjectMap<Habbo> habboList = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentHabbos();
        TIntObjectIterator<Habbo> habboIterator = habboList.iterator();

        for (int i = habboList.size(); i-- > 0;) {
            try {
                habboIterator.advance();
            } catch (NoSuchElementException e) {
                break;
            }
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserShoutComposer(new RoomChatMessage(message, habboIterator.value(), habboIterator.value(), RoomChatMessageBubbles.NORMAL)).compose());
        }

        return true;
    }
}
