package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@NullMarked
@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin<T extends EntityAccess> {
	@Inject(method = "addEntity", at = @At(value = "HEAD"))
	private void onAdd(T entityAccess, boolean bl, CallbackInfoReturnable<Boolean> cir) {
		if (entityAccess instanceof BossRiftEntity bossRift) bossRift.spawn();
	}
}
