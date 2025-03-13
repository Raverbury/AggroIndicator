package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.accessor.BrainAccessor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerEntityManager.class)
public abstract class ServerEntityManagerMixin {
    /**
     * Mixin to recreate EntityJoinLevelEvent in Neo/Forge
     *
     * @param entity
     * @param worldGenSpawned
     * @param cir
     * @param <T>
     */
    @Inject(method = "addEntity(Lnet/minecraft/world/entity/EntityLike;Z)Z", at = @At(value = "RETURN"))
    private <T extends EntityLike> void aggroindicator$assignOwnerToBrainOnAddEntity(T entity,
                                                                                     boolean worldGenSpawned, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && entity instanceof MobEntity mob && !mob.getWorld()
                .isClient()) {
            ((BrainAccessor) mob.getBrain()).aggroindicator$setBrainOwner(
                    mob);
        }
    }
}
