package io.github.jodlodi.bossrifts.mixin.accessor;

import net.minecraft.core.particles.SimpleParticleType;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@NullMarked
@Mixin(SimpleParticleType.class)
public interface SimpleParticleTypeAccessor {
	@Invoker("<init>")
	static SimpleParticleType invokeInit(boolean overrideLimiter) {
		return new SimpleParticleType(overrideLimiter) {

		};
	}
}
