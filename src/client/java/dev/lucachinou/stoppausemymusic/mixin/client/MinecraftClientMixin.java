package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Final
    private MusicTracker musicTracker;

    @Redirect(method = "reset", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;stopAll()V"))
    private void stoppausemymusic$keepCurrentMusicPlayingDuringReset(SoundManager soundManager) {
        PersistentMusicState.stopAllExceptCurrentMusic(this.musicTracker, soundManager);
    }

    @Redirect(method = "openGameMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;pauseAll()V"))
    private void stoppausemymusic$keepMusicPlayingInPauseMenu(SoundManager soundManager) {
        // Ne rien faire : on supprime complètement la mise en pause audio au menu pause.
    }
}
