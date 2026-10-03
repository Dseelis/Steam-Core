package com.steam.steamcore.client.compat;

import com.steam.steamcore.registry.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// Allows Spyglass Improvements to find a scope stored inside the worn helmet.
public final class SpyglassImprovementsBridge {
    private static final String MOD_ID = "spyglass_improvements";
    private static final String CLIENT_CLASS = "me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient";
    private static boolean registered;
    private static boolean installed;

    private SpyglassImprovementsBridge() {}

    public static void register() {
        if (!registered) {
            NeoForge.EVENT_BUS.addListener(SpyglassImprovementsBridge::onClientTick);
            registered = true;
        }
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        if (installed || !ModList.get().isLoaded(MOD_ID)) return;

        try {
            Class<?> clientClass = Class.forName(CLIENT_CLASS);
            Object client = clientClass.getMethod("getInstance").invoke(null);
            Field integrationField = clientClass.getDeclaredField("equipmentIntegration");
            integrationField.setAccessible(true);
            Object original = integrationField.get(client);

            Class<?> integrationType = Class.forName(
                    "me.juancarloscp52.spyglass_improvements.client.integrations.IEquipmentIntegration");
            if (original != null && Proxy.isProxyClass(original.getClass())
                    && Proxy.getInvocationHandler(original) instanceof HelmetScopeHandler) {
                installed = true;
                return;
            }

            Object bridge = Proxy.newProxyInstance(integrationType.getClassLoader(), new Class<?>[]{integrationType},
                    new HelmetScopeHandler(original));
            integrationField.set(client, bridge);
            installed = true;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            // Retry on the next client tick in case the other mod has not initialized yet.
        }
    }

    private static boolean hasHelmetScope(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        return helmet.getOrDefault(ModDataComponents.HAS_SPYGLASS.get(), false);
    }

    private static final class HelmetScopeHandler implements InvocationHandler {
        private final Object original;

        private HelmetScopeHandler(Object original) {
            this.original = original;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("isPlayerUsingSpyglass") && args != null && args.length == 1
                    && args[0] instanceof Player player && hasHelmetScope(player)) {
                return true;
            }
            if (original == null) {
                if (method.getName().equals("isPlayerUsingSpyglass")) return false;
                return null;
            }
            try {
                return method.invoke(original, args);
            } catch (InvocationTargetException exception) {
                throw exception.getCause();
            }
        }
    }
}
