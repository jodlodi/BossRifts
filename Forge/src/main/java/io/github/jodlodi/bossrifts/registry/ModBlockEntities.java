package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BossRifts.MOD_ID);

	public static final RegistryObject<BlockEntityType<RiftAggregatorBlockEntity>> RIFT_AGGREGATOR = BLOCK_ENTITIES.register("rift_aggregator", () ->
			BlockEntityType.Builder.of(RiftAggregatorBlockEntity::new, ModBlocks.RIFT_AGGREGATOR.get()).build(null)
	);
}
