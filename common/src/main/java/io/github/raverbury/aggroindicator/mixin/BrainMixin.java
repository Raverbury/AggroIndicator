package io.github.raverbury.aggroindicator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.raverbury.aggroindicator.accessors.BrainAccess;
import io.github.raverbury.aggroindicator.CommonClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemorySlot;
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
public abstract class BrainMixin implements BrainAccess {
    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories;
    @Unique
    private LivingEntity aggroindicator$brainOwner = null;

    /**
     * setInternalMemory overload #1, set
     */
    @WrapOperation(method = "setMemoryInternal(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/memory/MemorySlot;set(Ljava/lang/Object;)V"))
    private <T> void aggroindicator$dispatchCLCTEOnSetMemory1(MemorySlot<T> instance, T value, Operation<Void> original, @Local(argsOnly = true) MemoryModuleType<T> type) {
        if ((type == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            CommonClass.livingChangeTarget(aggroindicator$brainOwner, (LivingEntity) value);
        }
    }

    /**
     * setInternalMemory overload #1, clear
     */
    @WrapOperation(method = "setMemoryInternal(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/memory/MemorySlot;clear()V"))
    private <T> void aggroindicator$dispatchCLCTEOnClearMemory1(MemorySlot<T> instance, Operation<Void> original, @Local(argsOnly = true) MemoryModuleType<T> type) {
        if ((type == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            CommonClass.livingChangeTarget(aggroindicator$brainOwner, null);
        }
    }

    /**
     * setInternalMemory overload #2, set
     */
    @WrapOperation(method = "setMemoryInternal(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/memory/MemorySlot;set(Ljava/lang/Object;J)V"))
    private <T> void aggroindicator$dispatchCLCTEOnSetMemory2(MemorySlot<T> instance, T value, long timeToLive, Operation<Void> original, @Local(argsOnly = true) MemoryModuleType<T> type) {
        if ((type == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            CommonClass.livingChangeTarget(aggroindicator$brainOwner, (LivingEntity) value);
        }
    }

    /**
     * setInternalMemory overload #2, clear
     */
    @WrapOperation(method = "setMemoryInternal(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/memory/MemorySlot;clear()V"))
    private <T> void aggroindicator$dispatchCLCTEOnClearMemory2(MemorySlot<T> instance, Operation<Void> original, @Local(argsOnly = true) MemoryModuleType<T> type) {
        if ((type == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            CommonClass.livingChangeTarget(aggroindicator$brainOwner, null);
        }
    }

    /*
     * clearMemories
     */
    @Inject(method = "clearMemories", at = @At(value = "RETURN"))
    private void aggroindicator$dispatchCLCTEOnClearAll(CallbackInfo ci) {
        if (this.memories.containsKey(MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            CommonClass.livingChangeTarget(aggroindicator$brainOwner, null);
        }
    }

    /*
     * eraseMemory
     */
    @WrapOperation(method = "eraseMemory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/memory/MemorySlot;clear()V"))
    private <T> void aggroindicator$dispatchCLCTEOnErase(MemorySlot<T> instance,
                                                     Operation<Void> original, @Local(argsOnly = true) MemoryModuleType<T> type) {
        if ((type == MemoryModuleType.ATTACK_TARGET) && (aggroindicator$brainOwner != null)) {
            CommonClass.livingChangeTarget(aggroindicator$brainOwner, null);
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
