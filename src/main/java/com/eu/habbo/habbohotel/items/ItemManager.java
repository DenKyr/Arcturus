package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.*;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.*;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.*;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootball;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.*;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.*;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.*;
import com.eu.habbo.habbohotel.items.interactions.games.icetag.*;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.*;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.*;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.NoSuchElementException;

public class ItemManager {

    private final TIntObjectMap<Item> items;
    private final TIntObjectHashMap<CrackableReward> crackableRewards;
    private final THashSet<ItemInteraction> interactionsList;
    private final THashMap<String, SoundTrack> soundTracks;

    public ItemManager() {
        this.items = TCollections.synchronizedMap(new TIntObjectHashMap<Item>());
        this.crackableRewards = new TIntObjectHashMap<CrackableReward>();
        this.interactionsList = new THashSet<ItemInteraction>();
        this.soundTracks = new THashMap<String, SoundTrack>();
    }

    public void load() {
        Emulator.getPluginManager().fireEvent(new EmulatorLoadItemsManagerEvent());

        long millis = System.currentTimeMillis();

        this.loadItemInteractions();
        this.loadItems();
        this.loadCrackable();
        this.loadSoundTracks();

        Emulator.getLogging().logStart("Item Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    protected void loadItemInteractions() {
        this.interactionsList.add(new ItemInteraction("default", InteractionDefault.class));
        this.interactionsList.add(new ItemInteraction("gate", InteractionGate.class));
        this.interactionsList.add(new ItemInteraction("guild_furni", InteractionGuildFurni.class));
        this.interactionsList.add(new ItemInteraction("guild_gate", InteractionGuildGate.class));
        this.interactionsList.add(new ItemInteraction("background_toner", InteractionBackgroundToner.class));
        this.interactionsList.add(new ItemInteraction("badge_display", InteractionBadgeDisplay.class));
        this.interactionsList.add(new ItemInteraction("mannequin", InteractionMannequin.class));
        this.interactionsList.add(new ItemInteraction("ads_bg", InteractionRoomAds.class));
        this.interactionsList.add(new ItemInteraction("trophy", InteractionTrophy.class));
        this.interactionsList.add(new ItemInteraction("vendingmachine", InteractionVendingMachine.class));
        this.interactionsList.add(new ItemInteraction("pressureplate", InteractionPressurePlate.class));
        this.interactionsList.add(new ItemInteraction("multiheight", InteractionMultiHeight.class));
        this.interactionsList.add(new ItemInteraction("dice", InteractionDice.class));
        this.interactionsList.add(new ItemInteraction("colorwheel", InteractionColorWheel.class));
        this.interactionsList.add(new ItemInteraction("cannon", InteractionCannon.class));
        this.interactionsList.add(new ItemInteraction("teleport", InteractionTeleport.class));
        this.interactionsList.add(new ItemInteraction("crackable", InteractionCrackable.class));
        this.interactionsList.add(new ItemInteraction("nest", InteractionNest.class));
        this.interactionsList.add(new ItemInteraction("pet_drink", InteractionPetDrink.class));
        this.interactionsList.add(new ItemInteraction("pet_food", InteractionPetFood.class));
        this.interactionsList.add(new ItemInteraction("obstacle", InteractionObstacle.class));
        this.interactionsList.add(new ItemInteraction("monsterplant_seed", InteractionMonsterPlantSeed.class));
        this.interactionsList.add(new ItemInteraction("gift", InteractionGift.class));
        this.interactionsList.add(new ItemInteraction("stack_helper", InteractionStackHelper.class));
        this.interactionsList.add(new ItemInteraction("puzzle_box", InteractionPuzzleBox.class));
        this.interactionsList.add(new ItemInteraction("hopper", InteractionHopper.class));
        this.interactionsList.add(new ItemInteraction("costume_hopper", InteractionCostumeHopper.class));
        this.interactionsList.add(new ItemInteraction("club_hopper", InteractionHabboClubHopper.class));
        this.interactionsList.add(new ItemInteraction("club_gate", InteractionHabboClubGate.class));
        this.interactionsList.add(new ItemInteraction("onewaygate", InteractionOneWayGate.class));
        this.interactionsList.add(new ItemInteraction("love_lock", InteractionLoveLock.class));
        this.interactionsList.add(new ItemInteraction("clothing", InteractionClothing.class));
        this.interactionsList.add(new ItemInteraction("roller", InteractionRoller.class));
        this.interactionsList.add(new ItemInteraction("postit", InteractionPostIt.class));
        this.interactionsList.add(new ItemInteraction("dimmer", InteractionMoodLight.class));
        this.interactionsList.add(new ItemInteraction("rentable_space", InteractionRentableSpace.class));
        this.interactionsList.add(new ItemInteraction("pyramid", InteractionPyramid.class));
        this.interactionsList.add(new ItemInteraction("musicdisc", InteractionMusicDisc.class));
        this.interactionsList.add(new ItemInteraction("fireworks", InteractionFireworks.class));
        this.interactionsList.add(new ItemInteraction("talking_furni", InteractionTalkingFurniture.class));
        this.interactionsList.add(new ItemInteraction("water_item", InteractionWaterItem.class));
        this.interactionsList.add(new ItemInteraction("water", InteractionWater.class));
        this.interactionsList.add(new ItemInteraction("viking_cotie", InteractionVikingCotie.class));
        this.interactionsList.add(new ItemInteraction("tile_fxprovider_nfs", InteractionTileEffect.class));
        this.interactionsList.add(new ItemInteraction("mutearea", InteractionMuteArea.class));
        this.interactionsList.add(new ItemInteraction("information_terminal", InteractionInformationTerminal.class));

        /*
         Wireds
         */
        /*
         Triggers
         */
        this.interactionsList.add(new ItemInteraction("wf_trg_walks_on_furni", WiredTriggerHabboWalkOnFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_walks_off_furni", WiredTriggerHabboWalkOffFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_enter_room", WiredTriggerHabboEntersRoom.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_says_something", WiredTriggerHabboSaysKeyword.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_periodically", WiredTriggerRepeater.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_period_long", WiredTriggerRepeaterLong.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_state_changed", WiredTriggerFurniStateToggled.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_at_given_time", WiredTriggerAtSetTime.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_at_time_long", WiredTriggerAtTimeLong.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_collision", WiredTriggerCollision.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_game_starts", WiredTriggerGameStarts.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_game_ends", WiredTriggerGameEnds.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_bot_reached_stf", WiredTriggerBotReachedFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_trg_bot_reached_avtr", WiredTriggerBotReachedHabbo.class));

        /*
         Effects
         */
        this.interactionsList.add(new ItemInteraction("wf_act_toggle_state", WiredEffectToggleFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_reset_timers", WiredEffectResetTimers.class));
        this.interactionsList.add(new ItemInteraction("wf_act_match_to_sshot", WiredEffectMatchFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_move_rotate", WiredEffectMoveRotateFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_score", WiredEffectGiveScore.class));
        this.interactionsList.add(new ItemInteraction("wf_act_show_message", WiredEffectWhisper.class));
        this.interactionsList.add(new ItemInteraction("wf_act_teleport_to", WiredEffectTeleport.class));
        this.interactionsList.add(new ItemInteraction("wf_act_join_team", WiredEffectJoinTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_act_leave_team", WiredEffectLeaveTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_act_chase", WiredEffectMoveFurniTowards.class));
        this.interactionsList.add(new ItemInteraction("wf_act_flee", WiredEffectMoveFurniAway.class));
        this.interactionsList.add(new ItemInteraction("wf_act_move_to_dir", WiredEffectChangeFurniDirection.class));
        this.interactionsList.add(new ItemInteraction("wf_act_give_score_tm", WiredEffectGiveScoreToTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_act_toggle_to_rnd", WiredEffectToggleRandom.class));
        //TODO: this.interactionsList.add(new ItemInteraction("wf_act_move_furni_to", WiredEffectMoveFurni))
        this.interactionsList.add(new ItemInteraction("wf_act_give_reward", WiredEffectGiveReward.class));
        this.interactionsList.add(new ItemInteraction("wf_act_call_stacks", WiredEffectTriggerStacks.class));
        this.interactionsList.add(new ItemInteraction("wf_act_kick_user", WiredEffectKickHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_mute_triggerer", WiredEffectMuteHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_teleport", WiredEffectBotTeleport.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_move", WiredEffectBotWalkToFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_talk", WiredEffectBotTalk.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_give_handitem", WiredEffectBotGiveHandItem.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_follow_avatar", WiredEffectBotFollowHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_clothes", WiredEffectBotClothes.class));
        this.interactionsList.add(new ItemInteraction("wf_act_bot_talk_to_avatar", WiredEffectBotTalkToHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_give_user_diamonds", WiredEffectGiveDiamonds.class));
        this.interactionsList.add(new ItemInteraction("wf_give_user_xcredits", WiredEffectGiveCredits.class));
        this.interactionsList.add(new ItemInteraction("wf_give_user_xduckets", WiredEffectGiveDuckets.class));
        this.interactionsList.add(new ItemInteraction("wf_act_forward_user", WiredEffectForwardToRoom.class));
        this.interactionsList.add(new ItemInteraction("wf_act_roller_speed", WiredEffectRollerSpeed.class));
        this.interactionsList.add(new ItemInteraction("wf_act_raise_furni", WiredEffectRaiseFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_act_lower_furni", WiredEffectLowerFurni.class));

        /*
         Conditions
         */
        this.interactionsList.add(new ItemInteraction("wf_cnd_has_furni_on", WiredConditionFurniHaveFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_furnis_hv_avtrs", WiredConditionFurniHaveHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_stuff_is", WiredConditionFurniTypeMatch.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_actor_in_group", WiredConditionGroupMember.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_user_count_in", WiredConditionHabboCount.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_wearing_effect", WiredConditionHabboHasEffect.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_wearing_badge", WiredConditionHabboWearsBadge.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_time_less_than", WiredConditionLessTimeElapsed.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_match_snapshot", WiredConditionMatchStatePosition.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_time_more_than", WiredConditionMoreTimeElapsed.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_furni_on", WiredConditionNotFurniHaveFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_hv_avtrs", WiredConditionNotFurniHaveHabbo.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_stuff_is", WiredConditionNotFurniTypeMatch.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_user_count", WiredConditionNotHabboCount.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_wearing_fx", WiredConditionNotHabboHasEffect.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_wearing_b", WiredConditionNotHabboWearsBadge.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_in_group", WiredConditionNotInGroup.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_in_team", WiredConditionNotInTeam.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_match_snap", WiredConditionNotMatchStatePosition.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_not_trggrer_on", WiredConditionNotTriggerOnFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_actor_in_team", WiredConditionTeamMember.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_trggrer_on_frn", WiredConditionTriggerOnFurni.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_has_handitem", WiredConditionHabboHasHandItem.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_date_rng_active", WiredConditionDateRangeActive.class));
        this.interactionsList.add(new ItemInteraction("wf_cnd_motto_contains", WiredConditionMottoContains.class));

        /*
         Highscores
         */
        this.interactionsList.add(new ItemInteraction("wf_highscore", InteractionWiredHighscore.class));

        /*
         Battle Banzai
         */
        /*
         Battle Banzai Items
         */
        //battlebanzai_pyramid
        //battlebanzai_puck extends pushable
        this.interactionsList.add(new ItemInteraction("battlebanzai_timer", InteractionBattleBanzaiTimer.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_tile", InteractionBattleBanzaiTile.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_random_teleport", InteractionBattleBanzaiTeleporter.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_sphere", InteractionBattleBanzaiSphere.class));

        /*
         Battle Banzai Gates
         */
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_blue", InteractionBattleBanzaiGateBlue.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_green", InteractionBattleBanzaiGateGreen.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_red", InteractionBattleBanzaiGateRed.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_gate_yellow", InteractionBattleBanzaiGateYellow.class));

        /*
         Battle Banzai Scoreboards
         */
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_blue", InteractionBattleBanzaiScoreboardBlue.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_green", InteractionBattleBanzaiScoreboardGreen.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_red", InteractionBattleBanzaiScoreboardRed.class));
        this.interactionsList.add(new ItemInteraction("battlebanzai_counter_yellow", InteractionBattleBanzaiScoreboardYellow.class));

        /*
         Freeze
         */
        /*
         Freeze Items
         */
        this.interactionsList.add(new ItemInteraction("freeze_block", InteractionFreezeBlock.class));
        this.interactionsList.add(new ItemInteraction("freeze_tile", InteractionFreezeTile.class));
        this.interactionsList.add(new ItemInteraction("freeze_exit", InteractionFreezeExitTile.class));
        this.interactionsList.add(new ItemInteraction("freeze_timer", InteractionFreezeTimer.class));

        /*
         Freeze Gates
         */
        this.interactionsList.add(new ItemInteraction("freeze_gate_blue", InteractionFreezeGateBlue.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_green", InteractionFreezeGateGreen.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_red", InteractionFreezeGateRed.class));
        this.interactionsList.add(new ItemInteraction("freeze_gate_yellow", InteractionFreezeGateYellow.class));

        /*
         Freeze Scoreboards
         */
        this.interactionsList.add(new ItemInteraction("freeze_counter_blue", InteractionFreezeScoreboardBlue.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_green", InteractionFreezeScoreboardGreen.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_red", InteractionFreezeScoreboardRed.class));
        this.interactionsList.add(new ItemInteraction("freeze_counter_yellow", InteractionFreezeScoreboardYellow.class));

        /*
         Ice Tag
         */
        this.interactionsList.add(new ItemInteraction("icetag_pole", InteractionIceTagPole.class));

        /*
         Football
         */
        this.interactionsList.add(new ItemInteraction("football", InteractionFootball.class));
    }

    /**
     * Adds an new ItemInteraction to the interaction list.
     *
     * @param itemInteraction The ItemInteraction that must be added.
     */
    public void addItemInteraction(ItemInteraction itemInteraction) {
        for (ItemInteraction interaction : this.interactionsList) {
            if (interaction.getType() == itemInteraction.getType()
                    || interaction.getName().equalsIgnoreCase(itemInteraction.getName())) {
                throw new RuntimeException("Interaction Types must be unique. An class with type: " + interaction.getClass().getName() + " was already added OR the key: " + interaction.getName() + " is already in use.");
            }
        }

        this.interactionsList.add(itemInteraction);
    }

    /**
     * Gets that correct ItemInteraction class for the given Interaction.
     *
     * @param type The Interaction that must be found.
     * @return The ItemInteraction definition for the given class.
     */
    public ItemInteraction getItemInteraction(Class<? extends HabboItem> type) {
        for (ItemInteraction interaction : this.interactionsList) {
            if (interaction.getType() == type) {
                return interaction;
            }
        }

        Emulator.getLogging().logDebugLine("Can't find interaction class:" + type.getName());
        return getItemInteraction(InteractionDefault.class);
    }

    /**
     * Gets that correct ItemInteraction class for the given Interaction.
     *
     * @param type The Interaction that must be found.
     * @return The ItemInteraction definition for the given class.
     */
    public ItemInteraction getItemInteraction(String type) {
        for (ItemInteraction interaction : this.interactionsList) {
            if (interaction.getName().equalsIgnoreCase(type)) {
                return interaction;
            }
        }

        return this.getItemInteraction(InteractionDefault.class);
    }

    /**
     * Loads all items from the database. Updates the item definition when
     * needed.
     *
     * Note: Does not change the interaction type. Emulator requires a restart
     * for that.
     */
    public void loadItems() {
        // proxyItem;
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM items_base ORDER BY id DESC");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                try {
                    //Item proxyItem =
                    int id = set.getInt("id");
                    if (!this.items.containsKey(id)) {
                        this.items.put(id, new Item(set));
                    } else {
                        this.items.get(id).update(set);
                    }
                } catch (Exception e) {
                    Emulator.getLogging().logErrorLine(e);
                }
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Loads the crackable items and their rewards from the database.
     */
    public void loadCrackable() {
        this.crackableRewards.clear();
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM items_crackable");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                try {
                    this.crackableRewards.put(set.getInt("item_id"), new CrackableReward(set));
                } catch (Exception e) {
                    Emulator.getLogging().logErrorLine("Failed to load items_crackable item_id = " + set.getInt("ïtem_id"));
                }
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    /**
     * Gets the amount of clicks required for an crackable item.
     *
     * @param itemId The Item id.
     * @return The amount of clicks required.
     */
    public int getCrackableCount(int itemId) {
        if (this.crackableRewards.containsKey(itemId)) {
            return this.crackableRewards.get(itemId).count;
        } else {
            return 0;
        }
    }

    /**
     * Calculates the current state of the crackable item depending on the
     * amount of ticks.
     *
     * @param count The current amount of ticks.
     * @param max The amount of ticks needed.
     * @return State of the crackable.
     */
    public int calculateCrackState(int count, int max) {
        return (int) Math.floor((1.0D / ((double) max / (double) count) * 14.0D));
    }

    public CrackableReward getCrackableData(int itemId) {
        return this.crackableRewards.get(itemId);
    }

    public Item getCrackableReward(int itemId) {
        return this.getItem(this.crackableRewards.get(itemId).getRandomReward());
    }

    /**
     * Loads soundtracks from the database.
     */
    public void loadSoundTracks() {
        this.soundTracks.clear();

        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM soundtracks");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                this.soundTracks.put(set.getString("code"), new SoundTrack(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public SoundTrack getSoundTrack(String code) {
        return this.soundTracks.get(code);
    }

    public SoundTrack getSoundTrack(int id) {
        for (Map.Entry<String, SoundTrack> entry : this.soundTracks.entrySet()) {
            if (entry.getValue().getId() == id) {
                return entry.getValue();
            }
        }

        return null;
    }

    public HabboItem createItem(Habbo habbo, CatalogItem catalogItem, Item item, String extraData) {
        return createItem(habbo.getHabboInfo().getId(), item, catalogItem.getLimitedStack(), catalogItem.getLimitedSells(), extraData);
    }

    public HabboItem createItem(int habboId, Item item, int limitedStack, int limitedSells, String extraData) {
        PreparedStatement statement = null;
        ResultSet set = null;
        try {
            statement = Emulator.getDatabase().prepare("INSERT INTO items (user_id, item_id, extra_data, limited_data) VALUES (?, ?, ?, ?)");
            statement.setInt(1, habboId);
            statement.setInt(2, item.getId());
            statement.setString(3, extraData);
            statement.setString(4, limitedStack + ":" + limitedSells);
            statement.execute();

            set = statement.getGeneratedKeys();
            if (set.next()) {
                Class<? extends HabboItem> itemClass = item.getInteractionType().getType();

                if (itemClass != null) {
                    try {
                        return itemClass.getDeclaredConstructor(int.class, int.class, Item.class, String.class, int.class, int.class).newInstance(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (NoSuchMethodException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (SecurityException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (SQLException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (InstantiationException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (IllegalAccessException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (IllegalArgumentException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    } catch (InvocationTargetException e) {
                        return new InteractionDefault(set.getInt(1), habboId, item, extraData, limitedStack, limitedSells);
                    }
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (SQLException e) {
                    Emulator.getLogging().logSQLException(e);
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                    statement.getConnection().close();
                } catch (SQLException e) {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }
        return null;
    }

    public void deleteItem(HabboItem item) {
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM items WHERE id = ?");
            statement.setInt(1, item.getId());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public HabboItem handleRecycle(Habbo habbo, String itemId) {
        String extradata = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.YEAR);

        HabboItem item = null;
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO items (user_id, item_id, extra_data) VALUES (?, ?, ?)");
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, Emulator.getGameEnvironment().getCatalogManager().ecotronItem.getId());
            statement.setString(3, extradata);
            statement.execute();

            ResultSet set = statement.getGeneratedKeys();

            while (set.next() && item == null) {
                PreparedStatement preparedStatement = Emulator.getDatabase().prepare("INSERT INTO items_presents VALUES (?, ?)");
                preparedStatement.setInt(1, set.getInt(1));
                preparedStatement.setInt(2, Integer.valueOf(itemId));
                preparedStatement.execute();
                preparedStatement.close();
                preparedStatement.getConnection().close();

                item = new InteractionDefault(set.getInt(1), habbo.getHabboInfo().getId(), Emulator.getGameEnvironment().getCatalogManager().ecotronItem, extradata, 0, 0);
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        return item;
    }

    public HabboItem handleOpenRecycleBox(Habbo habbo, HabboItem box) {
        Emulator.getThreading().run(new QueryDeleteHabboItem(box));
        HabboItem item = null;
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM items_presents WHERE item_id = ? LIMIT 1");
            statement.setInt(1, box.getId());
            ResultSet rewardSet = statement.executeQuery();

            if (rewardSet.next()) {
                PreparedStatement preparedStatement = Emulator.getDatabase().prepare("INSERT INTO items (user_id, item_id) VALUES(?, ?)");
                preparedStatement.setInt(1, habbo.getHabboInfo().getId());
                preparedStatement.setInt(2, rewardSet.getInt("base_item_reward"));
                preparedStatement.execute();

                ResultSet set = preparedStatement.getGeneratedKeys();

                if (set.next()) {
                    PreparedStatement request = Emulator.getDatabase().prepare("SELECT * FROM items WHERE id = ? LIMIT 1");
                    request.setInt(1, set.getInt(1));

                    ResultSet resultSet = request.executeQuery();

                    if (resultSet.next()) {
                        PreparedStatement deleteStatement = Emulator.getDatabase().prepare("DELETE FROM items_presents WHERE item_id = ? LIMIT 1");
                        deleteStatement.setInt(1, box.getId());
                        deleteStatement.execute();

                        item = loadHabboItem(resultSet);
                        deleteStatement.close();
                        deleteStatement.getConnection().close();
                    }
                    resultSet.close();
                    request.close();
                    request.getConnection().close();
                }
                set.close();
                preparedStatement.close();
                preparedStatement.getConnection().close();
            }
            rewardSet.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }

        return item;
    }

    public void insertTeleportPair(int itemOneId, int itemTwoId) {
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO items_teleports VALUES (?, ?)");
            statement.setInt(1, itemOneId);
            statement.setInt(2, itemTwoId);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void insertHopper(HabboItem hopper) {
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO items_hoppers VALUES (?, ?)");
            statement.setInt(1, hopper.getId());
            statement.setInt(2, hopper.getBaseItem().getId());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public int[] getTargetTeleportRoomId(HabboItem item) {
        int[] a = new int[]{};

        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT items.id, items.room_id FROM items_teleports INNER JOIN items ON items_teleports.teleport_one_id = items.id OR items_teleports.teleport_two_id = items.id WHERE items.id != ? AND items.room_id > 0 LIMIT 1");
            statement.setInt(1, item.getId());
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                a = new int[]{set.getInt("room_id"), set.getInt("id")};
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        return a;
    }

    public HabboItem loadHabboItem(int itemId) {
        HabboItem item = null;
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM items WHERE id = ? LIMIT 1");
            statement.setInt(1, itemId);

            ResultSet set = statement.executeQuery();

            if (set.next()) {
                item = this.loadHabboItem(set);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }

        return item;
    }

    public HabboItem loadHabboItem(ResultSet set) throws SQLException {
        Item baseItem = this.getItem(set.getInt("item_id"));

        if (baseItem == null) {
            return null;
        }

        Class<? extends HabboItem> itemClass = baseItem.getInteractionType().getType();

        if (itemClass != null) {
            try {

                Constructor c = itemClass.getConstructor(ResultSet.class, Item.class);
                c.setAccessible(true);

                return (HabboItem) c.newInstance(set, baseItem);
            } catch (NoSuchMethodException e) {
                Emulator.getLogging().logErrorLine(e);
            } catch (SecurityException e) {
                Emulator.getLogging().logErrorLine(e);
            } catch (InstantiationException e) {
                Emulator.getLogging().logErrorLine(e);
            } catch (IllegalAccessException e) {
                Emulator.getLogging().logErrorLine(e);
            } catch (IllegalArgumentException e) {
                Emulator.getLogging().logErrorLine(e);
            } catch (InvocationTargetException e) {
                Emulator.getLogging().logErrorLine(e);
            }
        }

        return null;
    }

    public HabboItem createGift(String username, Item item, String extraData, int limitedStack, int limitedSells) {
        HabboItem gift = null;
        int userId = 0;
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

        if (habbo != null) {
            userId = habbo.getHabboInfo().getId();
        } else {
            try {
                PreparedStatement s = Emulator.getDatabase().prepare("SELECT id FROM users WHERE username = ?");
                s.setString(1, username);

                ResultSet set = s.executeQuery();

                if (set.next()) {
                    userId = set.getInt(1);
                }

                set.close();
                s.close();
                s.getConnection().close();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }

        if (userId == 0) {
            return null;
        }

        PreparedStatement statement;
        try {
            statement = Emulator.getDatabase().prepare("INSERT INTO items (user_id, item_id, extra_data, limited_data) VALUES (?, ?, ?, ?)");
            statement.setInt(1, userId);
            statement.setInt(2, item.getId());
            statement.setString(3, extraData);
            statement.setString(4, limitedStack + ":" + limitedSells);
            statement.execute();

            ResultSet set = statement.getGeneratedKeys();

            if (set.next()) {
                gift = new InteractionGift(set.getInt(1), userId, item, extraData, limitedStack, limitedSells);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        if (gift != null) {
            if (habbo != null) {
                habbo.getHabboInventory().getItemsComponent().addItem(gift);

                habbo.getClient().sendResponse(new AddHabboItemComposer(gift));
            }
        }

        return gift;
    }

    public Item getItem(int itemId) {
        if (itemId < 0) {
            return null;
        }

        return this.items.get(itemId);
    }

    public TIntObjectMap<Item> getItems() {
        return this.items;
    }

    public Item getItem(String itemName) {
        TIntObjectIterator<Item> item = this.items.iterator();

        for (int i = this.items.size(); i-- > 0;) {
            try {
                item.advance();
                if (item.value().getName().toLowerCase().equals(itemName.toLowerCase())) {
                    return item.value();
                }
            } catch (NoSuchElementException e) {
                break;
            }
        }

        return null;
    }

    public void dispose() {
        this.items.clear();

        Emulator.getLogging().logShutdownLine("Item Manager -> Disposed!");
    }

}
