package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.GuildPart;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildPartsComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.GroupPartsComposer);
        this.response.appendInt32(Emulator.getGameEnvironment().getGuildManager().getBases().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getBases()) {
            this.response.appendInt32(part.id);
            this.response.appendString(part.valueA);
            this.response.appendString(part.valueB);
        }

        this.response.appendInt32(Emulator.getGameEnvironment().getGuildManager().getSymbols().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getSymbols()) {
            this.response.appendInt32(part.id);
            this.response.appendString(part.valueA);
            this.response.appendString(part.valueB);
        }

        this.response.appendInt32(Emulator.getGameEnvironment().getGuildManager().getBaseColors().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getBaseColors()) {
            this.response.appendInt32(part.id);
            this.response.appendString(part.valueA);
        }

        this.response.appendInt32(Emulator.getGameEnvironment().getGuildManager().getSymbolColors().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getSymbolColors()) {
            this.response.appendInt32(part.id);
            this.response.appendString(part.valueA);
        }

        this.response.appendInt32(Emulator.getGameEnvironment().getGuildManager().getBackgroundColors().size());
        for (GuildPart part : Emulator.getGameEnvironment().getGuildManager().getBackgroundColors()) {
            this.response.appendInt32(part.id);
            this.response.appendString(part.valueA);
        }

        return this.response;
    }
}
