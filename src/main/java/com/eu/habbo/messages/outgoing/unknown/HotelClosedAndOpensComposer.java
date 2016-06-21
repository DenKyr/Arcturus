package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HotelClosedAndOpensComposer extends MessageComposer {

    private final int hour;
    private final int minute;

    public HotelClosedAndOpensComposer(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.HotelClosedAndOpensComposer);
        this.response.appendInt32(this.hour);
        this.response.appendInt32(this.minute);
        return this.response;
    }
}
