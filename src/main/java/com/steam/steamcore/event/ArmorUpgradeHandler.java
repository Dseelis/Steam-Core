package com.steam.steamcore.event;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.registry.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public final class ArmorUpgradeHandler {
    private ArmorUpgradeHandler() {}

    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        var stack = event.getItemStack();
        if (!(stack.getItem() instanceof ArmorItem armor) || armor.getEquipmentSlot() != EquipmentSlot.CHEST) return;

        if (stack.getOrDefault(ModDataComponents.PRESSED_IRON_ARMOR.get(), false)) {
            event.addModifier(Attributes.ARMOR,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "pressed_iron_armor"),
                            1.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
        }
        if (stack.getOrDefault(ModDataComponents.STEEL_TOUGHNESS.get(), false)) {
            event.addModifier(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "steel_toughness"),
                            0.5, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
        }
    }
}
