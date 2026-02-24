package com.steam.steamcore.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import java.time.format.DateTimeFormatter;
import net.minecraft.SharedConstants;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.*;

import com.steam.steamcore.Config;

public class PackInfoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("packinfo")
                        .requires(source -> source.hasPermission(2))
                        .executes(PackInfoCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {

        if (!Config.ENABLE_PACKINFO_COMMAND.get()) {
            context.getSource().sendFailure(
                    Component.literal("This command is disabled in config."));
            return 0;
        }

        Path dir = FMLPaths.CONFIGDIR.get().resolve("steamcore");

        try {
            Files.createDirectories(dir);
        } catch (Exception ignored) {}

        Path file = dir.resolve("packinfo.json");

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("packName", "SteamCreate 2");
        data.put("packVersion", "2.0.7b");
        data.put("minecraft", SharedConstants.getCurrentVersion().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        data.put("generatedAt", LocalDateTime.now().format(formatter));
        data.put("modsCount", ModList.get().getMods().size());

        List<Map<String, String>> mods = new ArrayList<>();

        ModList.get().getMods().forEach(mod -> {
            Map<String, String> modData = new LinkedHashMap<>();
            modData.put("modId", mod.getModId());
            modData.put("name", mod.getDisplayName());
            modData.put("version", mod.getVersion().toString());
            mods.add(modData);
        });

        data.put("mods", mods);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Pack info generated in config/steamcore/packinfo.json"),
                true
        );

        return 1;
    }
}