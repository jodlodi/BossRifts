package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.mixin.accessor.SimpleParticleTypeAccessor;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.particles.SimpleParticleType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModParticles {
	public static final MultiSupplier<SimpleParticleType> RIFT_SMOKE = Services.REGISTRY.registerParticleType("rift_smoke", () -> SimpleParticleTypeAccessor.invokeInit(false));

	public static void init() {

	}
}
