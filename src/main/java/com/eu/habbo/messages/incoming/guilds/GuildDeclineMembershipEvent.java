package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildMembersComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildRefreshMembersListComposer;
import com.eu.habbo.plugin.events.guilds.GuildDeclinedMembershipEvent;

public class GuildDeclineMembershipEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId()) {
            return;
        }

        guild.decreaseRequestCount();
        Emulator.getGameEnvironment().getGuildManager().removeMember(guild, userId);
        this.client.sendResponse(new GuildMembersComposer(guild, Emulator.getGameEnvironment().getGuildManager().getGuildMembers(guild, 0, 0, ""), this.client.getHabbo(), 0, 0));
        this.client.sendResponse(new GuildRefreshMembersListComposer(guild));

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        Emulator.getPluginManager().fireEvent(new GuildDeclinedMembershipEvent(guild, userId, habbo, this.client.getHabbo()));

        if (habbo != null) {
            Room room = habbo.getHabboInfo().getCurrentRoom();
            if (room != null) {
                if (room.getGuildId() == guildId) {
                    habbo.getClient().sendResponse(new GuildInfoComposer(guild, habbo.getClient(), false, null));
                }
            }
        }
    }
}
