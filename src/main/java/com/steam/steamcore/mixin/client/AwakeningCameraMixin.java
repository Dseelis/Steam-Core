package com.steam.steamcore.mixin.client;

import com.steam.steamcore.client.story.AwakeningCutsceneController;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class AwakeningCameraMixin {
    @Invoker("setPosition")
    protected abstract void steamcore$setCameraPosition(double x, double y, double z);

    @Invoker("setRotation")
    protected abstract void steamcore$setCameraRotation(float yaw, float pitch);

    @Inject(method = "setup", at = @At("TAIL"))
    private void steamcore$applyAwakeningCamera(BlockGetter level, Entity entity, boolean detached,
                                                 boolean mirrored, float partialTick, CallbackInfo callback) {
        if (entity != net.minecraft.client.Minecraft.getInstance().player
                || !AwakeningCutsceneController.isActive()) return;
        AwakeningCutsceneController.CameraPose pose = AwakeningCutsceneController.cameraPose(partialTick);
        if (pose == null) return;
        steamcore$setCameraPosition(pose.x(), pose.y(), pose.z());
        steamcore$setCameraRotation(pose.yaw(), pose.pitch());
    }
}
