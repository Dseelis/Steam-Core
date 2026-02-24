package com.steam.steamcore.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedWriter;
import java.io.IOException;
import com.steam.steamcore.Config;

public class GenerateModListCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("generatemodlist")
                        .requires(source -> source.hasPermission(2)) // OP only
                        .executes(GenerateModListCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {

        if (!Config.ENABLE_MODLIST_COMMAND.get()) {
            context.getSource().sendFailure(
                    Component.literal("This command is disabled in config."));
            return 0;
        }

        Path configDir = FMLPaths.CONFIGDIR.get().resolve("steamcore");

        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path file = configDir.resolve("modlist.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            ModList.get().getMods().forEach(mod -> {
                try {
                    writer.write("Name: " + mod.getDisplayName());
                    writer.newLine();
                    writer.write("Version: " + mod.getVersion());
                    writer.newLine();
                    writer.write("----------------------------------");
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Mod list generated in config/steamcore/modlist.txt"),
                true
        );

        return 1;
    }
}