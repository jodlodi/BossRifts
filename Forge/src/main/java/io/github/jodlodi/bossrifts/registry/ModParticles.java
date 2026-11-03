package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BossRifts.MOD_ID);
	public static final RegistryObject<SimpleParticleType> RIFT_SMOKE = PARTICLE_TYPES.register("rift_smoke", () -> new SimpleParticleType(false));
}
