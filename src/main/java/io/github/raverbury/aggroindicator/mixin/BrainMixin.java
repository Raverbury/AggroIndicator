package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.event.CustomLivingChangeTargetEvent;
import io.github.raverbury.aggroindicator.util.BrainAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(Brain.class)
public class BrainMixin implements BrainAccess {
    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories;
    @Unique
    private LivingEntity aggroIndicator$brainOwner = null;

    /**
     * Check for modification of ATTACK_TARGET memory type, treat it as a
     * setter call for the purpose of acquiring a new target (nullable) and
     * dispatch CustomLivingChangeTargetEvent
     *
     * @param memoryModuleType
     * @param memoryValue
     * @param ci
     */
    @Inject(method = "setMemoryInternal", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <U> void aggroIndicator$dispatchCLCTEOnSetMemory(MemoryModuleType<U> memoryModuleType, Optional<? extends ExpirableValue<?>> memoryValue, CallbackInfo ci) {
        if ((memoryModuleType == MemoryModuleType.ATTACK_TARGET) && (aggroIndicator$brainOwner != null)) {
            if (memoryValue.isPresent()) {
                MinecraftForge.EVENT_BUS.post(new CustomLivingChangeTargetEvent(
                        aggroIndicator$brainOwner,
                        (LivingEntity) memoryValue.get().getValue()));
            } else {
                MinecraftForge.EVENT_BUS.post(new CustomLivingChangeTargetEvent(
                        aggroIndicator$brainOwner, null));
            }
        }
    }

    @Override
    public void aggroIndicator$setBrainOwner(LivingEntity owner) {
        aggroIndicator$brainOwner = owner;
    }
}
