package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.AggroIndicator;
import io.github.raverbury.aggroindicator.accessor.BrainAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
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
public abstract class BrainMixin implements BrainAccessor {

    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;
    @Unique
    private LivingEntity aggroindicator$brainOwner = null;

    /**
     * Check for modification of ATTACK_TARGET memory type, treat it as a
     * setter call for the purpose of acquiring a new target (nullable) and
     * dispatch CustomLivingChangeTargetEvent
     *
     * @param memoryModuleType
     * @param memoryValue
     * @param ci
     */
    @Inject(method = "setMemory", at = @At(value = "INVOKE", target = "Ljava/util" +
            "/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <U> void aggroindicator$dispatchCLCTEOnSetMemory(MemoryModuleType<U> memoryModuleType, Optional<? extends Memory<U>> memoryValue, CallbackInfo ci) {
        if ((memoryModuleType == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            if (memoryValue.isPresent()) {
                AggroIndicator.livingChangeTarget(aggroindicator$brainOwner,
                        (LivingEntity) memoryValue.get().getValue());
            } else {
                AggroIndicator.livingChangeTarget(aggroindicator$brainOwner,
                        null);
            }
        }
    }

    /**
     * Check for existence of ATTACK_TARGET memory type, treat it as a
     * setter call for the purpose of acquiring a new target (null) and dispatch
     * CustomLivingChangeTargetEvent
     *
     * @param ci
     */
    @Inject(method = "forgetAll", at = @At(value = "INVOKE", target = "Ljava/util" +
            "/Map;keySet()Ljava/util/Set;"))
    private void aggroindicator$dispatchCLCTEOnClear(CallbackInfo ci) {
        if (this.memories.containsKey(
                MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            AggroIndicator.livingChangeTarget(aggroindicator$brainOwner,
                    null);
        }
    }

    @Override
    public void aggroindicator$setBrainOwner(LivingEntity owner) {
        aggroindicator$brainOwner = owner;
    }

    @Override
    public LivingEntity aggroindicator$getBrainOwner() {
        return aggroindicator$brainOwner;
    }
}
