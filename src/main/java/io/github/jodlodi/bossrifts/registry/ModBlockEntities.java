package io.github.jodlodi.bossrifts.registry;

import io.github.jodlodi.bossrifts.BossRifts;
import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
	public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BossRifts.MOD_ID);

	public static final RegistryObject<TileEntityType<RiftAggregatorBlockEntity>> RIFT_AGGREGATOR = BLOCK_ENTITIES.register("rift_aggregator", () ->
			TileEntityType.Builder.of(RiftAggregatorBlockEntity::new, ModBlocks.RIFT_AGGREGATOR.get()).build(null)
	);
}
