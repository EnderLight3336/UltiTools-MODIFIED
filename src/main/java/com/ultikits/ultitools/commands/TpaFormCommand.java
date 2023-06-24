package com.ultikits.ultitools.commands;

import com.ultikits.abstracts.AbstractTabExecutor;
import me.enderlight3336.modified.form.TpaFormKit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TpaFormCommand extends AbstractTabExecutor {
    @Override
    protected boolean onPlayerCommand(@NotNull Command command, @NotNull String[] strings, @NotNull Player player) {
        if(FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
            TpaFormKit.TpaChooseForm(floodgatePlayer, player);
            return true;
        }else {
            return false;
        }
    }

    @Nullable
    @Override
    protected List<String> onPlayerTabComplete(@NotNull Command command, @NotNull String[] strings, @NotNull Player player) {
        return null;
    }
}
