package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Final
    private MusicTracker musicTracker;

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void stoppausemymusic$preserveCurrentMusicBeforeJoinWorld(CallbackInfo ci) {
        PersistentMusicState.keepCurrentMusic(this.musicTracker);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("HEAD"))
    private void stoppausemymusic$preserveCurrentMusicBeforeDisconnect(net.minecraft.client.gui.screen.Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
        PersistentMusicState.keepCurrentMusic(this.musicTracker);
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void stoppausemymusic$preserveCurrentMusicBeforeDisconnectCleanup(CallbackInfo ci) {
        PersistentMusicState.keepCurrentMusic(this.musicTracker);
    }

    @Redirect(method = "reset", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;stopAll()V"))
    private void stoppausemymusic$keepCurrentMusicPlayingDuringReset(SoundManager soundManager) {
        PersistentMusicState.stopAllExceptCurrentMusic(this.musicTracker, soundManager);
    }
}
