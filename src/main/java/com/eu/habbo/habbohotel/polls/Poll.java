package com.eu.habbo.habbohotel.polls;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Poll {

    private final int id;
    private final String title;
    private final String thanksMessage;
    private final String badgeReward;

    private final THashMap<Integer, PollQuestion> questions;

    public Poll(ResultSet set) throws SQLException {
        set.first();
        this.id = set.getInt("poll_id");
        this.title = set.getString("title");
        this.thanksMessage = set.getString("thanks_message");
        this.badgeReward = set.getString("reward_badge");

        this.questions = new THashMap<Integer, PollQuestion>();

        set.beforeFirst();
        while (set.next()) {
            this.questions.put(set.getInt("question_number"), new PollQuestion(set));
        }
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getThanksMessage() {
        return this.thanksMessage;
    }

    public String getBadgeReward() {
        return this.badgeReward;
    }

    public THashMap<Integer, PollQuestion> getQuestions() {
        return this.questions;
    }
}
