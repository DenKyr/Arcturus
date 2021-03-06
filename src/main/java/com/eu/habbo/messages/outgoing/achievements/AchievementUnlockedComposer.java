package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AchievementUnlockedComposer extends MessageComposer {

    private final Achievement achievement;
    private final Habbo habbo;

    public AchievementUnlockedComposer(Habbo habbo, Achievement achievement) {
        this.achievement = achievement;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.AchievementUnlockedComposer);

        AchievementLevel level = this.achievement.getLevelForProgress(this.habbo.getHabboStats().getAchievementProgress(this.achievement));
        this.response.appendInt32(this.achievement.id);
        this.response.appendInt32(level.level);
        this.response.appendInt32(144);
        this.response.appendString("ACH_" + this.achievement.name + level.level);
        this.response.appendInt32(level.pixels);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(10);
        this.response.appendInt32(21);
        this.response.appendString(level.level > 1 ? "ACH_" + this.achievement.name + (level.level - 1) : "");
        this.response.appendString(this.achievement.category.name());
        this.response.appendBoolean(true);
        return this.response;
    }
}
