package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Redirect(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;stop()V"))
    private void stoppausemymusic$keepMusicDuringWorldTransfer(MusicTracker musicTracker) {
        if (!PersistentMusicState.keepCurrentMusic(musicTracker, MinecraftClient.getInstance().getSoundManager())) {
            musicTracker.stop();
        }
    }
}
