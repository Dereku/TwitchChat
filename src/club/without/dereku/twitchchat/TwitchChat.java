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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author Dereku
 */
public class TwitchChat extends JavaPlugin {

    private final ArrayList<String> ignoreList = new ArrayList<>();
    private final ArrayList<String> autojoinChannels = new ArrayList<>();
    //Send message to players
    private final ArrayList<UUID> smtp = new ArrayList<>();
    
    private IRCClient client;
    private MessageFormat twitchMessage;
    private String oauthKey, badgeMod, badgeTurbo, badgeSubscriber;
    private boolean broadcastMessage;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.client = new IRCClient(this);
        this.loadSettings();
        this.initConnection();
    }

    @Override
    public void onDisable() {
        this.client.disconnect();
    }
    
    private void initConnection() {
        try {
            this.client.setEncoding("UTF-8");
            this.client.connect("irc.chat.twitch.tv", 6667, this.oauthKey);
            this.client.sendRawLine("CAP REQ :twitch.tv/tags");
            this.autojoinChannels.stream().forEach(chan -> {
                this.client.joinChannel(chan);
            });
        } catch (IOException | IrcException ex) {
            this.getLogger().log(Level.WARNING, "Failed to connect", ex);
        }
    }

    private void loadSettings() {
        if (this.client.isConnected()) {
            this.client.disconnect();
        }
        
        this.autojoinChannels.clear();
        this.ignoreList.clear();
        
        this.reloadConfig();
        ConfigurationSection cs = this.getConfig();
        
        this.broadcastMessage = cs.getBoolean("broadcastMessage");
        
        this.client.setVerbose(cs.getBoolean("verbose"));
        this.client.setName(cs.getString("connection.nick").toLowerCase());
        
        this.oauthKey = cs.getString("connection.oAuthKey");
        
        this.badgeMod = cs.getString("tags.mod");
        this.badgeTurbo = cs.getString("tags.turbo");
        this.badgeSubscriber = cs.getString("tags.subscriber");
       
        cs.getStringList("autojoinChannels").stream().forEach(chan -> {
            this.autojoinChannels.add("#".concat(chan.toLowerCase()));
        });

        cs.getStringList("usersIgnoreList").stream().forEach(user -> {
            this.ignoreList.add(user.toLowerCase());
        });
        
        if (cs.getBoolean("shouldIgnoreYourself")) {
            this.ignoreList.add(this.client.getName().toLowerCase());
        }
        
        this.twitchMessage = new MessageFormat(
                ChatColor.translateAlternateColorCodes('&', cs.getString("twitchChatStyle"))
        );
        
        
    }
    
    public void onMessage(ChatEntry entry) {
        if (this.ignoreList.contains(entry.getUsername())) {
            return;
        }
        
        StringBuilder badges = new StringBuilder();
        if (entry.isMod() || entry.isSubscriber() || entry.isTurbo()) {
            if (entry.isMod()) {
                badges.append(this.badgeMod);
            }
            if (entry.isTurbo()) {
                badges.append(this.badgeTurbo);
            }
            if (entry.isSubscriber()) {
                badges.append(this.badgeSubscriber);
            }
        }
        
        String output = this.twitchMessage.format(new Object[]{
                        entry.getChannel(), 
                        badges.toString(), 
                        entry.getDisplayName(), 
                        entry.getMessage()
                    });
        
//        if (!this.broadcastMessage) {
//            this.smtp.stream().forEach((uuid) -> {
//                this.getServer().getPlayer(uuid).sendMessage(output);
//            });
//        } else {
            this.getServer().broadcastMessage(output);
//        }
    }
}