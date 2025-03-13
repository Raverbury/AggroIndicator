package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.AggroIndicator;
import net.minecraft.entity.ai.brain.task.RamImpactTask;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RamImpactTask.class)
public abstract class RamImpactTaskMixin {
    @Inject(method = "finishRam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
    private void aggroindicator$goatFinishRam(ServerWorld level, GoatEntity goat,
                                              CallbackInfo ci) {
        AggroIndicator.livingChangeTarget(goat, null);
    }
}
