package dev.lucachinou.stoppausemymusic.client;

import dev.lucachinou.stoppausemymusic.mixin.client.accessor.MusicTrackerAccessor;
import dev.lucachinou.stoppausemymusic.mixin.client.accessor.SoundManagerAccessor;
import dev.lucachinou.stoppausemymusic.mixin.client.accessor.SoundSystemAccessor;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;

import java.util.ArrayList;

public final class PersistentMusicState {
    private static SoundInstance preservedMusic;

    private PersistentMusicState() {
    }

    public static boolean keepCurrentMusic(MusicTracker musicTracker, SoundManager soundManager) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) musicTracker).stoppausemymusic$getCurrent();
        if (currentMusic == null || !soundManager.isPlaying(currentMusic)) {
            clear();
            return false;
        }

        preservedMusic = currentMusic;
        return true;
    }

    public static void stopAllExceptCurrentMusic(MusicTracker musicTracker, SoundManager soundManager) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) musicTracker).stoppausemymusic$getCurrent();
        if (currentMusic == null || !soundManager.isPlaying(currentMusic)) {
            clear();
            soundManager.stopAll();
            return;
        }

        preservedMusic = currentMusic;
        SoundSystem soundSystem = ((SoundManagerAccessor) soundManager).stoppausemymusic$getSoundSystem();

        for (Object sound : new ArrayList<>(((SoundSystemAccessor) soundSystem).stoppausemymusic$getSources().keySet())) {
            if (sound != currentMusic) {
                soundManager.stop((SoundInstance) sound);
            }
        }
    }

    public static boolean shouldKeepPlaying(SoundInstance currentMusic, SoundManager soundManager) {
        if (preservedMusic == null) {
            return false;
        }

        if (currentMusic == preservedMusic && soundManager.isPlaying(currentMusic)) {
            return true;
        }

        clear();
        return false;
    }

    public static void validate(SoundInstance currentMusic, SoundManager soundManager) {
        if (preservedMusic != null && (currentMusic != preservedMusic || !soundManager.isPlaying(currentMusic))) {
            clear();
        }
    }

    public static void clear() {
        preservedMusic = null;
    }
}
