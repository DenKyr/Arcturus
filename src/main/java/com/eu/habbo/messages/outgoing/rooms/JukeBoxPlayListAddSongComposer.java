package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class JukeBoxPlayListAddSongComposer extends MessageComposer {

    private final SoundTrack track;

    public JukeBoxPlayListAddSongComposer(SoundTrack track) {
        this.track = track;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.JukeBoxPlayListAddSongComposer);
        this.response.appendInt32(this.track.getId());
        this.response.appendInt32(this.track.getLength() * 1000);
        this.response.appendString(this.track.getCode());
        this.response.appendString(this.track.getAuthor());
        return this.response;
    }
}
