package com.steam.steamcore.registry;

import com.steam.steamcore.SteamCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, SteamCore.MODID);

    public static final Supplier<MenuType<com.steam.steamcore.inventory.DisassemblyTableMenu>> DISASSEMBLY_TABLE_MENU =
            registerMenuType("disassembly_table", com.steam.steamcore.inventory.DisassemblyTableMenu::new);

    public static final Supplier<MenuType<com.steam.steamcore.inventory.EternalInfuserMenu>> ETERNAL_INFUSER_MENU =
            registerMenuType("eternal_infuser", com.steam.steamcore.inventory.EternalInfuserMenu::new);

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
