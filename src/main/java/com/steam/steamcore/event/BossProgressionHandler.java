package com.steam.steamcore.event;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.story.StoryWorldData;
import com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public final class BossProgressionHandler {
    private static final TagKey<EntityType<?>> BOSS_ENTITIES = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "bosses"));

    private BossProgressionHandler() {}

    @SubscribeEvent
    public static void onBossDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(event.getSource().getEntity() instanceof Player)) return;

        EntityType<?> type = entity.getType();
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        int points;

        if ("cataclysm".equals(entityId.getNamespace()) && type.is(BOSS_ENTITIES)) {
            // Cataclysm boss defeats count at half strength versus a major vanilla boss/shard pair.
            points = 1;
        } else if (type == EntityType.ENDER_DRAGON || type == EntityType.WITHER) {
            points = 2;
        } else {
            return;
        }

        StoryWorldData story = StoryWorldData.get(entity.level().getServer());
        if (!story.isMobProgressionEnabled()) return;

        if (SteamCoreKubeJSEvents.isKubeJSLoaded()) {
            var hook = SteamCoreKubeJSEvents.fireBossProgressEvent(entity.level(), entity, entityId.toString(), points);
            if (hook.isCancelled()) return;
            points = hook.getPoints();
            if (points <= 0) return;
        }

        int previousLevelBonus = story.getBossProgressPoints() / 2;
        if (!story.recordBossDefeat(entityId.toString(), points)) return;
        int newLevelBonus = story.getBossProgressPoints() / 2;

        if (newLevelBonus > previousLevelBonus) {
            entity.level().getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("Поражение босса изменило мир: новые враждебные мобы станут сильнее.")
                            .withStyle(ChatFormatting.DARK_RED), false);
        }
    }
}
