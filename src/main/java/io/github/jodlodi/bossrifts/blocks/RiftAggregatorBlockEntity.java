package io.github.jodlodi.bossrifts.blocks;

import io.github.jodlodi.bossrifts.registry.ModBlockEntities;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftAggregatorBlockEntity extends TileEntity implements ITickableTileEntity {
	static final VoxelShape INSIDE = Block.box(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	static final VoxelShape ABOVE = Block.box(0.0D, 16.0D, 0.0D, 16.0D, 48.0D, 16.0D);
	static final VoxelShape SUCK = VoxelShapes.or(INSIDE, ABOVE);

	public RiftAggregatorBlockEntity() {
		super(ModBlockEntities.RIFT_AGGREGATOR.get());
	}

	@Override
	public void tick() {
		if (this.level == null || this.level.isClientSide) return;

		TileEntity entity = this.level.getBlockEntity(this.worldPosition.below());
		if (entity instanceof HopperTileEntity) {
			HopperTileEntity hopper = (HopperTileEntity) entity;

			if (!hopper.isOnCooldown()) {
				hopper.setCooldown(0);
				hopper.tryMoveItems(() -> suckInItems(hopper, this));
			}
		} else {
			for (ItemEntity itementity : getItemsAtAndAbove(this)) {
				itementity.setExtendedLifetime();
			}
		}
	}

	public static boolean suckInItems(IHopper hopper, RiftAggregatorBlockEntity aggregator) {
		Boolean forgeHook = VanillaInventoryCodeHooks.extractHook(hopper);
		if (forgeHook != null) return forgeHook;

		IInventory inventory = HopperTileEntity.getSourceContainer(hopper);
		if (inventory != null) {
			Direction direction = Direction.DOWN;
			return !HopperTileEntity.isEmptyContainer(inventory, direction) && HopperTileEntity.getSlots(inventory, direction).anyMatch(fromSlot ->
					HopperTileEntity.tryTakeInItemFromSlot(hopper, inventory, fromSlot, direction)
			);
		} else {
			for (ItemEntity itementity : getItemsAtAndAbove(aggregator)) {
				if (HopperTileEntity.addItem(hopper, itementity)) return true;
			}
			return false;
		}
	}

	public static List<ItemEntity> getItemsAtAndAbove(RiftAggregatorBlockEntity aggregatorBlock) {
		BlockPos pos = aggregatorBlock.getBlockPos();
		return SUCK.toAabbs().stream().flatMap(bb ->
				Objects.requireNonNull(aggregatorBlock.getLevel()).getEntitiesOfClass(ItemEntity.class, bb.move(pos.getX(), pos.getY() - 0.5D, pos.getZ()), EntityPredicates.ENTITY_STILL_ALIVE).stream()
		).collect(Collectors.toList());
	}
}
