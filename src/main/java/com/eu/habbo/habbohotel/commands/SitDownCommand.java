package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;

import java.util.NoSuchElementException;

public class SitDownCommand extends Command {

    public SitDownCommand() {
        super("cmd_sitdown", Emulator.getTexts().getValue("commands.keys.cmd_sitdown").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        TIntObjectMap<Habbo> habboList = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentHabbos();
        TIntObjectIterator<Habbo> habboIterator = habboList.iterator();

        for (int i = habboList.size(); i-- > 0;) {
            try {
                habboIterator.advance();
            } catch (NoSuchElementException e) {
                break;
            }
            if (habboIterator.value().getRoomUnit().isWalking()) {
                habboIterator.value().getRoomUnit().stopWalking();
            } else if (habboIterator.value().getRoomUnit().getStatus().containsKey("sit")) {
                continue;
            }

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().makeSit(habboIterator.value());
        }

        return true;
    }
}
