package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildBoughtComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.plugin.events.guilds.GuildPurchasedEvent;

public class RequestGuildBuyEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.getHabbo().getHabboInfo().addCredits(-10);
        this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));

        String name = this.packet.readString();
        String description = this.packet.readString();

        int roomId = this.packet.readInt();

        Room r = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if (r != null) {
            if (r.getOwnerId() == this.client.getHabbo().getHabboInfo().getId()) {
                if (r.getGuildId() == 0) {
                    int colorOne = this.packet.readInt();
                    int colorTwo = this.packet.readInt();

                    int count = this.packet.readInt();

                    String badge = "";

                    byte base = 1;

                    while (base < count) {
                        int id = this.packet.readInt();
                        int color = this.packet.readInt();
                        int pos = this.packet.readInt();

                        if (base == 1) {
                            badge += "b";
                        } else {
                            badge += "s";
                        }

                        badge += (id < 100 ? "0" : "") + (id < 10 ? "0" : "") + id + (color < 10 ? "0" : "") + color + "" + pos;

                        base += 3;
                    }

                    Guild guild = Emulator.getGameEnvironment().getGuildManager().createGuild(this.client.getHabbo(), roomId, name, description, badge, colorOne, colorTwo);
                    r.setGuild(guild.getId());
                    r.setNeedsUpdate(true);
                    this.client.sendResponse(new PurchaseOKComposer());
                    this.client.sendResponse(new GuildBoughtComposer(guild));
                    r.refreshGuild(guild);

                    Emulator.getPluginManager().fireEvent(new GuildPurchasedEvent(guild, this.client.getHabbo()));

                    Emulator.getGameEnvironment().getGuildManager().addGuild(guild);
                }
            } else {
                String message = Emulator.getTexts().getValue("scripter.warning.guild.buy.owner").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%roomname%", r.getName().replace("%owner%", r.getOwnerName()));
                Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", message);
                Emulator.getLogging().logUserLine(message);
            }
        }
    }
}
