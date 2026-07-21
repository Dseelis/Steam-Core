package com.steam.steamcore.mixin;

import com.steam.steamcore.registry.ModDataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Mixin to integrate helmets with attached goggles into Create's goggles system.
// When a player wears a helmet with the HAS_GOGGLES component, Create will recognize it
// as if the player is wearing goggles, displaying the kinetic network overlay.
@Mixin(targets = "com.simibubi.create.content.equipment.goggles.GogglesItem", remap = false)
public class GogglesItemMixin {

//Inject into the isWearingGoggles method to check for helmets with attached goggles.
// This method is called by Create to determine if the player should see the overlay.
    @Inject(method = "isWearingGoggles", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onIsWearingGoggles(Player player, CallbackInfoReturnable<Boolean> cir) {
        // Check if the player is wearing a helmet with attached goggles
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (!helmet.isEmpty()) {
            Boolean hasGoggles = helmet.get(ModDataComponents.HAS_GOGGLES.get());
            if (hasGoggles != null && hasGoggles) {
                // Cancel the original method and return true
                cir.setReturnValue(true);
            }
        }
    }
}
