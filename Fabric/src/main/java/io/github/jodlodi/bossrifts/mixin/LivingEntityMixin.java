package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.platform.services.IConfigHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "die", at = @At(value = "HEAD"))
	private void onDie(DamageSource source, CallbackInfo ci) {
		if (this.getType().is(Services.CONFIG.getTag()) && !this.level().isClientSide()) {
			LivingEntity dis = (LivingEntity) (Object) this;
			if (!Services.CONFIG.isV1()) IConfigHelper.triggerV2(dis, source);
			else IConfigHelper.triggerV1(dis);
		}
	}
}
