package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.services.IRegistryHelper;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

@NullMarked
public class ForgeRegistryHelper implements IRegistryHelper {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Constants.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

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