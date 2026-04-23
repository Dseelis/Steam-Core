package com.steam.steamcore;

import com.steam.steamcore.block.EternalInfuserBlock;
import com.steam.steamcore.block.EternalInfuserBlockEntity;
import com.steam.steamcore.block.EternalInfuserCapabilities;
import com.steam.steamcore.utils.TeleportUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import com.steam.steamcore.command.DebugCommand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
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
import net.minecraft.core.BlockPos;

@Mod(SteamCore.MODID)
public class SteamCore {

    public static final String MODID = "steamcore";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String PACK_NAME = "SteamCreate 2";
    public static final String PACK_VERSION = "2.1.0b";

    // REGISTRIES
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);


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

    public static final DeferredBlock<EternalInfuserBlock> ETERNAL_INFUSER =
            BLOCKS.register("eternal_infuser",
                    () -> new EternalInfuserBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.METAL)
                                    .strength(4f)
                    ));

    public static final DeferredBlock<Block> ETERNAL_ORE =
            BLOCKS.registerSimpleBlock("eternal_ore",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(4f));


    // BLOCK ENTITY TYPES

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EternalInfuserBlockEntity>>
            ETERNAL_INFUSER_BE_TYPE = BLOCK_ENTITY_TYPES.register(
            "eternal_infuser",
            () -> BlockEntityType.Builder
                    .of(EternalInfuserBlockEntity::new, ETERNAL_INFUSER.get())
                    .build(null)
    );


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
            ITEMS.register("eternal_gem",
                    () -> new com.steam.steamcore.item.EternalGemItem(
                            new Item.Properties().stacksTo(1)
                    ));

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
                                            5,
                                            -2.4F
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
                                output.accept(GAMMA_BRICK_ITEM.get());
                                output.accept(GAMMA_ORE_ITEM.get());
                                output.accept(SEALED_BLOCK_ITEM.get());
                                output.accept(ETERNAL_INFUSER_ITEM.get());
                                output.accept(ETERNAL_ORE_ITEM.get());
                                output.accept(UNFINISHED_CALCULATION_PRESS.get());
                                output.accept(UNFINISHED_ENGINEERING_PRESS.get());
                                output.accept(UNFINISHED_LOGIC_PRESS.get());
                                output.accept(UNFINISHED_SILICON_PRESS.get());
                                output.accept(WRENCH.get());
                                output.accept(EMPTY_ETERNAL_GEM.get());
                                output.accept(ETERNAL_CORE.get());
                                output.accept(ETERNAL_GEM.get());
                                output.accept(GAMMA_IGNITE.get());
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
        BLOCK_ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(EternalInfuserCapabilities::register);
        modEventBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DebugCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // getServer() is safe here — PlayerLoggedInEvent fires server-side only
        ServerLevel overworld = player.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld == null) return;

        var tag = player.getPersistentData();

        if (!tag.getBoolean("steamcore_spawned")) {
            // Find a safe surface position near world spawn.
            // We search from just below the build height down to ground level.
            BlockPos spawn = overworld.getSharedSpawnPos();
            int searchStartY = Math.min(overworld.getMaxBuildHeight() - 2, 200);

            BlockPos safePos = TeleportUtils.findSafePosition(
                    overworld, spawn.getX(), spawn.getZ(), searchStartY);

            // Fallback: use the world spawn directly if scan found nothing
            // (e.g. superflat worlds). findSafePosition already clamped to bounds.
            if (safePos == null) {
                safePos = overworld.getSharedSpawnPos().above();
            }

            player.teleportTo(
                    overworld,
                    safePos.getX() + 0.5,
                    safePos.getY(),
                    safePos.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

            tag.putBoolean("steamcore_spawned", true);
        }

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

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        com.steam.steamcore.item.GammaIgniteItem.cancelPendingTeleport(player.getUUID());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(SEALED_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
