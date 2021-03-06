package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserDataComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;

public class MimicCommand extends Command {

    public MimicCommand() {
        super("cmd_mimic", Emulator.getTexts().getValue("commands.keys.cmd_mimic").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length == 2) {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

            if (habbo == gameClient.getHabbo()) {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_mimic.not_self"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            } else if (habbo.hasPermission("acc_not_mimiced") && !gameClient.getHabbo().hasPermission("acc_not_mimiced")) {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_mimic.blocked").replace("%user%", params[1]).replace("%gender_name%", (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? Emulator.getTexts().getValue("gender.his") : Emulator.getTexts().getValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            } else {
                gameClient.getHabbo().getHabboInfo().setLook(habbo.getHabboInfo().getLook());
                gameClient.getHabbo().getHabboInfo().setGender(habbo.getHabboInfo().getGender());
                gameClient.sendResponse(new UserDataComposer(gameClient.getHabbo()));
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDataComposer(gameClient.getHabbo()).compose());
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_mimic.copied").replace("%user%", params[1]).replace("%gender_name%", (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? Emulator.getTexts().getValue("gender.his") : Emulator.getTexts().getValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        } else {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_mimic.not_found").replace("%user%", ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
    }
}
