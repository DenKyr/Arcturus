package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MeMenuSettingsComposer extends MessageComposer {

    private final Habbo habbo;

    public MeMenuSettingsComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.MeMenuSettingsComposer);

        this.response.appendInt32(this.habbo.getHabboStats().volumeSystem);
        this.response.appendInt32(this.habbo.getHabboStats().volumeFurni);
        this.response.appendInt32(this.habbo.getHabboStats().volumeTrax);
        this.response.appendBoolean(this.habbo.getHabboStats().preferOldChat);
        this.response.appendBoolean(this.habbo.getHabboStats().blockRoomInvites);
        this.response.appendBoolean(this.habbo.getHabboStats().blockCameraFollow);
        this.response.appendInt32(1);
        this.response.appendInt32(this.habbo.getHabboStats().chatColor.getType());

        return this.response;
    }
}
