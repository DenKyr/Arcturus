package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveScoreForTeam implements Runnable {

    public final GameTeam team;
    public final Game game;

    public SaveScoreForTeam(GameTeam team, Game game) {
        this.team = team;
        this.game = game;
    }

    @Override
    public void run() {
        PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO room_game_scores (room_id, game_start_timestamp, game_name, user_id, team_id, score, team_score) VALUES (?, ?, ?, ?, ?, ?, ?)");

        try {
            for (GamePlayer player : this.team.getMembers()) {
                statement.setInt(1, this.game.getRoom().getId());
                statement.setInt(2, this.game.getStartTime());
                statement.setString(3, this.game.getClass().getName());
                statement.setInt(4, player.getHabbo().getHabboInfo().getId());
                statement.setInt(5, player.getTeamColor().type);
                statement.setInt(6, player.getScore());
                statement.setInt(7, this.team.getTeamScore());
                statement.execute();
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        } finally {
            try {
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
