package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProfileFriendsComposer extends MessageComposer {

    private Habbo habbo;
    private final List<MessengerBuddy> lovers = new ArrayList<MessengerBuddy>();
    private final List<MessengerBuddy> friends = new ArrayList<MessengerBuddy>();
    private final List<MessengerBuddy> haters = new ArrayList<MessengerBuddy>();
    private final int userId;

    public ProfileFriendsComposer(THashMap<Integer, THashSet<MessengerBuddy>> map, int userId) {
        try {
            lovers.addAll(map.get(1));
            friends.addAll(map.get(2));
            haters.addAll(map.get(3));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }

        this.userId = userId;
    }

    public ProfileFriendsComposer(Habbo habbo) {
        try {
            this.habbo = habbo;

            for (Map.Entry<Integer, MessengerBuddy> map : this.habbo.getMessenger().getFriends().entrySet()) {
                if (map.getValue().getRelation() == 0) {
                    continue;
                }

                switch (map.getValue().getRelation()) {
                    case 1:
                        this.lovers.add(map.getValue());
                        break;
                    case 2:
                        this.friends.add(map.getValue());
                        break;
                    case 3:
                        this.haters.add(map.getValue());
                        break;
                }
            }
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }

        this.userId = habbo.getHabboInfo().getId();
    }

    @Override
    public ServerMessage compose() {

        try {

            this.response.init(Outgoing.ProfileFriendsComposer);
            this.response.appendInt32(this.userId);

            int total = 0;

            if (!this.lovers.isEmpty()) {
                total++;
            }

            if (!this.friends.isEmpty()) {
                total++;
            }

            if (!this.haters.isEmpty()) {
                total++;
            }

            this.response.appendInt32(total);

            Random random = new Random();

            if (!this.lovers.isEmpty()) {
                int loversIndex = random.nextInt(this.lovers.size());
                this.response.appendInt32(1);
                this.response.appendInt32(this.lovers.size());
                this.response.appendInt32(this.lovers.get(loversIndex).getId());
                this.response.appendString(this.lovers.get(loversIndex).getUsername());
                this.response.appendString(this.lovers.get(loversIndex).getLook());
            }

            if (!friends.isEmpty()) {
                int friendsIndex = random.nextInt(this.friends.size());
                this.response.appendInt32(2);
                this.response.appendInt32(this.friends.size());
                this.response.appendInt32(this.friends.get(friendsIndex).getId());
                this.response.appendString(this.friends.get(friendsIndex).getUsername());
                this.response.appendString(this.friends.get(friendsIndex).getLook());
            }

            if (!this.haters.isEmpty()) {
                int hatersIndex = random.nextInt(this.haters.size());
                this.response.appendInt32(3);
                this.response.appendInt32(this.haters.size());
                this.response.appendInt32(this.haters.get(hatersIndex).getId());
                this.response.appendString(this.haters.get(hatersIndex).getUsername());
                this.response.appendString(this.haters.get(hatersIndex).getLook());
            }
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
        return this.response;
    }
}
