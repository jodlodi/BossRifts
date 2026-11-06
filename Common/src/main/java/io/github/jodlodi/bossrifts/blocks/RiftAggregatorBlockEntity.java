package io.github.jodlodi.bossrifts.blocks;

import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RiftAggregatorBlockEntity extends BlockEntity {
	private static final VoxelShape INSIDE = Block.box(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	private static final VoxelShape ABOVE = Block.box(0.0D, 16.0D, 0.0D, 16.0D, 48.0D, 16.0D);
	public static final VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

	public RiftAggregatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RIFT_AGGREGATOR.get(), pos, state);
	}
}
