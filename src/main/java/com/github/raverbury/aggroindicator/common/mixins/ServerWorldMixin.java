package com.github.raverbury.aggroindicator.common.mixins;

import com.github.raverbury.aggroindicator.common.events.EntityTickEventCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    /**
     * Dispatch an EntityTick after entity is ticked
     * @param entity
     * @param ci
     */
    @Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet" + "/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void aggroIndicator$entityPostTick(Entity entity, CallbackInfo ci) {
        EntityTickEventCallback.EVENT.invoker().interact(entity);
    }
}