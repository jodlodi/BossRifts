package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.services.IRegistryHelper;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@NullMarked
public class FabricRegistryHelper implements IRegistryHelper {

	@Override
	public <T extends ParticleType<?>> MultiSupplier<T> registerParticleType(String id, Supplier<T> supplier) {
		return register(BuiltInRegistries.PARTICLE_TYPE, id, supplier);
	}

	@Override
	public <T extends Block> MultiSupplier<T> registerBlock(String id, Function<BlockBehaviour.Properties, T> block, Supplier<BlockBehaviour.Properties> properties) {
		Identifier location = Constants.prefix(id);
		T registered = Registry.register(BuiltInRegistries.BLOCK, location, block.apply(properties.get().setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), location))));
		return new MultiSupplier<>(() -> registered, location);
	}

	@Override
	public <T extends BlockEntity, B extends Block> MultiSupplier<BlockEntityType<T>> registerBlockEntity(String id, BiFunction<BlockPos, BlockState, T> supplier, @UnknownNullability MultiSupplier<B> block) {
		Identifier location = Constants.prefix(id);

		FabricBlockEntityTypeBuilder<T> builder = FabricBlockEntityTypeBuilder.create(supplier::apply);
		builder.addBlock(block.get());

		BlockEntityType<T> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, location, builder.build());
		return new MultiSupplier<>(() -> type, location);
	}

	@Override
	public <T> MultiSupplier<DataComponentType<T>> registerDataComponentType(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, () -> builder.apply(DataComponentType.builder()).build());
	}

	@Override
	public <T extends Item> MultiSupplier<T> registerItem(String id, Function<Item.Properties, T> item) {
		Identifier location = Constants.prefix(id);
		T registered = Registry.register(BuiltInRegistries.ITEM, location, item.apply(new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), location))));
		return new MultiSupplier<>(() -> registered, location);
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
		Identifier location = Constants.prefix(id);
		T t = Registry.register(registry, location, supplier.get());
		return new MultiSupplier<>(() -> t, location);
	}
}