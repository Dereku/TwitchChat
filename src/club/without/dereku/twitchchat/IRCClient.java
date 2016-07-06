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

import org.jibble.pircbot.PircBot;

/**
 *
 * @author Dereku
 */
public class IRCClient extends PircBot {

    private final TwitchChat plugin;

    public IRCClient(TwitchChat aThis) {
        this.plugin = aThis;
    }

    public IRCClient() {
        this.plugin = null;
    }
    
    @Override
    public void log(String line) {
        if (!this.getVerbose()) {
            return;
        }
        
        if (line.contains(">>>PASS oauth:")) {
            line = ">>>PASS oauth:supermegasecretoathkey";
        }
        if (this.plugin == null) {
            System.out.println(line);
            return;
        }
        this.plugin.getLogger().info(line);
    }

    @Override
    protected void onMessage(String channel, String displayName, String user, String hostname, String message) {
        System.out.println("[" + channel + "] <" + displayName + "> " + message);
    }
    
    protected void onMessage(ChatEntry entry) {
        if (this.plugin == null) {
            this.onMessage(entry.getChannel(), entry.getDisplayName(), entry.getUsername(), "", entry.getMessage());
            return;
        }
        this.plugin.onMessage(entry);
    }

    @Override
    protected void onUnknown(String line) {
        if (line.startsWith("@badges=")) {
            this.onMessage(new ChatEntry(line));
        }
    }
}
