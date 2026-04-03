package dev.lucachinou.stoppausemymusic.mixin.client.accessor;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicManager.class)
public interface MusicTrackerAccessor {
    @Accessor("currentMusic")
    SoundInstance stoppausemymusic$getCurrent();
}
