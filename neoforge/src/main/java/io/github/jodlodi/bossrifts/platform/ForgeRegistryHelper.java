package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.services.IRegistryHelper;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@NullMarked
public class ForgeRegistryHelper implements IRegistryHelper {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Constants.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Constants.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Constants.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(Registries.SOUND_EVENT, Constants.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);

	@Override
	public <T extends ParticleType<?>> MultiSupplier<T> registerParticleType(String id, Supplier<T> supplier) {
		return new MultiSupplier<>(PARTICLE_TYPES.register(id, supplier), Constants.prefix(id));
	}

	@Override
	public <T extends Block> MultiSupplier<T> registerBlock(String id, Supplier<T> supplier) {
		return new MultiSupplier<>(BLOCKS.register(id, supplier), Constants.prefix(id));
	}

	@Override
	public <T extends BlockEntityType<?>> MultiSupplier<T> registerBlockEntity(String id, Supplier<T> supplier) {
		return new MultiSupplier<>(BLOCK_ENTITIES.register(id, supplier), Constants.prefix(id));
	}

	@Override
	public <T> MultiSupplier<DataComponentType<T>> registerDataComponentType(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return new MultiSupplier<>(DATA_COMPONENTS.register(id, () -> builder.apply(DataComponentType.builder()).build()), Constants.prefix(id));
	}

	@Override
	public <T extends Item> MultiSupplier<T> registerItem(String id, Supplier<T> supplier) {
		return new MultiSupplier<>(ITEMS.register(id, supplier), Constants.prefix(id));
	}

	@Override
	public MultiSupplier<SoundEvent> registerSound(String id, Supplier<SoundEvent> supplier) {
		return new MultiSupplier<>(SOUND_TYPES.register(id, supplier), Constants.prefix(id));
	}

	@Override
	public <T extends EntityType<?>> MultiSupplier<T> registerEntityType(String id, Supplier<T> supplier) {
		return new MultiSupplier<>(ENTITY_TYPES.register(id, supplier), Constants.prefix(id));
	}
}