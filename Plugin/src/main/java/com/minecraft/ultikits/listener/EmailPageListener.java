package com.minecraft.ultikits.listener;

import com.minecraft.ultikits.enums.ConfigsEnum;
import com.minecraft.ultikits.inventoryapi.InventoryManager;
import com.minecraft.ultikits.inventoryapi.PagesListener;
import com.minecraft.ultikits.ultitools.UltiTools;
import com.minecraft.ultikits.utils.SerializationUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class EmailPageListener extends PagesListener {

    @Override
    public void onItemClick(InventoryClickEvent event, Player player, InventoryManager inventoryManager, ItemStack clickedItem) {
        if (!event.getView().getTitle().contains(String.format(UltiTools.languageUtils.getWords("email_page_title"), player.getName()))) {
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        File file = new File(ConfigsEnum.PLAYER_EMAIL.toString(), player.getName() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (clicked != null) {
            event.setCancelled(true);
            if (Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().contains(UltiTools.languageUtils.getWords("email_item_description_from"))) {
                for (String lore : Objects.requireNonNull(clicked.getItemMeta().getLore())) {
                    if (lore.contains("ID:")) {
                        String uuid = lore.split(":")[1];
                        if (config.getBoolean(uuid + ".isRead")) {
                            return;
                        }
                        if (config.getString(uuid + ".item") == null) {
                            config.set(uuid + ".isRead", true);
                        } else {
                            String itemStackSerialized = config.getString(uuid + ".item");
                            if (player.getInventory().firstEmpty() != -1) {
                                ItemStack itemStack = SerializationUtils.encodeToItem(itemStackSerialized);
                                player.getInventory().addItem(itemStack);
                                config.set(uuid + ".isRead", true);
                                config.set(uuid + ".isClaimed", true);
                            } else {
                                player.sendMessage(ChatColor.RED + UltiTools.languageUtils.getWords("email_inventory_space_not_enough"));
                            }
                        }
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.closeInventory();
                        player.performCommand("email read");
                    }
                }
            }
        }
    }
}
