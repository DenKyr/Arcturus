package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.THashSet;

public class RoomUsersComposer extends MessageComposer {

    private Habbo habbo;
    private TIntObjectMap<Habbo> habbos;
    private Bot bot;
    private THashSet<Bot> bots;

    public RoomUsersComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    public RoomUsersComposer(TIntObjectMap<Habbo> habbos) {
        this.habbos = habbos;
    }

    public RoomUsersComposer(Bot bot) {
        this.bot = bot;
    }

    public RoomUsersComposer(THashSet<Bot> bots, boolean isBot) {
        this.bots = bots;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomUsersComposer);
        if (this.habbo != null) {
            this.response.appendInt32(1);
            this.response.appendInt32(this.habbo.getHabboInfo().getId());
            this.response.appendString(this.habbo.getHabboInfo().getUsername());
            this.response.appendString(this.habbo.getHabboInfo().getMotto());
            this.response.appendString(this.habbo.getHabboInfo().getLook());
            this.response.appendInt32(this.habbo.getRoomUnit().getId()); //Room Unit ID
            this.response.appendInt32(this.habbo.getRoomUnit().getX());
            this.response.appendInt32(this.habbo.getRoomUnit().getY());
            this.response.appendString(this.habbo.getRoomUnit().getZ() + "");
            this.response.appendInt32(this.habbo.getRoomUnit().getBodyRotation().getValue());
            this.response.appendInt32(1);
            this.response.appendString(this.habbo.getHabboInfo().getGender().name().toUpperCase());
            this.response.appendInt32(this.habbo.getHabboStats().guild != 0 ? this.habbo.getHabboStats().guild : -1);
            this.response.appendInt32(this.habbo.getHabboStats().guild != 0 ? 1 : -1);

            String name = "";
            if (this.habbo.getHabboStats().guild != 0) {
                Guild g = Emulator.getGameEnvironment().getGuildManager().getGuild(this.habbo.getHabboStats().guild);

                if (g != null) {
                    name = g.getName();
                }
            }
            this.response.appendString(name);

            this.response.appendString("");
            this.response.appendInt32(this.habbo.getHabboInfo().getAchievementScore());
            this.response.appendBoolean(true);
        } else if (this.habbos != null) {
            this.response.appendInt32(this.habbos.size());
            for (Habbo habbol : this.habbos.valueCollection()) {
                this.response.appendInt32(habbol.getHabboInfo().getId());
                this.response.appendString(habbol.getHabboInfo().getUsername());
                this.response.appendString(habbol.getHabboInfo().getMotto());
                this.response.appendString(habbol.getHabboInfo().getLook());
                this.response.appendInt32(habbol.getRoomUnit().getId()); //Room Unit ID
                this.response.appendInt32(habbol.getRoomUnit().getX());
                this.response.appendInt32(habbol.getRoomUnit().getY());
                this.response.appendString(habbol.getRoomUnit().getZ() + "");
                this.response.appendInt32(habbol.getRoomUnit().getBodyRotation().getValue());
                this.response.appendInt32(1);
                this.response.appendString(habbol.getHabboInfo().getGender().name().toUpperCase());
                this.response.appendInt32(habbol.getHabboStats().guild != 0 ? habbol.getHabboStats().guild : -1);
                this.response.appendInt32(habbol.getHabboStats().guild != 0 ? 1 : -1);
                this.response.appendString(habbol.getHabboStats().guild != 0 ? Emulator.getGameEnvironment().getGuildManager().getGuild(habbol.getHabboStats().guild).getName() : "");
                this.response.appendString("");
                this.response.appendInt32(habbol.getHabboInfo().getAchievementScore());
                this.response.appendBoolean(true);
            }
        } else if (this.bot != null) {
            this.response.appendInt32(1);
            this.response.appendInt32(0 - this.bot.getId());
            this.response.appendString(this.bot.getName());
            this.response.appendString(this.bot.getMotto());
            this.response.appendString(this.bot.getFigure());
            this.response.appendInt32(this.bot.getRoomUnit().getId());
            this.response.appendInt32(this.bot.getRoomUnit().getX());
            this.response.appendInt32(this.bot.getRoomUnit().getY());
            this.response.appendString(this.bot.getRoomUnit().getZ() + "");
            this.response.appendInt32(this.bot.getRoomUnit().getBodyRotation().getValue());
            this.response.appendInt32(4);
            this.response.appendString(this.bot.getGender().name().toUpperCase());
            this.response.appendInt32(this.bot.getOwnerId());
            this.response.appendString(this.bot.getOwnerName());
            this.response.appendInt32(16);
            this.response.appendInt32(0);
            this.response.appendInt32(1);
            this.response.appendInt32(2);
            this.response.appendInt32(3);
            this.response.appendInt32(4);
            this.response.appendInt32(5);
            this.response.appendInt32(6);
            this.response.appendInt32(7);
            this.response.appendInt32(8);
            this.response.appendInt32(9);
            this.response.appendInt32(10);
            this.response.appendInt32(11);
            this.response.appendInt32(12);
            this.response.appendInt32(13);
            this.response.appendInt32(14);
        } else if (this.bots != null) {
            this.response.appendInt32(this.bots.size());
            for (Bot botl : this.bots) {
                this.response.appendInt32(0 - botl.getId());
                this.response.appendString(botl.getName());
                this.response.appendString(botl.getMotto());
                this.response.appendString(botl.getFigure());
                this.response.appendInt32(botl.getRoomUnit().getId());
                this.response.appendInt32(botl.getRoomUnit().getX());
                this.response.appendInt32(botl.getRoomUnit().getY());
                this.response.appendString(botl.getRoomUnit().getZ() + "");
                this.response.appendInt32(botl.getRoomUnit().getBodyRotation().getValue());
                this.response.appendInt32(4);
                this.response.appendString(botl.getGender().name().toUpperCase());
                this.response.appendInt32(botl.getOwnerId());
                this.response.appendString(botl.getOwnerName());
                this.response.appendInt32(16);
                this.response.appendInt32(0);
                this.response.appendInt32(1);
                this.response.appendInt32(2);
                this.response.appendInt32(3);
                this.response.appendInt32(4);
                this.response.appendInt32(5);
                this.response.appendInt32(6);
                this.response.appendInt32(7);
                this.response.appendInt32(8);
                this.response.appendInt32(9);
                this.response.appendInt32(10);
                this.response.appendInt32(11);
                this.response.appendInt32(12);
                this.response.appendInt32(13);
                this.response.appendInt32(14);
            }
        }
        return this.response;
    }
}
