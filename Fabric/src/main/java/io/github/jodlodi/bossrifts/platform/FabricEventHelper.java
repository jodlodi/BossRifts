package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlockEntity;
import io.github.jodlodi.bossrifts.mixin.accessor.HopperBlockEntityAccessor;
import io.github.jodlodi.bossrifts.platform.services.IEventHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public class FabricEventHelper implements IEventHelper {

    @Override
    public Vec3 onTeleport(Entity entity, Vec3 destination) {
        return destination;
    }

    @Override
    public <T extends BlockEntity> void tickAggregator(Level level, BlockPos pos, BlockState state, T dis) {
        if (level.isClientSide || !(dis instanceof RiftAggregatorBlockEntity aggregator)) return;

        if (level.getBlockEntity(pos.below()) instanceof HopperBlockEntity hopper) {
            if (!((HopperBlockEntityAccessor) hopper).invokeIsOnCooldown()) {
                ((HopperBlockEntityAccessor) hopper).invokeSetCooldown(0);
                HopperBlockEntityAccessor.invokeTryMoveItems(level, hopper.getBlockPos(), hopper.getBlockState(), hopper, () -> suckInItems(hopper, aggregator));
            }
        } else {
            for (ItemEntity itementity : getItemsAtAndAbove(aggregator)) {
                itementity.setExtendedLifetime();
            }
        }
    }

    public static boolean suckInItems(HopperBlockEntity hopper, RiftAggregatorBlockEntity aggregator) {
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