package com.steam.steamcore;

import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import com.steam.steamcore.command.PackInfoCommand;
import com.steam.steamcore.command.GenerateModListCommand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SteamCore.MODID)
public class SteamCore {

    public static final String MODID = "steamcore";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String PACK_NAME = "SteamCreate 2";
    public static final String PACK_VERSION = "2.0.7b";

    // REGISTRIES
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    // BLOCKS

    public static final DeferredBlock<Block> GAMMA_BRICK =
            BLOCKS.registerSimpleBlock("gamma_brick",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3f));

    public static final DeferredBlock<Block> GAMMA_ORE =
            BLOCKS.registerSimpleBlock("gamma_ore",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3f));

    public static final DeferredBlock<Block> SEALED_BLOCK =
            BLOCKS.registerSimpleBlock("sealed_block",
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4f));
    public static final DeferredBlock<Block> ETERNAL_INFUSER =
            BLOCKS.registerSimpleBlock("eternal_infuser",
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4f));

    public static final DeferredBlock<Block> ETERNAL_ORE =
            BLOCKS.registerSimpleBlock("eternal_ore",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4f));


    // BLOCK ITEMS

    public static final DeferredItem<BlockItem> GAMMA_BRICK_ITEM =
            ITEMS.registerSimpleBlockItem("gamma_brick", GAMMA_BRICK);

    public static final DeferredItem<BlockItem> GAMMA_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("gamma_ore", GAMMA_ORE);

    public static final DeferredItem<BlockItem> SEALED_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("sealed_block", SEALED_BLOCK);
    public static final DeferredItem<BlockItem> ETERNAL_INFUSER_ITEM =
            ITEMS.registerSimpleBlockItem("eternal_infuser", ETERNAL_INFUSER);
    public static final DeferredItem<BlockItem> ETERNAL_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("eternal_ore", ETERNAL_ORE);


    // ITEMS

    public static final DeferredItem<Item> UNFINISHED_CALCULATION_PRESS =
            ITEMS.registerSimpleItem("unfinished_calculation_press");

    public static final DeferredItem<Item> UNFINISHED_ENGINEERING_PRESS =
            ITEMS.registerSimpleItem("unfinished_engineering_press");

    public static final DeferredItem<Item> UNFINISHED_LOGIC_PRESS =
            ITEMS.registerSimpleItem("unfinished_logic_press");

    public static final DeferredItem<Item> UNFINISHED_SILICON_PRESS =
            ITEMS.registerSimpleItem("unfinished_silicon_press");

    public static final DeferredItem<Item> WRENCH =
            ITEMS.register("wrench",
                    () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> EMPTY_ETERNAL_GEM =
            ITEMS.registerSimpleItem("empty_eternal_gem");

    public static final DeferredItem<Item> ETERNAL_CORE =
            ITEMS.registerSimpleItem("eternal_core");

    public static final DeferredItem<Item> GAMMA_IGNITE =
            ITEMS.register("gamma_ignite",
                    () -> new com.steam.steamcore.item.GammaIgniteItem(
                            new Item.Properties().stacksTo(1)
                    ));

    public static final DeferredItem<Item> ETERNAL_GEM =
            ITEMS.registerSimpleItem("eternal_gem");

    public static final DeferredItem<Item> SWORD_PART =
            ITEMS.registerSimpleItem("sword_part");

    public static final DeferredItem<Item> SWORD_PART_2 =
            ITEMS.registerSimpleItem("sword_part_2");

    public static final DeferredItem<Item> FINAL_SWORD =
            ITEMS.register("final_sword",
                    () -> new SwordItem(
                            Tiers.DIAMOND,
                            new Item.Properties()
                                    .attributes(SwordItem.createAttributes(
                                            Tiers.DIAMOND,
                                            5,      // damage
                                            -2.4F   // attack speed
                                    ))
                                    .stacksTo(1)
                    ));


    // CREATIVE TAB

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STEAM_TAB =
            CREATIVE_MODE_TABS.register("steam_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.steamcore"))
                            .icon(() -> new ItemStack(ETERNAL_CORE.get()))
                            .displayItems((parameters, output) -> {

                                // Blocks
                                output.accept(GAMMA_BRICK_ITEM.get());
                                output.accept(GAMMA_ORE_ITEM.get());
                                output.accept(SEALED_BLOCK_ITEM.get());
                                output.accept(ETERNAL_INFUSER_ITEM.get());
                                output.accept(ETERNAL_ORE_ITEM.get());

                                // Items
                                output.accept(UNFINISHED_CALCULATION_PRESS.get());
                                output.accept(UNFINISHED_ENGINEERING_PRESS.get());
                                output.accept(UNFINISHED_LOGIC_PRESS.get());
                                output.accept(UNFINISHED_SILICON_PRESS.get());
                                output.accept(WRENCH.get());
                                output.accept(EMPTY_ETERNAL_GEM.get());
                                output.accept(ETERNAL_CORE.get());
                                output.accept(ETERNAL_GEM.get());
                                output.accept(GAMMA_IGNITE.get());

                                // Sword parts
                                output.accept(SWORD_PART.get());
                                output.accept(SWORD_PART_2.get());
                                output.accept(FINAL_SWORD.get());
                            })
                            .build()
            );


    // CONSTRUCTOR

    public SteamCore(IEventBus modEventBus, ModContainer modContainer) {

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GenerateModListCommand.register(event.getDispatcher());
        PackInfoCommand.register(event.getDispatcher());
    }

    private void clearArea(Level level, BlockPos center) {

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 2; y++) {

                    BlockPos pos = center.offset(x, y, z);

                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        var tag = player.getPersistentData();

        // FIRST SPAWN
        if (!tag.getBoolean("steamcore_spawned")) {

            int randomY = 80 + level.random.nextInt(21);

            BlockPos spawnPos = new BlockPos(
                    player.getBlockX(),
                    randomY,
                    player.getBlockZ()
            );

            clearArea(level, spawnPos);

            player.teleportTo(
                    (ServerLevel) level,
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

            tag.putBoolean("steamcore_spawned", true);
        }

        // INTRO MESSAGE

        if (Config.SHOW_INTRO_MESSAGES.get()
                && !tag.getBoolean("steamcore_intro_shown")) {

            player.sendSystemMessage(
                    Component.literal("Welcome to SteamCore Beta!")
                            .withStyle(ChatFormatting.GOLD)
            );

            player.sendSystemMessage(
                    Component.literal("You wake up in an unfamiliar world... How did you get here?")
                            .withStyle(ChatFormatting.GRAY)
            );

            tag.putBoolean("steamcore_intro_shown", true);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(SEALED_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

}