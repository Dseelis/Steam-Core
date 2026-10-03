package com.steam.steamcore.event;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.story.StoryWorldData;
import com.steam.steamcore.integration.kubejs.SteamCoreKubeJSEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = SteamCore.MODID)
public final class MobLevelHandler {
    public static final String LEVEL_KEY = "steamcore_mob_level";
    private static final String APPLIED_KEY = "steamcore_mob_level_applied";
    private static final int MAX_LEVEL = 10;
    private static final double HEALTH_PER_LEVEL = 0.10;
    private static final double DAMAGE_PER_LEVEL = 0.07;
    private static final double SPEED_PER_LEVEL = 0.015;

    private static final TagKey<EntityType<?>> LEVEL_EXEMPT = TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "level_exempt")
    );
    private static final TagKey<EntityType<?>> BOSS_ENTITIES = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "bosses"));

    private static final ResourceLocation HEALTH_MODIFIER = id("mob_level_health");
    private static final ResourceLocation DAMAGE_MODIFIER = id("mob_level_damage");
    private static final ResourceLocation SPEED_MODIFIER = id("mob_level_speed");

    private MobLevelHandler() {}

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, path);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || event.loadedFromDisk()) return;
        if (!(event.getEntity() instanceof Mob mob) || !(mob instanceof Enemy)) return;
        if (mob.getType().is(LEVEL_EXEMPT) || mob.getType().is(BOSS_ENTITIES)) return;

        var persistentData = mob.getPersistentData();
        if (persistentData.getBoolean(APPLIED_KEY)) return;

        StoryWorldData story = StoryWorldData.get(event.getLevel().getServer());
        if (!story.isMobProgressionEnabled()) return;

        int level = getLevel(event.getLevel().dimension(), story.getBossProgressPoints());
        if (SteamCoreKubeJSEvents.isKubeJSLoaded()) {
            var hook = SteamCoreKubeJSEvents.fireMobLevelEvent(event.getLevel(), mob, level);
            if (hook.isCancelled()) return;
            level = hook.getLevelValue();
        }
        applyLevel(mob, level);
    }

    public static int getLevel(net.minecraft.resources.ResourceKey<Level> dimension, int bossFragments) {
        int dimensionLevel;
        if (dimension.equals(Level.NETHER)) {
            dimensionLevel = 2;
        } else if (dimension.equals(Level.END)) {
            dimensionLevel = 3;
        } else {
            dimensionLevel = 1;
        }

        int storyBonus = Math.max(0, bossFragments) / 2;
        return Math.min(MAX_LEVEL, dimensionLevel + storyBonus);
    }

    private static void applyLevel(Mob mob, int level) {
        double healthRatio = 1.0;
        AttributeInstance maxHealth = mob.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getValue() > 0.0) {
            healthRatio = mob.getHealth() / (float) maxHealth.getValue();
        }

        double levelSteps = level - 1.0;
        if (levelSteps > 0.0) {
            addModifier(mob, Attributes.MAX_HEALTH, HEALTH_MODIFIER, HEALTH_PER_LEVEL * levelSteps);
            addModifier(mob, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER, DAMAGE_PER_LEVEL * levelSteps);
            addModifier(mob, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER, SPEED_PER_LEVEL * levelSteps);
        }

        if (maxHealth != null) {
            mob.setHealth((float) Math.min(maxHealth.getValue(), maxHealth.getValue() * healthRatio));
        }

        Component originalName = mob.getCustomName();
        Component name = Component.literal("Ур. " + level + " ")
                .withStyle(ChatFormatting.GOLD)
                .append(originalName == null ? mob.getType().getDescription() : originalName);
        mob.setCustomName(name);
        mob.getPersistentData().putInt(LEVEL_KEY, level);
        mob.getPersistentData().putBoolean(APPLIED_KEY, true);
    }

    private static void addModifier(LivingEntity entity, net.minecraft.core.Holder<Attribute> attribute,
                                    ResourceLocation id, double amount) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(
                id, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }
}
