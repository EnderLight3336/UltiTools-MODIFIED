package com.minecraft.ultikits.commands;

import com.minecraft.ultikits.enums.ConfigsEnum;
import com.minecraft.ultikits.ultitools.UltiTools;
import com.minecraft.ultikits.utils.GroupManagerUtils;
import com.minecraft.ultikits.views.PermissionMainView;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.minecraft.ultikits.utils.MessagesUtils.info;
import static com.minecraft.ultikits.utils.MessagesUtils.warning;

public class PermissionCommands implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("ultikits.tools.admin")) {
                player.sendMessage(warning(UltiTools.languageUtils.getWords("no_permission")));
                return true;
            }
            switch (args.length) {
                case 0:
                    Inventory inventory = PermissionMainView.setUp();
                    player.openInventory(inventory);
                    return true;
                case 1:
                    if (args[0].equals("help")) sendHelpMessage(player);
                case 2:
                    switch (args[0]) {
                        case "create":
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GroupManagerUtils.createGroup(args[1]);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("created")));
                                }
                            }.runTaskAsynchronously(UltiTools.getInstance());
                            return true;
                        case "delete":
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GroupManagerUtils.deleteGroup(args[1]);
                                    player.sendMessage(warning(UltiTools.languageUtils.getWords("deleted")));
                                }
                            }.runTaskAsynchronously(UltiTools.getInstance());
                            return true;
                        default:
                            return false;
                    }
                case 3:
                    switch (args[0]) {
                        case "add":
                            Player addedPlayer = Bukkit.getPlayerExact(args[1]);
                            if (addedPlayer == null) {
                                player.sendMessage(warning(UltiTools.languageUtils.getWords("player_not_online_or_not_exits")));
                                return true;
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GroupManagerUtils.addPlayerToGroup(addedPlayer, args[2]);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("added")));
                                }
                            }.runTaskAsynchronously(UltiTools.getInstance());
                            return true;
                        case "remove":
                            Player removedPlayer = Bukkit.getPlayerExact(args[1]);
                            if (removedPlayer == null) {
                                player.sendMessage(warning(UltiTools.languageUtils.getWords("player_not_online_or_not_exits")));
                                return true;
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GroupManagerUtils.removePlayerFromGroup(removedPlayer, args[2]);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("removed")));
                                }
                            }.runTaskAsynchronously(UltiTools.getInstance());
                            return true;
                        case "create":
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GroupManagerUtils.createGroup(args[1], args[2]);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("created")));
                                }
                            }.runTaskAsynchronously(UltiTools.getInstance());
                        default:
                            return false;
                    }
                case 4:
                    String permission = args[3];
                    switch (args[0]) {
                        case "give":
                            switch (args[1]) {
                                case "player":
                                    Player addedPlayer = Bukkit.getPlayerExact(args[2]);
                                    if (addedPlayer == null) {
                                        player.sendMessage(warning(UltiTools.languageUtils.getWords("player_not_online_or_not_exits")));
                                        return true;
                                    }
                                    GroupManagerUtils.addPlayerPermission(addedPlayer, permission);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("added")));
                                    return true;
                                case "group":
                                    GroupManagerUtils.addGroupPermission(args[2], permission);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("added")));
                                    return true;
                                default:
                                    return false;
                            }
                        case "take":
                            switch (args[1]) {
                                case "player":
                                    Player addedPlayer = Bukkit.getPlayerExact(args[2]);
                                    if (addedPlayer == null) {
                                        player.sendMessage(warning(UltiTools.languageUtils.getWords("player_not_online_or_not_exits")));
                                        return true;
                                    }
                                    GroupManagerUtils.takePlayerPermission(addedPlayer, permission);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("removed")));
                                    return true;
                                case "group":
                                    GroupManagerUtils.takeGroupPermission(args[2], permission);
                                    player.sendMessage(info(UltiTools.languageUtils.getWords("removed")));
                                    return true;
                                default:
                                    return false;
                            }
                    }
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.hasPermission("ultikits.tools.admin") || sender.isOp()) {
            if (args.length == 1) {
                return Arrays.asList("help", "give", "take", "add", "remove", "create", "delete");
            } else if (args.length == 2) {
                switch (args[0]) {
                    case "give":
                    case "take":
                        return Arrays.asList("player", "group");
                    case "add":
                    case "remove":
                        List<String> tabs = new ArrayList<>();
                        tabs.add("[" + UltiTools.languageUtils.getWords("player_name") + "]");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            tabs.add(player.getName());
                        }
                        return tabs;
                    case "create":
                        return Collections.singletonList("[" + UltiTools.languageUtils.getWords("permission_group_name") + "]");
                    case "delete":
                        List<String> tab = new ArrayList<>();
                        tab.add("[" + UltiTools.languageUtils.getWords("permission_group_name") + "]");
                        tab.addAll(GroupManagerUtils.getGroups());
                        return tab;
                    default:
                        return null;
                }
            } else if (args.length == 3) {
                switch (args[1]) {
                    case "player":
                        List<String> tabs = new ArrayList<>();
                        tabs.add("[" + UltiTools.languageUtils.getWords("player_name") + "]");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            tabs.add(player.getName());
                        }
                        return tabs;
                    case "group":
                        List<String> tab = new ArrayList<>();
                        tab.add("[" + UltiTools.languageUtils.getWords("permission_group_name") + "]");
                        tab.addAll(GroupManagerUtils.getGroups());
                        return tab;
                    default:
                        switch (args[0]) {
                            case "add":
                            case "remove":
                                List<String> tab1 = new ArrayList<>();
                                tab1.add("[" + UltiTools.languageUtils.getWords("permission_group_name") + "]");
                                tab1.addAll(GroupManagerUtils.getGroups());
                                return tab1;
                            case "create":
                                return Collections.singletonList("[" + UltiTools.languageUtils.getWords("inherited_permission_group") + "]");
                            default:
                                return null;
                        }
                }
            } else if (args.length == 4) {
                if (args[0].equals("give") || args[0].equals("take")) {
                    return Collections.singletonList("[" + UltiTools.languageUtils.getWords("permission") + "]");
                }
                return null;
            } else {
                return null;
            }
        }
        return null;
    }

    private static void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + UltiTools.languageUtils.getWords("permission_help_header"));
        sender.sendMessage(ChatColor.AQUA + "/pers help" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_help"));
        sender.sendMessage(ChatColor.AQUA + "/pers give player [" + UltiTools.languageUtils.getWords("player_name") + "] [" + UltiTools.languageUtils.getWords("permission") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_give_player"));
        sender.sendMessage(ChatColor.AQUA + "/pers give group [" + UltiTools.languageUtils.getWords("permission_group_name") + "] [" + UltiTools.languageUtils.getWords("permission") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_give_group"));
        sender.sendMessage(ChatColor.AQUA + "/pers take player [" + UltiTools.languageUtils.getWords("player_name") + "] [" + UltiTools.languageUtils.getWords("permission") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_take_player"));
        sender.sendMessage(ChatColor.AQUA + "/pers take group [" + UltiTools.languageUtils.getWords("permission_group_name") + "] [" + UltiTools.languageUtils.getWords("permission") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_take_group"));
        sender.sendMessage(ChatColor.AQUA + "/pers add [" + UltiTools.languageUtils.getWords("player_name") + "] [" + UltiTools.languageUtils.getWords("permission_group_name") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_add_player"));
        sender.sendMessage(ChatColor.AQUA + "/pers remove [" + UltiTools.languageUtils.getWords("player_name") + "] [" + UltiTools.languageUtils.getWords("permission_group_name") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_help_remove_player"));
        sender.sendMessage(ChatColor.AQUA + "/pers create [" + UltiTools.languageUtils.getWords("permission_group_name") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_create_group"));
        sender.sendMessage(ChatColor.AQUA + "/pers create [" + UltiTools.languageUtils.getWords("permission_group_name") + "] [" + UltiTools.languageUtils.getWords("inherited_permission_group") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_create_group_inherited"));
        sender.sendMessage(ChatColor.AQUA + "/pers delete [" + UltiTools.languageUtils.getWords("permission_group_name") + "]" + ChatColor.GRAY + "  " + UltiTools.languageUtils.getWords("permission_delete_group"));
    }
}
