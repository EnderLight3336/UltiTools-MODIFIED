package me.enderlight3336.modified.form;

import com.ultikits.ultitools.tasks.TpTimerTask;
import com.ultikits.ultitools.ultitools.UltiTools;
import com.ultikits.utils.MessagesUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.ultikits.utils.MessagesUtils.warning;
import static org.bukkit.Bukkit.getOnlinePlayers;


public class TpaFormKit {
    public static void TpaRequestForm(@NotNull FloodgatePlayer floodgateTarget, @NotNull Player target, @NotNull Player sender) {
        if (UltiTools.isTpaformEnabled) {
            int i = target.getNoDamageTicks();
            target.setNoDamageTicks(2000000);
            floodgateTarget.sendForm(
                    SimpleForm.builder()
                            .title("传送请求面板")
                            .content(sender.getDisplayName() + "请求传送至您的位置")
                            .button("同意")
                            .button("拒绝")
                            .validResultHandler(response -> resultHandler1(response, sender, target, true)).build()
            );
            target.setNoDamageTicks(i + 3);
        }
    }

    public static void TpahereRequestForm(@NotNull FloodgatePlayer floodgateTarget, @NotNull Player target, @NotNull Player sender) {
        if (UltiTools.isTpaformEnabled) {
            int i = target.getNoDamageTicks();
            target.setNoDamageTicks(2000000);
            floodgateTarget.sendForm(
                    SimpleForm.builder()
                            .title("传送请求面板")
                            .content(sender.getDisplayName() + "请求您传送至他的位置")
                            .button("同意")
                            .button("拒绝")
                            .validResultHandler(response -> resultHandler1(response, sender, target, false)).build()
            );
            target.setNoDamageTicks(i + 3);
        }
    }

    public static void TpaChooseForm(@NotNull FloodgatePlayer floodgateSender, @NotNull Player sender) {
        int i1 = sender.getNoDamageTicks();
        sender.setNoDamageTicks(2000000);
        Player[] onlinePlayers = getOnlinePlayers().toArray(new Player[getOnlinePlayers().size()]);
        ArrayList<String> list = new ArrayList<>();
        list.add("空");
        for (int i = 0; i < getOnlinePlayers().size() - 1; i = i + 1) {
            list.add(onlinePlayers[i + 1].getDisplayName());
        }
        floodgateSender.sendForm(
                CustomForm.builder()
                        .title("传送选择面板")
                        .dropdown("传送模式", "传送到他人", "传送他人到此处")
                        .dropdown("传送目标", list)
                        .input("请输入在线玩家名称")
                        .validResultHandler(response -> resultHandler2(response, sender, onlinePlayers)).build()

        );
        sender.setNoDamageTicks(i1 + 3);
    }

    public static void resultHandler1(@NotNull SimpleFormResponse response, Player sender, Player target, boolean mode) {
        if (mode) {// tpa
            if (response.clickedButtonId() == 0) {// accept
                if (target == null) {
                    sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_no_request")));
                }
                TpTimerTask.tpTemp.put(sender, null);
                TpTimerTask.tpTimer.put(sender, 0);
                target.teleport(sender.getLocation());
                target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpa_teleport_success")));
            } else if (response.clickedButtonId() == 1) {// reject
                if (target == null) {
                    sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_no_request")));
                } else {
                    sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_teleport_rejected")));
                    target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpa_rejected")));
                    TpTimerTask.tpTemp.put(sender, null);
                    TpTimerTask.tpTimer.put(sender, 0);
                }
            }
        } else if (!mode) {//  tpahere
            if (response.clickedButtonId() == 0) {// accept
                if (target == null) {
                    sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_no_request")));
                } else {
                    TpTimerTask.tphereTemp.put(sender, null);
                    TpTimerTask.tphereTimer.put(sender, 0);
                    sender.teleport(target.getLocation());
                    target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpa_teleport_success")));
                }
            } else if (response.clickedButtonId() == 1) {// reject
                sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_teleport_rejected")));
                target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpa_rejected")));
                TpTimerTask.tphereTemp.put(sender, null);
                TpTimerTask.tphereTimer.put(sender, 0);
            }
        }
    }

    public static void resultHandler2(@NotNull CustomFormResponse response, Player sender, Player[] onlinePlayers) {// tpa/tpahere xxx
        int mode = response.asDropdown(0);
        int targetId = response.asDropdown(1);
        String input = response.asInput(2);
        Player target = null;
        if (mode == 0) {// tpa
            if (targetId == 0) {// button = 空
                if (input != null) {
                    if (!input.isEmpty()) {
                        target = Bukkit.getPlayerExact(input);
                    } else {
                        invalidResultHandler(sender);
                    }
                } else {
                    invalidResultHandler(sender);
                }
            } else if (targetId >= 0) {// button = player
                if (input == null) {
                    target = onlinePlayers[targetId - 1];
                } else if (input.isEmpty()) {
                    target = onlinePlayers[targetId - 1];
                } else {
                    invalidResultHandler(sender);
                }
            }
            if (target == null) {
                sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_player_not_found")));
            }
            if (TpTimerTask.tpTemp.get(target) != null) {
                sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_target_busy")));
            }
            if (target != null) {
                TpTimerTask.tpTemp.put(target, sender);
                TpTimerTask.tpTimer.put(target, 20);
                sender.sendMessage(MessagesUtils.info(String.format(UltiTools.languageUtils.getString("tpa_tp_send_successfully"), target.getName())));
                target.sendMessage(MessagesUtils.info(String.format(UltiTools.languageUtils.getString("tpa_tp_enquire"), sender.getName())));
                target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpa_accept_tip")));
                target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpa_reject_tip")));
                if (FloodgateApi.getInstance().isFloodgatePlayer(target.getUniqueId())) {
                    TpaFormKit.TpaRequestForm(FloodgateApi.getInstance().getPlayer(target.getUniqueId()), target, sender);
                }
            }
        } else if (mode == 1) {// tpahere
            if (targetId == 0) {// button = 空
                if (input != null) {
                    if (!input.isEmpty()) {
                        target = Bukkit.getPlayerExact(input);
                    } else {
                        invalidResultHandler(sender);
                    }
                } else {
                    invalidResultHandler(sender);
                }
            } else if (targetId >= 0) {
                if (input == null) {
                    target = onlinePlayers[targetId - 1];
                } else if (input.isEmpty()) {
                    target = onlinePlayers[targetId - 1];
                } else {
                    invalidResultHandler(sender);
                }
            }
            if (target == null) {
                sender.sendMessage(warning(UltiTools.languageUtils.getString("tpa_player_not_found")));
            } else {
                TpTimerTask.tphereTemp.put(target, sender);
                TpTimerTask.tphereTimer.put(target, 20);
                sender.sendMessage(MessagesUtils.info(String.format(UltiTools.languageUtils.getString("tpa_tp_send_successfully"), target.getName())));
                target.sendMessage(MessagesUtils.info(String.format(UltiTools.languageUtils.getString("tpahere_enquire"), sender.getName())));
                target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpahere_accept_tip")));
                target.sendMessage(MessagesUtils.info(UltiTools.languageUtils.getString("tpahere_reject_tip")));
            }
        }
    }

    public static void invalidResultHandler(@NotNull Player player) {
        player.sendMessage(ChatColor.RED + "请不要在传送目标选择玩家的同时，在输入栏输入玩家名称");
    }
}
