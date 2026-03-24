package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import dev.lucachinou.stoppausemymusic.mixin.client.accessor.MusicTrackerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;stop(Lnet/minecraft/client/sound/SoundInstance;)V"))
    private void stoppausemymusic$preventTransitionReplacement(SoundManager soundManager, SoundInstance currentSound) {
        if (!PersistentMusicState.shouldKeepPlaying(currentSound, soundManager)) {
            soundManager.stop(currentSound);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stoppausemymusic$clearExpiredPreservedMusic(CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        PersistentMusicState.validate(currentMusic, this.client.getSoundManager());
    }

    @Inject(method = "stop()V", at = @At("HEAD"), cancellable = true)
    private void stoppausemymusic$keepPreservedMusicOnStop(CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        if (PersistentMusicState.shouldKeepPlaying(currentMusic, this.client.getSoundManager())) {
            ci.cancel();
        }
    }

    @Inject(method = "stop(Lnet/minecraft/sound/MusicSound;)V", at = @At("HEAD"), cancellable = true)
    private void stoppausemymusic$keepPreservedMusicOnTypedStop(MusicSound musicSound, CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        if (PersistentMusicState.shouldKeepPlaying(currentMusic, this.client.getSoundManager())) {
            ci.cancel();
        }
    }
}
