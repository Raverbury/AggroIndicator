package com.github.raverbury.aggroindicator.common.mixin;

import com.github.raverbury.aggroindicator.common.events.EntityTickEventCallback;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    // @Inject(method = "tickRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    // private void aggroIndicator$entityPreTick(CallbackInfo ci) {
    //     EntityTickEventCallback.EVENT.invoker()
    //             .interact((Entity) (Object) this);
    // }

    @Inject(method = "tickRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void aggroIndicator$entityPostTick(CallbackInfo ci) {
        EntityTickEventCallback.EVENT.invoker()
                .interact((Entity) (Object) this);
    }
}
