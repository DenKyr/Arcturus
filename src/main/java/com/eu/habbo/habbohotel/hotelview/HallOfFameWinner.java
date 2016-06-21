package com.eu.habbo.habbohotel.hotelview;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HallOfFameWinner implements Comparable<HallOfFameWinner> {

    /**
     * Habbo ID
     */
    private final int id;

    /**
     * Name
     */
    private final String username;

    /**
     * Look
     */
    private final String look;

    /**
     * Score
     */
    private final int points;

    public HallOfFameWinner(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.username = set.getString("username");
        this.look = set.getString("look");
        this.points = set.getInt("hof_points");
    }

    /**
     * Habbo ID
     *
     * @return ID of winner
     */
    public int getId() {
        return this.id;
    }

    /**
     * Name
     *
     * @return Username of winner
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Look
     *
     * @return Look of winner
     */
    public String getLook() {
        return this.look;
    }

    /**
     * Score
     *
     * @return Score of winner
     */
    public int getPoints() {
        return this.points;
    }

    @Override
    public int compareTo(HallOfFameWinner o) {
        return o.getPoints() - this.points;
    }
}
