package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UserPerksComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.UserPerksComposer);

        this.response.appendInt32(15);

        this.response.appendString("USE_GUIDE_TOOL");
        this.response.appendString("requirement.unfulfilled.helper_level_4");
        this.response.appendBoolean(true);

        this.response.appendString("GIVE_GUIDE_TOURS");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("JUDGE_CHAT_REVIEWS");
        this.response.appendString("requirement.unfulfilled.helper_level_6");
        this.response.appendBoolean(true);

        this.response.appendString("VOTE_IN_COMPETITIONS");
        this.response.appendString("requirement.unfulfilled.helper_level_2");
        this.response.appendBoolean(true);

        this.response.appendString("CALL_ON_HELPERS");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("CITIZEN");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("TRADE");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("HEIGHTMAP_EDITOR_BETA");
        this.response.appendString("requirement.unfulfilled.feature_disabled");
        this.response.appendBoolean(true);

        this.response.appendString("BUILDER_AT_WORK");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("NAVIGATOR_PHASE_ONE_2014");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("CAMERA");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("NAVIGATOR_PHASE_TWO_2014");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("MOUSE_ZOOM");
        this.response.appendString("");
        this.response.appendBoolean(true);

        this.response.appendString("NAVIGATOR_ROOM_THUMBNAIL_CAMERA");
        this.response.appendString("");
        this.response.appendBoolean(Emulator.getConfig().getBoolean("hotel.navigator.camera", false));

        this.response.appendString("HABBO_CLUB_OFFER_BETA");
        this.response.appendString("");
        this.response.appendBoolean(true);

        return this.response;
    }
}
