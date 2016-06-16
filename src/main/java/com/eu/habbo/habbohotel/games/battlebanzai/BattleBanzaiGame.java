package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventPriority;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.BattleBanzaiTilesFlicker;
import com.eu.habbo.threading.runnables.GameStop;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public class BattleBanzaiGame extends Game {

    /**
     * The effect id BB effects start at.
     */
    public static final int effectId = 33;

    /**
     * Points for hijacking another users tile.
     */
    public static final int POINTS_HIJACK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.steal");

    /**
     * Points for coloring a grey tile.
     */
    public static final int POINTS_FILL_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.fill");

    /**
     * Points for locking a tile.
     */
    public static final int POINTS_LOCK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.lock");

    private boolean running;

    private int timeLeft;

    private int tileCount;

    private int countDown;

    /**
     * All locked tiles.
     */
    private final THashMap<GameTeamColors, THashSet<HabboItem>> lockedTiles;

    public BattleBanzaiGame(Room room) {
        super(BattleBanzaiGameTeam.class, BattleBanzaiGamePlayer.class, room);

        this.lockedTiles = new THashMap<GameTeamColors, THashSet<HabboItem>>();
    }

    @Override
    public void initialise() {
        if (this.running) {
            return;
        }

        int highestTime = 0;
        this.countDown = 3;

        this.resetMap();

        for (Map.Entry<Integer, InteractionBattleBanzaiTimer> set : this.room.getRoomSpecialTypes().getBattleBanzaiTimers().entrySet()) {
            if (set.getValue().getExtradata().isEmpty()) {
                continue;
            }

            if (highestTime < Integer.valueOf(set.getValue().getExtradata())) {
                highestTime = Integer.valueOf(set.getValue().getExtradata());
            }
        }

        for (GameTeam t : this.teams.values()) {
            t.initialise();
        }

        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
            item.setExtradata("1");
            this.room.updateItem(item);
        }

        this.timeLeft = highestTime;

        this.start();
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void start() {
        super.start();

        if (this.running) {
            return;
        }

        this.running = true;

        Emulator.getThreading().run(this, 0);
    }

    @Override
    public synchronized void run() {
        try {
            if (!this.isRunning()) {
                return;
            }

            if (this.countDown > 0) {
                this.countDown--;

                if (this.countDown == 0) {
                    WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, this.room, null);

                    for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
                        item.setExtradata("2");
                        this.room.updateItem(item);
                    }
                }

                if (this.countDown > 1) {
                    Emulator.getThreading().run(this, 500);

                    return;
                }
            }

            if (this.timeLeft > 0) {
                Emulator.getThreading().run(this, 1000);

                this.timeLeft--;

                for (Map.Entry<Integer, InteractionBattleBanzaiTimer> set : this.room.getRoomSpecialTypes().getBattleBanzaiTimers().entrySet()) {
                    set.getValue().setExtradata(timeLeft + "");
                    this.room.updateItem(set.getValue());
                }

                int total = 0;
                for (Map.Entry<GameTeamColors, THashSet<HabboItem>> set : this.lockedTiles.entrySet()) {
                    total += set.getValue().size();
                }

                GameTeam highestScore = null;

                for (Map.Entry<GameTeamColors, GameTeam> set : this.teams.entrySet()) {
                    if (highestScore == null || highestScore.getTotalScore() < set.getValue().getTotalScore()) {
                        highestScore = set.getValue();
                    }
                }

                if (highestScore != null) {
                    for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
                        item.setExtradata((highestScore.teamColor.type + 3) + "");
                        this.room.updateItem(item);
                    }
                }

                if (total >= this.tileCount) {
                    this.timeLeft = 0;
                }
            } else {
                Emulator.getThreading().run(new GameStop(this), 3500);

                GameTeam winningTeam = null;

                for (GameTeam team : this.teams.values()) {
                    for (GamePlayer player : team.getMembers()) {
                        //player.getScore()
                        AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("BattleBallPlayer"));
                        AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("BattleBallQuestCompleted"));
                    }

                    if (winningTeam == null || team.getTotalScore() > winningTeam.getTotalScore()) {
                        winningTeam = team;
                    }
                }

                if (winningTeam != null) {
                    for (GamePlayer player : winningTeam.getMembers()) {
                        this.room.sendComposer(new RoomUserActionComposer(player.getHabbo().getRoomUnit(), 1).compose());
                        AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("BattleBallWinner"));
                    }

                    for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
                        item.setExtradata((7 + winningTeam.teamColor.type) + "");
                        this.room.updateItem(item);
                    }

                    Emulator.getThreading().run(new BattleBanzaiTilesFlicker(this.lockedTiles.get(winningTeam.teamColor), winningTeam.teamColor, this.room));
                }

                this.running = false;
            }
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void stop() {
        super.stop();

        this.timeLeft = 0;

        for (HabboItem item : this.room.getFloorItems()) {
            if (item instanceof InteractionBattleBanzaiTile || item instanceof InteractionBattleBanzaiScoreboard) {
                item.setExtradata("0");
                this.room.updateItem(item);
            }
        }

        this.lockedTiles.clear();

        this.running = false;
    }

    /**
     * Resets the map.
     */
    protected synchronized void resetMap() {
        for (HabboItem item : this.room.getFloorItems()) {
            if (item instanceof InteractionBattleBanzaiTile) {
                item.setExtradata("1");
                this.room.updateItem(item);
                this.tileCount++;
            }

            if (item instanceof InteractionBattleBanzaiScoreboard) {
                item.setExtradata("0");
                this.room.updateItem(item);
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void addPositionToGate(GameTeamColors teamColor) {
        for (InteractionBattleBanzaiGate gate : this.room.getRoomSpecialTypes().getBattleBanzaiGates().values()) {
            if (gate.teamColor != teamColor) {
                continue;
            }

            if (gate.getExtradata().isEmpty() || gate.getExtradata().equals("0")) {
                continue;
            }

            gate.setExtradata(Integer.valueOf(gate.getExtradata()) - 1 + "");
            this.room.updateItem(gate);
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void onUserWalkEvent(UserTakeStepEvent event) {
        if (event.habbo.getHabboInfo().getCurrentGame() == BattleBanzaiGame.class) {
            BattleBanzaiGame game = (BattleBanzaiGame) event.habbo.getHabboInfo().getCurrentRoom().getGame(BattleBanzaiGame.class);
            if (game != null && game.isRunning()) {
                if (!event.habbo.getHabboInfo().getCurrentRoom().hasObjectTypeAt(InteractionBattleBanzaiTile.class, event.toLocation.getX(), event.toLocation.getY())) {
                    event.setCancelled(true);
                    event.habbo.getRoomUnit().setGoalLocation(event.habbo.getRoomUnit().getLocation());
                    event.habbo.getRoomUnit().getStatus().remove("mv");
                    game.room.sendComposer(new RoomUserStatusComposer(event.habbo.getRoomUnit()).compose());
                }
            }
        }
    }

    /**
     * Locks an tile
     *
     * @param teamColor The color to lock.
     * @param item The item to lock.
     * @param habbo The habbo executing action.
     */
    public void tileLocked(GameTeamColors teamColor, HabboItem item, Habbo habbo) {
        if (item instanceof InteractionBattleBanzaiTile) {
            if (!this.lockedTiles.containsKey(teamColor)) {
                this.lockedTiles.put(teamColor, new THashSet<HabboItem>());
            }

            this.lockedTiles.get(teamColor).add(item);
        }

        if (habbo != null) {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().achievements.get("BattleBallTilesLocked"));
        }
    }

    /**
     * Updates the counters in the room.
     */
    public void refreshCounters() {
        for (GameTeam team : this.teams.values()) {
            if (team.getMembers().isEmpty()) {
                continue;
            }

            this.refreshCounters(team.teamColor);
        }
    }

    /**
     * Updates the counters for the given GameTeamColors in the room.
     *
     * @param teamColors The color that should be updated.
     */
    public void refreshCounters(GameTeamColors teamColors) {
        int totalScore = this.teams.get(teamColors).getTotalScore();

        THashMap<Integer, InteractionBattleBanzaiScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getBattleBanzaiScoreboards(teamColors);

        for (InteractionBattleBanzaiScoreboard scoreboard : scoreBoards.values()) {
            if (scoreboard.getExtradata().isEmpty()) {
                scoreboard.setExtradata("0");
            }

            int oldScore = Integer.valueOf(scoreboard.getExtradata());

            if (oldScore == totalScore) {
                continue;
            }

            scoreboard.setExtradata(totalScore + "");
            this.room.updateItem(scoreboard);
        }
    }
}
