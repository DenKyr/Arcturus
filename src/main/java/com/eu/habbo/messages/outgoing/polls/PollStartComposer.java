package com.eu.habbo.messages.outgoing.polls;

import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PollStartComposer extends MessageComposer {

    private final Poll poll;

    public PollStartComposer(Poll poll) {
        this.poll = poll;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.PollStartComposer);
        this.response.appendInt32(this.poll.getId());
        this.response.appendString(this.poll.getTitle());
        this.response.appendString(this.poll.getTitle());
        this.response.appendString(this.poll.getThanksMessage());
        return this.response;
    }
}
