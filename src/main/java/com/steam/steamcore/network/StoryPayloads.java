package com.steam.steamcore.network;

import com.steam.steamcore.SteamCore;
import com.steam.steamcore.story.dialogue.DialogueService;
import com.steam.steamcore.story.StoryService;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = SteamCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class StoryPayloads {
    private StoryPayloads() {}

    public record OpenDialogue(String dialogueId, String definitionJson) implements CustomPacketPayload {
        public static final Type<OpenDialogue> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "open_dialogue"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenDialogue> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, OpenDialogue::dialogueId,
                ByteBufCodecs.STRING_UTF8, OpenDialogue::definitionJson,
                OpenDialogue::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record StartAwakening(String dialogueId, String definitionJson) implements CustomPacketPayload {
        public static final Type<StartAwakening> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "start_awakening"));
        public static final StreamCodec<RegistryFriendlyByteBuf, StartAwakening> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, StartAwakening::dialogueId,
                ByteBufCodecs.STRING_UTF8, StartAwakening::definitionJson,
                StartAwakening::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ChooseDialogueOption(String dialogueId, String nodeId, String actionId) implements CustomPacketPayload {
        public static final Type<ChooseDialogueOption> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "choose_dialogue_option"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChooseDialogueOption> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ChooseDialogueOption::dialogueId,
                ByteBufCodecs.STRING_UTF8, ChooseDialogueOption::nodeId,
                ByteBufCodecs.STRING_UTF8, ChooseDialogueOption::actionId,
                ChooseDialogueOption::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record FinishAwakening(String dialogueId) implements CustomPacketPayload {
        public static final Type<FinishAwakening> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(SteamCore.MODID, "finish_awakening"));
        public static final StreamCodec<RegistryFriendlyByteBuf, FinishAwakening> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, FinishAwakening::dialogueId,
                FinishAwakening::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(ChooseDialogueOption.TYPE, ChooseDialogueOption.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        DialogueService.choose(player, payload.dialogueId(), payload.nodeId(), payload.actionId());
                    }
                });
        registrar.playToServer(FinishAwakening.TYPE, FinishAwakening.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        DialogueService.finishAwakening(player, payload.dialogueId());
                    }
                });
        registrar.playToClient(OpenDialogue.TYPE, OpenDialogue.STREAM_CODEC,
                (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        com.steam.steamcore.client.network.ClientStoryPayloads.openDialogue(
                                payload.dialogueId(), payload.definitionJson());
                    }
                });
        registrar.playToClient(StartAwakening.TYPE, StartAwakening.STREAM_CODEC,
                (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        com.steam.steamcore.client.network.ClientStoryPayloads.startAwakening(
                                payload.dialogueId(), payload.definitionJson());
                    }
                });
    }
}
