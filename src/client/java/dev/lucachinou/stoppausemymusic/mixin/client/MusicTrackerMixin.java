package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import dev.lucachinou.stoppausemymusic.mixin.client.accessor.MusicTrackerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicTrackerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void stoppausemymusic$preventTransitionReplacement(SoundManager soundManager, SoundInstance currentSound) {
        if (!PersistentMusicState.shouldKeepPlaying(currentSound, soundManager)) {
            soundManager.stop(currentSound);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stoppausemymusic$clearExpiredPreservedMusic(CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        PersistentMusicState.validate(currentMusic, this.minecraft.getSoundManager());
    }

    @Inject(method = "stopPlaying()V", at = @At("HEAD"), cancellable = true)
    private void stoppausemymusic$keepPreservedMusicOnStop(CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        if (PersistentMusicState.shouldKeepPlaying(currentMusic, this.minecraft.getSoundManager())) {
            ci.cancel();
        }
    }

    @Inject(method = "stopPlaying(Lnet/minecraft/sounds/Music;)V", at = @At("HEAD"), cancellable = true)
    private void stoppausemymusic$keepPreservedMusicOnTypedStop(Music music, CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        if (PersistentMusicState.shouldKeepPlaying(currentMusic, this.minecraft.getSoundManager())) {
            ci.cancel();
        }
    }
}
