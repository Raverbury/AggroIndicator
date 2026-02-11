package io.github.raverbury.aggroindicator.modules;

import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public final class AggroSoundPlayer {
    private static final long AGGRO_SOUND_COOLDOWN_MS = 400;

    private static final Identifier aggroSoundIdentifier = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "aggro_sound");
    private static final Supplier<SoundEvent> aggroSound = Services.PLATFORM.register(BuiltInRegistries.SOUND_EVENT, aggroSoundIdentifier, () -> SoundEvent.createVariableRangeEvent(aggroSoundIdentifier));

    private static long lastPlayedSoundAt = 0;

    public static void init()
    {
    }

    public static void playClientSoundForPlayer(Player player)
    {
        if (player == null || player.level() == null)
        {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastPlayedSoundAt) > AGGRO_SOUND_COOLDOWN_MS)
        {
            player.level().playLocalSound(player, aggroSound.get(), SoundSource.HOSTILE, 1f, 1f);
            lastPlayedSoundAt = currentTime;
        }
    }
}
