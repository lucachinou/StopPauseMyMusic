package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import dev.lucachinou.stoppausemymusic.mixin.client.accessor.MusicTrackerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "shouldReplace", at = @At("HEAD"), cancellable = true)
    private static void stoppausemymusic$preventTransitionReplacement(MusicSound newSound, SoundInstance currentSound, CallbackInfoReturnable<Boolean> cir) {
        if (PersistentMusicState.shouldKeepPlaying(currentSound, MinecraftClient.getInstance().getSoundManager())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void stoppausemymusic$clearExpiredPreservedMusic(CallbackInfo ci) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) this).stoppausemymusic$getCurrent();
        PersistentMusicState.validate(currentMusic, this.client.getSoundManager());
    }

    @Inject(method = "stop()V", at = @At("TAIL"))
    private void stoppausemymusic$clearStateAfterStop(CallbackInfo ci) {
        PersistentMusicState.clear();
    }

    @Inject(method = "stop(Lnet/minecraft/sound/MusicSound;)V", at = @At("TAIL"))
    private void stoppausemymusic$clearStateAfterTypedStop(MusicSound musicSound, CallbackInfo ci) {
        PersistentMusicState.clear();
    }
}
