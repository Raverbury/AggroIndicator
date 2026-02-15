package io.github.raverbury.aggroindicator.modules;

import io.github.raverbury.aggroindicator.AggroIndicator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class AggroSoundPlayer {
    private static final long AGGRO_SOUND_COOLDOWN_MS = 400;

    private static final Identifier aggroSoundIdentifier =
            Identifier.of(AggroIndicator.MOD_ID, "aggro_sound");
    private static final SoundEvent aggroSound =
            Registry.register(Registries.SOUND_EVENT,
                    aggroSoundIdentifier,
                    SoundEvent.of(aggroSoundIdentifier));

    private static long lastPlayedSoundAt = 0;

    public static void init() {
    }

    public static void playClientSoundForPlayer(PlayerEntity player) {
        if (player == null || player.getWorld() == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastPlayedSoundAt) > AGGRO_SOUND_COOLDOWN_MS) {
            player.getWorld().playSoundAtBlockCenter(player.getBlockPos(),
                    aggroSound,
                    SoundCategory.HOSTILE, 1f, 1f, false);
            lastPlayedSoundAt = currentTime;
        }
    }
}