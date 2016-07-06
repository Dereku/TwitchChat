/*
 * Copyright (C) 2016 Dereku
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package club.without.dereku.twitchchat;

import java.util.HashMap;

/**
 *
 * @author Dereku
 */
public class ChatEntry {

    private final String username;
    private final String displayName;
    private final String channel;
    private final String message;
    private final boolean mod;
    private final boolean turbo;
    private final boolean subscriber;

    public ChatEntry(String line) {
        String[] input = line.split(";");
        HashMap<String, String> values = new HashMap<>();
        for (String arg : input) {
            String[] tmparr = arg.split("=");
            values.put(tmparr[0], tmparr.length > 1 ? tmparr[1] : null);
        }

        String[] tmparr = values.get("user-type").split(":");
        String tmp = line.substring(line.indexOf("user-type="));
        tmp = tmp.substring(tmp.indexOf(" #"));
        this.username = tmparr[1].substring(0, tmparr[1].indexOf("!"));
        this.channel = tmparr[1].substring(tmparr[1].indexOf("PRIVMSG") + 9).trim();
        this.message = tmp.substring(tmp.indexOf(":") + 1);
        this.displayName = values.get("display-name");
        this.mod = values.get("mod").equals("1");
        this.turbo = values.get("turbo").equals("1");
        this.subscriber = values.get("subscriber").equals("1");
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName == null ? username : displayName;
    }

    /**
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the mod
     */
    public boolean isMod() {
        return mod;
    }

    /**
     * @return the turbo
     */
    public boolean isTurbo() {
        return turbo;
    }

    /**
     * @return the subscriber
     */
    public boolean isSubscriber() {
        return subscriber;
    }
}
