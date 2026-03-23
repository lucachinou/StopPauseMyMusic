package dev.lucachinou.stoppausemymusic.mixin.client;

import dev.lucachinou.stoppausemymusic.client.PersistentMusicState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final private MusicTracker musicTracker;
    @Shadow @Final private SoundManager soundManager;
    @Shadow @Final public WorldRenderer worldRenderer;
    @Shadow @Final public ParticleManager particleManager;
    @Shadow @Final public GameRenderer gameRenderer;
    @Shadow private ClientConnection integratedServerConnection;

    @Shadow
    public abstract void setCameraEntity(Entity entity);

    @Shadow
    public abstract void updateWindowTitle();

    @Inject(method = "setWorld", at = @At("HEAD"), cancellable = true)
    private void stoppausemymusic$keepCurrentMusicPlaying(ClientWorld world, CallbackInfo ci) {
        PersistentMusicState.stopAllExceptCurrentMusic(this.musicTracker, this.soundManager);
        this.setCameraEntity(null);
        this.integratedServerConnection = null;
        this.worldRenderer.setWorld(world);
        this.particleManager.setWorld(world);
        this.gameRenderer.setWorld(world);
        this.updateWindowTitle();
        ci.cancel();
    }
}
