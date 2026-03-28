package io.github.raverbury.aggroindicator.modules;

import io.github.raverbury.aggroindicator.config.ClientConfig;
import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.time.Instant;
import java.util.function.Supplier;

public final class AggroSoundPlayer {
    private static final Identifier aggroSoundIdentifier = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "aggro_sound");
    private static final Supplier<SoundEvent> aggroSound = Services.PLATFORM.register(BuiltInRegistries.SOUND_EVENT, aggroSoundIdentifier, () -> SoundEvent.createVariableRangeEvent(aggroSoundIdentifier));

    private static long lastPlayedSoundAtSecond = 0;

    public static void init()
    {
        // needed even if blank, for static initializer
    }

    public static void playClientSoundForPlayer(Player player)
    {
        if (player == null || player.level() == null)
        {
            return;
        }
        ClientConfig config = ClientConfig.cachedOrDefault();
        long currentTime = Instant.now().getEpochSecond();
        if ((currentTime - lastPlayedSoundAtSecond) > config.getAlertSoundCooldown())
        {
            player.level().playLocalSound(player, aggroSound.get(), SoundSource.HOSTILE, 1f, 1f);
            lastPlayedSoundAtSecond = currentTime;
        }
    }
}
