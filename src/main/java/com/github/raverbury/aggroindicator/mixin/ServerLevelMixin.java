package com.github.raverbury.aggroindicator.mixin;

import com.github.raverbury.aggroindicator.event.EntityPostTickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void aggroindicator$dispatchEPT(Entity entity, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EntityPostTickEvent(entity));
    }
}
