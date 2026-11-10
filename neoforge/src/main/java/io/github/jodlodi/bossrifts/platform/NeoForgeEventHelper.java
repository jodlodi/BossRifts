package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import io.github.jodlodi.bossrifts.platform.services.IEventHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.items.VanillaInventoryCodeHooks;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public class NeoForgeEventHelper implements IEventHelper {

	@Nullable
	@Override
	public Vec3 onTeleport(Entity entity, Vec3 destination) {
		EntityTeleportEvent.TeleportCommand event = EventHooks.onEntityTeleportCommand(entity, destination.x, destination.y, destination.z);
		if (event.isCanceled()) return null;
		return new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
	}

	@Override
	public <T extends BlockEntity> void tickAggregator(Level level, BlockPos pos, BlockState state, T dis) {
		if (level.isClientSide || !(dis instanceof RiftAggregatorBlockEntity aggregator)) return;

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

	public static boolean suckInItems(Level level, Hopper hopper, RiftAggregatorBlockEntity aggregator) {
		Boolean forgeHook = VanillaInventoryCodeHooks.extractHook(level, hopper);
		if (forgeHook != null) return forgeHook;

		for (ItemEntity itementity : getItemsAtAndAbove(aggregator)) {
			if (HopperBlockEntity.addItem(hopper, itementity)) return true;
		}

		return false;
	}

	public static List<ItemEntity> getItemsAtAndAbove(RiftAggregatorBlockEntity aggregatorBlock) {
		BlockPos pos = aggregatorBlock.getBlockPos();
		return RiftAggregatorBlockEntity.SUCK.toAabbs().stream().flatMap(bb ->
				Objects.requireNonNull(aggregatorBlock.getLevel()).getEntitiesOfClass(ItemEntity.class, bb.move(pos.getX(), pos.getY() - 0.5D, pos.getZ()), Entity::isAlive).stream()
		).collect(Collectors.toList());
	}
}