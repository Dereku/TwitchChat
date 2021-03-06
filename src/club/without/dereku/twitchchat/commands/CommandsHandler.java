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
package club.without.dereku.twitchchat.commands;

import club.without.dereku.twitchchat.TwitchChat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Dereku
 */
public class CommandsHandler {

    private final HashMap<String, Command> commands = new HashMap<>();
    private final TwitchChat plugin;
    
    public CommandsHandler(TwitchChat plugin) {
        this.plugin = plugin;
        this.initCommands();
    }

    private void initCommands() {
        String addChannelDesc = "TODO: description for addchannel";
        BiFunction<CommandSender, ArrayList<String>, Boolean> addChannel = (CommandSender commandSender, ArrayList<String> channels) -> {
            ArrayList<String> outMessages = new ArrayList<>();
            channels.stream().forEach(channel -> {
                if (commandSender.hasPermission("twitchchat.addchannel.".concat(channel.toLowerCase()))) {
                    CommandsHandler.this.plugin.addChannel(channel.toLowerCase());
                    commandSender.sendMessage("Channel \"#".concat(channel).concat("\" has added."));
                }
            });
            
            outMessages.stream().forEach(message -> {
                commandSender.sendMessage(message);
            });

            return !outMessages.isEmpty();
        };
        
        Command addChannelCommand = new Command(addChannelDesc, addChannel);
        this.commands.put("addchannel", addChannelCommand);
        ////////////////////////////////////////////////////////////////////////
        
        String removeChannelDesc = "TODO: description for removechannel";
        BiFunction<CommandSender, ArrayList<String>, Boolean> removeChannel = (CommandSender commandSender, ArrayList<String> channels) -> {
            ArrayList<String> outMessages = new ArrayList<>();
            channels.stream().forEach(channel -> {
                if (commandSender.hasPermission("twitchchat.removechannel.".concat(channel.toLowerCase()))) {
                    CommandsHandler.this.plugin.removeChannel(channel.toLowerCase());
                    outMessages.add("Channel \"#".concat(channel).concat("\" has removed."));
                }
            });
            
            outMessages.stream().forEach(message -> {
                commandSender.sendMessage(message);
            });
            
            return !outMessages.isEmpty();
        };
        
        Command removeChannelCommand = new Command(removeChannelDesc, removeChannel);
        this.commands.put("removechannel", removeChannelCommand);
        ////////////////////////////////////////////////////////////////////////
        
        String ignoreListAddDesc = "TODO: description for ignoreListAdd";
        BiFunction<CommandSender, ArrayList<String>, Boolean> ignoreListAdd = (CommandSender commandSender, ArrayList<String> usernames) -> {
            ArrayList<String> outMessages = new ArrayList<>();
            usernames.stream().forEach((String username) -> {
                if (commandSender.hasPermission("twitchchat.ignorelist.add.".concat(username.toLowerCase()))) {
                    CommandsHandler.this.plugin.ignoreListAdd(username.toLowerCase());
                    outMessages.add("User \"".concat(username).concat("\" has ignored."));
                }
            });
            
            outMessages.stream().forEach(message -> {
                commandSender.sendMessage(message);
            });
            
            return !outMessages.isEmpty();
        };
        
        Command ignoreListAddCommand = new Command(ignoreListAddDesc, ignoreListAdd);
        this.commands.put("ignorelistadd", ignoreListAddCommand);
        ////////////////////////////////////////////////////////////////////////
        
        String ignoreListRemoveDesc = "TODO: description for ignoreListRemove";
        BiFunction<CommandSender, ArrayList<String>, Boolean> ignoreListRemove = (CommandSender commandSender, ArrayList<String> usernames) -> {
            ArrayList<String> outMessages = new ArrayList<>();
            usernames.stream().forEach((String username) -> {
                if (commandSender.hasPermission("twitchchat.ignorelist.remove.".concat(username.toLowerCase()))) {
                    CommandsHandler.this.plugin.ignoreListRemove(username.toLowerCase());
                    outMessages.add("User \"".concat(username).concat("\" has no more ignored."));
                }
            });
            
            outMessages.stream().forEach(message -> {
                commandSender.sendMessage(message);
            });
            
            return !outMessages.isEmpty();
        };
        
        Command ignoreListRemoveCommand = new Command(ignoreListRemoveDesc, ignoreListRemove);
        this.commands.put("ignorelistremove", ignoreListRemoveCommand);
        ////////////////////////////////////////////////////////////////////////

        String recieversAddDesc = "TODO: description for ignoreListRemove";
        BiFunction<CommandSender, ArrayList<String>, Boolean> recieversAdd = (CommandSender commandSender, ArrayList<String> usernames) -> {
            ArrayList<String> outMessages = new ArrayList<>();
            usernames.stream().forEach((String username) -> {
                if (commandSender.hasPermission("twitchchat.recievers.add.".concat(username.toLowerCase()))) {
                    CommandsHandler.this.plugin.ignoreListRemove(username.toLowerCase());
                    outMessages.add("User \"".concat(username).concat("\" has no more ignored."));
                }
            });

            outMessages.stream().forEach(message -> {
                commandSender.sendMessage(message);
            });

            return !outMessages.isEmpty();
        };

        Command recieversAddCommand = new Command(recieversAddDesc, recieversAdd);
        this.commands.put("recieversadd", recieversAddCommand);

    }
}
