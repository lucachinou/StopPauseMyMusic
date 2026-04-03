package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Final
    private MusicManager musicManager;

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void stoppausemymusic$preserveCurrentMusicBeforeSetLevel(ClientLevel level, CallbackInfo ci) {
        PersistentMusicState.keepCurrentMusic(this.musicManager);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    private void stoppausemymusic$preserveCurrentMusicBeforeDisconnect(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
        PersistentMusicState.keepCurrentMusic(this.musicManager);
    }

    @Inject(method = "clearClientLevel", at = @At("HEAD"))
    private void stoppausemymusic$preserveCurrentMusicBeforeClearClientLevel(Screen screen, CallbackInfo ci) {
        PersistentMusicState.keepCurrentMusic(this.musicManager);
    }

    @Redirect(method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;stop()V"))
    private void stoppausemymusic$keepCurrentMusicPlayingDuringWorldSwitch(SoundManager soundManager) {
        PersistentMusicState.stopAllExceptCurrentMusic(this.musicManager, soundManager);
    }
}
