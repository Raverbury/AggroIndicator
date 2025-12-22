package io.github.raverbury.aggroindicator.mixin;

import io.github.raverbury.aggroindicator.accessors.BrainAccess;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin {

    /**
     * Mixin to recreate EntityJoinLevelEvent in Neo/Forge
     * @param entity
     * @param worldGenSpawned
     * @param cir
     * @param <T>
     */
    @Inject(method = "addEntity", at = @At(value = "RETURN"))
    private <T extends EntityAccess> void aggroindicator$assignOwnerToBrainOnAddEntity(T entity,
                                                                                       boolean worldGenSpawned, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && entity instanceof Mob mob && !mob.level().isClientSide) {
            ((BrainAccess) mob.getBrain()).aggroindicator$setBrainOwner(
                    mob);
        }
    }
}
