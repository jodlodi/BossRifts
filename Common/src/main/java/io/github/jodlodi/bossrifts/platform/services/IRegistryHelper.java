package io.github.jodlodi.bossrifts.platform.services;

import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@NullMarked
public interface IRegistryHelper {

	<T extends ParticleType<?>> MultiSupplier<T> registerParticleType(String id, Supplier<T> supplier);

	<T extends Block> MultiSupplier<T> registerBlock(String id, Supplier<T> supplier);

	<T extends BlockEntityType<?>> MultiSupplier<T> registerBlockEntity(String id, Supplier<T> supplier);

	<T> MultiSupplier<DataComponentType<T>> registerDataComponentType(String id, UnaryOperator<DataComponentType.Builder<T>> builder);

	<T extends Item> MultiSupplier<T> registerItem(String id, Supplier<T> supplier);

	MultiSupplier<SoundEvent> registerSound(String id, Supplier<SoundEvent> supplier);

	<T extends EntityType<?>> MultiSupplier<T> registerEntityType(String id, Supplier<T> supplier);
}
