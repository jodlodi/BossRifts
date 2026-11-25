package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import io.github.jodlodi.bossrifts.platform.services.IEventHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ContainerOrHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaInventoryCodeHooks;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public class NeoForgeEventHelper implements IEventHelper {

	@Nullable
	@Override
	public Vec3 onTeleport(Entity entity, ServerLevel level, Vec3 destination) {
		EntityTeleportEvent.TeleportCommand event = EventHooks.onEntityTeleportCommand(entity, level, destination.x, destination.y, destination.z);
		if (event.isCanceled()) return null;
		return new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
	}

	@Override
	public <T extends BlockEntity> void tickAggregator(Level level, BlockPos pos, BlockState state, T dis) {
		if (level.isClientSide() || !(dis instanceof RiftAggregatorBlockEntity aggregator)) return;

		if (level.getBlockEntity(pos.below()) instanceof HopperBlockEntity hopper) {
			if (!hopper.isOnCooldown()) {
				hopper.setCooldown(0);
				HopperBlockEntity.tryMoveItems(level, hopper.getBlockPos(), hopper.getBlockState(), hopper, () -> suckInItems(level, hopper, aggregator));
			}
		} else {
			for (ItemEntity itementity : getItemsAtAndAbove(aggregator)) {
				itementity.setExtendedLifetime();
			}
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	public static boolean suckInItems(Level level, HopperBlockEntity hopper, RiftAggregatorBlockEntity aggregator) {
		BlockPos blockPos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
		BlockState blockState = level.getBlockState(blockPos);
		ContainerOrHandler containerOrHandler = getSourceContainerOrHandler(level, hopper, blockPos, blockState);

		if (containerOrHandler.itemHandler() != null) {
			return VanillaInventoryCodeHooks.extractHook(hopper, containerOrHandler.itemHandler());
		} else {
			for (ItemEntity entity : getItemsAtAndAbove(aggregator)) {
				if (HopperBlockEntity.addItem(hopper, entity)) return true;
			}
			return false;
		}
	}

	private static ContainerOrHandler getSourceContainerOrHandler(Level level, Hopper hopper, BlockPos pos, BlockState state) {
		return getContainerOrHandlerAt(level, pos, state, hopper.getLevelX(), hopper.getLevelY() + (double) 1.0F, hopper.getLevelZ());
	}

	@SuppressWarnings("UnstableApiUsage")
	private static ContainerOrHandler getContainerOrHandlerAt(Level level, BlockPos pos, BlockState state, double x, double y, double z) {
		Container container = getBlockContainer(level, pos, state);
		if (container != null) {
			return new ContainerOrHandler(container, null);
		} else {
			ResourceHandler<ItemResource> blockItemHandler = level.getCapability(Capabilities.Item.BLOCK, pos, state, null, Direction.DOWN);
			return blockItemHandler != null ? new ContainerOrHandler(null, blockItemHandler) : VanillaInventoryCodeHooks.getEntityContainerOrHandler(level, x, y, z, Direction.DOWN);
		}
	}

	private static @Nullable Container getBlockContainer(Level level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();

		if (block instanceof WorldlyContainerHolder holder) {
			return holder.getContainer(state, level, pos);
		} else {
			if (state.hasBlockEntity()) {
				if (level.getBlockEntity(pos) instanceof Container container) {
					if (container instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
						return ChestBlock.getContainer(chestBlock, state, level, pos, true);
					}
					return container;
				}
			}

			return null;
		}
	}

	public static List<ItemEntity> getItemsAtAndAbove(RiftAggregatorBlockEntity aggregatorBlock) {
		BlockPos pos = aggregatorBlock.getBlockPos();
		return RiftAggregatorBlockEntity.SUCK.toAabbs().stream().flatMap(bb ->
				Objects.requireNonNull(aggregatorBlock.getLevel()).getEntitiesOfClass(ItemEntity.class, bb.move(pos.getX(), pos.getY() - 0.5D, pos.getZ()), Entity::isAlive).stream()
		).collect(Collectors.toList());
	}
}