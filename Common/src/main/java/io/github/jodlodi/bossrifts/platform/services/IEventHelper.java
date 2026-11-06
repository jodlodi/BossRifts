package io.github.jodlodi.bossrifts.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface IEventHelper {

	@Nullable
	Vec3 onTeleport(Entity entity, Vec3 destination);

	<T extends BlockEntity> void tickAggregator(Level level, BlockPos pos, BlockState state, T dis);
}