package io.github.raverbury.aggroindicator.common.mixins;

import io.github.raverbury.aggroindicator.common.events.CustomLivingChangeTargetEvent;
import io.github.raverbury.aggroindicator.common.BrainAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.common.NeoForge;
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
    @Inject(method = "setMemoryInternal", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <U> void aggroindicator$dispatchCLCTEOnSetMemory(MemoryModuleType<U> memoryModuleType, Optional<? extends ExpirableValue<?>> memoryValue, CallbackInfo ci) {
        if ((memoryModuleType == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            if (memoryValue.isPresent()) {
                NeoForge.EVENT_BUS.post(new CustomLivingChangeTargetEvent(
                        aggroindicator$brainOwner,
                        (LivingEntity) memoryValue.get().getValue()));
            } else {
                NeoForge.EVENT_BUS.post(new CustomLivingChangeTargetEvent(
                        aggroindicator$brainOwner, null));
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
    @Inject(method = "clearMemories", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"))
    private void aggroindicator$dispatchCLCTEOnClear(CallbackInfo ci) {
        if (this.memories.containsKey(
                MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            NeoForge.EVENT_BUS.post(
                    new CustomLivingChangeTargetEvent(aggroindicator$brainOwner,
                            null));
        }
    }

    @Override
    public void aggroindicator$setBrainOwner(LivingEntity owner) {
        aggroindicator$brainOwner = owner;
    }
}