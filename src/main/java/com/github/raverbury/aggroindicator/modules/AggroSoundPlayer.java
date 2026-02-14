package com.github.raverbury.aggroindicator.modules;

import com.github.raverbury.aggroindicator.AggroIndicator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class AggroSoundPlayer {
    private static final long AGGRO_SOUND_COOLDOWN_MS = 400;

    private static final ResourceLocation aggroSoundIdentifier =
            new ResourceLocation(AggroIndicator.MODID, "aggro_sound");
    private  static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
                    AggroIndicator.MODID);
    private static final Supplier<SoundEvent> AGGRO_SOUND =
            SOUNDS.register("aggro_sound",
                    () -> SoundEvent.createVariableRangeEvent(aggroSoundIdentifier));

    private static long lastPlayedSoundAt = 0;

    public static void init()
    {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
            player.level().playLocalSound(player.blockPosition(), AGGRO_SOUND.get(),
                    SoundSource.HOSTILE, 1f, 1f, false);
            lastPlayedSoundAt = currentTime;
        }
    }
}