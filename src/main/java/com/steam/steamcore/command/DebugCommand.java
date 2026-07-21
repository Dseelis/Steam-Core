package com.steam.steamcore.command;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.steam.steamcore.Config;
import com.steam.steamcore.SteamCore;
import com.steam.steamcore.block.EternalInfuserBlockEntity;
import com.steam.steamcore.utils.PlayerDataKeys;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//   /steamdebug info <player>
//   /steamdebug abysstp <player>
//   /steamdebug setenergy <amount>
//   /steamdebug packinfo
//   /steamdebug modlist

public class DebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("steamdebug")
                        .requires(src -> src.hasPermission(2)
                                && Config.ENABLE_STEAMDEBUG_COMMAND.get())

                        .then(Commands.literal("info")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(DebugCommand::printInfo)))

                        .then(Commands.literal("ping")
                                .executes(DebugCommand::showPing))

                        .then(Commands.literal("abysstp")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(DebugCommand::forceAbyssTp)))

                        .then(Commands.literal("setenergy")
                                .then(Commands.argument("amount",
                                                IntegerArgumentType.integer(0, EternalInfuserBlockEntity.MAX_ENERGY))
                                        .executes(DebugCommand::setEnergy)))

                        .then(Commands.literal("packinfo")
                                .executes(DebugCommand::generatePackInfo))

                        .then(Commands.literal("modlist")
                                .executes(DebugCommand::generateModList))
        );
    }

    private static int printInfo(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
            var tag = target.getPersistentData();

            ctx.getSource().sendSuccess(
                    () -> Component.literal("=== SteamCore: " + target.getName().getString() + " ===")
                            .withStyle(ChatFormatting.GOLD), false);

            sendField(ctx, PlayerDataKeys.SPAWNED,         String.valueOf(tag.getBoolean(PlayerDataKeys.SPAWNED)));
            sendField(ctx, PlayerDataKeys.INTRO_SHOWN,     String.valueOf(tag.getBoolean(PlayerDataKeys.INTRO_SHOWN)));
            sendField(ctx, PlayerDataKeys.ABYSS_COOLDOWN,  String.valueOf(tag.getInt(PlayerDataKeys.ABYSS_COOLDOWN)));
            sendField(ctx, PlayerDataKeys.JOIN_PROTECTION, String.valueOf(tag.getInt(PlayerDataKeys.JOIN_PROTECTION)));

            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Player not found."));
            return 0;
        }
    }

    private static int showPing(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            int ping = player.connection.latency();

            ctx.getSource().sendSuccess(() -> Component.literal("[SteamDebug] Your ping: ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(ping + "ms")
                            .withStyle(ping < 100 ? ChatFormatting.GREEN : (ping < 200 ? ChatFormatting.YELLOW : ChatFormatting.RED))), false);

            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Must be run by a player."));
            return 0;
        }
    }

    private static int forceAbyssTp(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
            TeleportUtils.teleportToRespawn(target);

            ctx.getSource().sendSuccess(
                    () -> Component.literal("[SteamDebug] Teleported "
                                    + target.getName().getString() + " to respawn.")
                            .withStyle(ChatFormatting.YELLOW), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Player not found."));
            return 0;
        }
    }

    private static int setEnergy(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("Must be run by a player."));
            return 0;
        }

        var level = player.serverLevel();
        BlockPos playerPos = player.blockPosition();

        EternalInfuserBlockEntity found = null;
        BlockPos foundPos = null;
        int radius = 5;

        outer:
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos check = playerPos.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(check);
                    if (be instanceof EternalInfuserBlockEntity infuser) {
                        found = infuser;
                        foundPos = check;
                        break outer;
                    }
                }
            }
        }

        if (found == null) {
            source.sendFailure(Component.literal(
                    "No Eternal Infuser found within " + radius + " blocks."));
            return 0;
        }

        found.setEnergy(amount);

        BlockPos finalFoundPos = foundPos;
        int finalAmount = amount;
        source.sendSuccess(
                () -> Component.literal("[SteamDebug] Set energy to ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(finalAmount + " FE")
                                .withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal(" in Infuser at " +
                                        finalFoundPos.getX() + " " +
                                        finalFoundPos.getY() + " " +
                                        finalFoundPos.getZ())
                                .withStyle(ChatFormatting.GRAY)),
                true
        );
        return 1;
    }

    private static int generatePackInfo(CommandContext<CommandSourceStack> ctx) {
        Path dir = steamcoreConfigDir();
        ensureDir(dir);
        Path file = dir.resolve("packinfo.json");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("packName",    SteamCore.getPackName());
        data.put("packVersion", SteamCore.getPackVersion());
        data.put("minecraft",   SharedConstants.getCurrentVersion().getName());
        data.put("generatedAt", timestamp());
        data.put("modsCount",   ModList.get().getMods().size());

        List<Map<String, String>> mods = new ArrayList<>();
        ModList.get().getMods().forEach(mod -> {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("modId",   mod.getModId());
            m.put("name",    mod.getDisplayName());
            m.put("version", mod.getVersion().toString());
            mods.add(m);
        });
        data.put("mods", mods);

        try (Writer writer = Files.newBufferedWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.getSource().sendFailure(Component.literal("Failed to write packinfo.json"));
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.literal("[SteamDebug] Pack info → config/steamcore/packinfo.json")
                        .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int generateModList(CommandContext<CommandSourceStack> ctx) {
        Path dir = steamcoreConfigDir();
        ensureDir(dir);
        Path file = dir.resolve("modlist.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("Pack: " + SteamCore.getPackName() + " " + SteamCore.getPackVersion());
            writer.newLine();
            writer.write("Generated: " + timestamp());
            writer.newLine();
            writer.write("==================================");
            writer.newLine();

            ModList.get().getMods().forEach(mod -> {
                try {
                    writer.write("Name: "    + mod.getDisplayName()); writer.newLine();
                    writer.write("Version: " + mod.getVersion());     writer.newLine();
                    writer.write("----------------------------------"); writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            ctx.getSource().sendFailure(Component.literal("Failed to write modlist.txt"));
            return 0;
        }

        ctx.getSource().sendSuccess(
                () -> Component.literal("[SteamDebug] Mod list → config/steamcore/modlist.txt")
                        .withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static void sendField(CommandContext<CommandSourceStack> ctx,
                                  String key, String value) {
        ctx.getSource().sendSuccess(
                () -> Component.literal("  " + key + ": ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(value).withStyle(ChatFormatting.WHITE)),
                false
        );
    }

    private static Path steamcoreConfigDir() {
        return FMLPaths.CONFIGDIR.get().resolve("steamcore");
    }

    private static void ensureDir(Path dir) {
        try { Files.createDirectories(dir); } catch (Exception ignored) {}
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}