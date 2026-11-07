package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.services.IRegistryHelper;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

@NullMarked
public class FabricRegistryHelper implements IRegistryHelper {

	@Override
	public <T extends ParticleType<?>> MultiSupplier<T> registerParticleType(String id, Supplier<T> supplier) {
		return register(BuiltInRegistries.PARTICLE_TYPE, id, supplier);
	}

	@Override
	public <T extends Block> MultiSupplier<T> registerBlock(String id, Supplier<T> supplier) {
		return register(BuiltInRegistries.BLOCK, id, supplier);
	}

	@Override
	public <T extends BlockEntityType<?>> MultiSupplier<T> registerBlockEntity(String id, Supplier<T> supplier) {
		return register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, supplier);
	}

	@Override
	public <T extends Item> MultiSupplier<T> registerItem(String id, Supplier<T> supplier) {
		return register(BuiltInRegistries.ITEM, id, supplier);
	}

	@Override
	public MultiSupplier<SoundEvent> registerSound(String id, Supplier<SoundEvent> supplier) {
		return register(BuiltInRegistries.SOUND_EVENT, id, supplier);
	}

	@Override
	public <T extends EntityType<?>> MultiSupplier<T> registerEntityType(String id, Supplier<T> supplier) {
		return register(BuiltInRegistries.ENTITY_TYPE, id, supplier);
	}

	private static <T extends G, G> MultiSupplier<T> register(Registry<G> registry, String id, Supplier<T> supplier) {
		ResourceLocation location = Constants.prefix(id);
		T t = Registry.register(registry, location, supplier.get());
		return new MultiSupplier<>(() -> t, location);
	}
}