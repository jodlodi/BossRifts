package io.github.jodlodi.bossrifts.blocks;

import io.github.jodlodi.bossrifts.mixin.HopperBlockEntityAccessor;
import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public class RiftAggregatorBlockEntity extends BlockEntity {
	static final VoxelShape INSIDE = Block.box(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	static final VoxelShape ABOVE = Block.box(0.0D, 16.0D, 0.0D, 16.0D, 48.0D, 16.0D);
	static final VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

	public RiftAggregatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RIFT_AGGREGATOR.get(), pos, state);
	}

	public void tick() {
		if (this.level == null || this.level.isClientSide) return;

		BlockEntity entity = this.level.getBlockEntity(this.worldPosition.below());
		if (entity instanceof HopperBlockEntity hopper) {
			if (!((HopperBlockEntityAccessor) hopper).invokeIsOnCooldown()) {
				hopper.setCooldown(0);
				HopperBlockEntityAccessor.invokeTryMoveItems(this.level, hopper.getBlockPos(), hopper.getBlockState(), hopper, () -> suckInItems(this.level, hopper, this));
			}
		} else {
			for (ItemEntity itementity : getItemsAtAndAbove(this)) {
				itementity.setExtendedLifetime();
			}
		}
	}

	public static boolean suckInItems(Level level, HopperBlockEntity hopper, RiftAggregatorBlockEntity aggregator) {
		Boolean forgeHook = VanillaInventoryCodeHooks.extractHook(level, hopper);
		if (forgeHook != null) return forgeHook;

		Container inventory = HopperBlockEntity.getContainerAt(level, hopper.getBlockPos());
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			return !HopperBlockEntityAccessor.invokeIsEmptyContainer(inventory, direction) && HopperBlockEntityAccessor.invokeGetSlots(inventory, direction).anyMatch(fromSlot ->
					HopperBlockEntityAccessor.invokeTryTakeInItemFromSlot(hopper, inventory, fromSlot, direction)
			);
		} else {
			for (ItemEntity itementity : getItemsAtAndAbove(aggregator)) {
				if (HopperBlockEntity.addItem(hopper, itementity)) return true;
			}
			return false;
		}
	}

	public static List<ItemEntity> getItemsAtAndAbove(RiftAggregatorBlockEntity aggregatorBlock) {
		BlockPos pos = aggregatorBlock.getBlockPos();
		return SUCK.toAabbs().stream().flatMap(bb ->
				Objects.requireNonNull(aggregatorBlock.getLevel()).getEntitiesOfClass(ItemEntity.class, bb.move(pos.getX(), pos.getY() - 0.5D, pos.getZ()), Entity::isAlive).stream()
		).collect(Collectors.toList());
	}
}
