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
    private static final int TRANSITION_GRACE_TICKS = 40;

    private static SoundInstance preservedMusic;
    private static int graceTicksRemaining;

    private PersistentMusicState() {
    }

    public static boolean keepCurrentMusic(MusicTracker musicTracker) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) musicTracker).stoppausemymusic$getCurrent();
        if (currentMusic == null) {
            clear();
            return false;
        }

        preserve(currentMusic);
        return true;
    }

    public static void stopAllExceptCurrentMusic(MusicTracker musicTracker, SoundManager soundManager) {
        SoundInstance currentMusic = ((MusicTrackerAccessor) musicTracker).stoppausemymusic$getCurrent();
        if (currentMusic == null) {
            clear();
            soundManager.stopAll();
            return;
        }

        preserve(currentMusic);
        SoundSystem soundSystem = ((SoundManagerAccessor) soundManager).stoppausemymusic$getSoundSystem();

        for (SoundInstance sound : new ArrayList<>(((SoundSystemAccessor) soundSystem).stoppausemymusic$getSources().keySet())) {
            if (sound != currentMusic) {
                soundManager.stop(sound);
            }
        }
    }

    public static boolean shouldKeepPlaying(SoundInstance currentMusic, SoundManager soundManager) {
        if (preservedMusic == null || currentMusic != preservedMusic) {
            return false;
        }

        if (soundManager.isPlaying(currentMusic) || graceTicksRemaining > 0) {
            return true;
        }

        clear();
        return false;
    }

    public static void validate(SoundInstance currentMusic, SoundManager soundManager) {
        if (preservedMusic == null) {
            return;
        }

        if (currentMusic != preservedMusic) {
            clear();
            return;
        }

        if (soundManager.isPlaying(currentMusic)) {
            graceTicksRemaining = TRANSITION_GRACE_TICKS;
            return;
        }

        if (graceTicksRemaining > 0) {
            graceTicksRemaining--;
            return;
        }

        clear();
    }

    public static void clear() {
        preservedMusic = null;
        graceTicksRemaining = 0;
    }

    private static void preserve(SoundInstance currentMusic) {
        preservedMusic = currentMusic;
        graceTicksRemaining = TRANSITION_GRACE_TICKS;
    }
}
