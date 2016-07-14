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
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author Dereku
 */
public class TwitchChat extends JavaPlugin {

    private final ArrayList<String> ignoreList = new ArrayList<>();
    private final ArrayList<String> currentChannels = new ArrayList<>();
    private final ArrayList<UUID> recievers = new ArrayList<>();

    private IRCClient client;
    private MessageFormat twitchMessage;
    private String name, oauthKey, badgeMod, badgeTurbo, badgeSubscriber;
    private boolean verbose, broadcastMessage, autojoin;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.client = new IRCClient(this);
        this.loadSettings();
        this.initConnection();
        this.getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        this.client.disconnect();
        this.saveSettings();
        this.getLogger().info("Disabled");
    }
    
    public ArrayList<String> getIgnoreList() {
        return this.ignoreList;
    }
    
    public ArrayList<String> getCurrentChannels() {
        return this.currentChannels;
    }
    
    public ArrayList<UUID> getMessageRecievers() {
        return this.recievers;
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
        }).trim();

        if (!this.broadcastMessage) {
            this.recievers.stream().forEach((uuid) -> {
                this.getServer().getPlayer(uuid).sendMessage(output);
            });
        } else {
            this.getServer().broadcastMessage(output);
        }
    }

    public void addChannel(String channel) {
        this.currentChannels.add(channel.toLowerCase());
        this.client.joinChannel(channel.toLowerCase());
    }

    public void removeChannel(String channel) {
        this.currentChannels.remove(channel.toLowerCase());
        this.client.partChannel(channel.toLowerCase());
    }

    public void ignoreListAdd(String username) {
        this.ignoreList.add(username.toLowerCase());
    }

    public void ignoreListRemove(String username) {
        this.ignoreList.remove(username.toLowerCase());
    }
    
    public void addReciever(UUID reciever) {
        this.recievers.add(reciever);
    }
    
    public void removeReciever(UUID reciever) {
        this.recievers.remove(reciever);
    }

    private void initConnection() {
        if (this.client.isConnected()) {
            this.client.disconnect();
        }

        try {
            this.client.setEncoding("UTF-8");
            this.client.setName(this.name);
            this.client.setVerbose(this.verbose);
            this.client.connect("irc.chat.twitch.tv", 6667, this.oauthKey);
            this.client.sendRawLine("CAP REQ :twitch.tv/tags");
            if (this.autojoin) {
                this.joinChannels();
            }
        } catch (IOException | IrcException ex) {
            this.getLogger().log(Level.WARNING, "Failed to connect", ex);
        }
    }
    
    public void joinChannels() {
        synchronized(this.currentChannels) {
            this.currentChannels.stream().forEach(chan -> {
                this.client.joinChannel(chan);
            });
        }
    }
    
    public void partAllChannels() {
        synchronized (this.currentChannels) {
            this.currentChannels.stream().forEach(chan -> {
                this.client.partChannel(chan);
            });
        }
        
        if (this.client.getChannels().length > 0) {
            this.getLogger().info("Woah. We still have channels.");
            
            for (String chan : this.client.getChannels()) {
                this.currentChannels.add(chan);
                this.client.partChannel(chan);
            }
        }
    }

    private void loadSettings() {
        if (this.client.isConnected()) {
            this.client.disconnect();
        }

        this.currentChannels.clear();
        this.ignoreList.clear();

        this.reloadConfig();
        ConfigurationSection cs = this.getConfig();

        this.verbose = cs.getBoolean("verbose");

        this.name = cs.getString("connection.nick").toLowerCase();
        this.oauthKey = cs.getString("connection.oAuthKey");

        this.broadcastMessage = cs.getBoolean("broadcastMessage");
        this.autojoin = cs.getBoolean("autojoin");

        this.badgeMod = ChatColor.translateAlternateColorCodes('&', cs.getString("tags.mod"));
        this.badgeTurbo = ChatColor.translateAlternateColorCodes('&', cs.getString("tags.turbo"));
        this.badgeSubscriber = ChatColor.translateAlternateColorCodes('&', cs.getString("tags.subscriber"));

        cs.getStringList("autojoinChannels").stream().forEach(chan -> {
            this.currentChannels.add("#".concat(chan.toLowerCase()));
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
    
    private void saveSettings() {
        this.getConfig().set("ignoreList", this.getIgnoreList());
        this.getConfig().set("autojoinChannels", this.getCurrentChannels());
        this.getConfig().set("messageRecievers", this.getMessageRecievers());
        this.getConfig().set("broadcastMessage", this.broadcastMessage);
        this.getConfig().set("autojoin", this.autojoin);
        this.getConfig().set("verbose", this.verbose);
        this.saveConfig();
    }
}
