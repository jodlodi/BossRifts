package io.github.jodlodi.bossrifts.blocks;

import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.registry.ModParticles;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;

@NullMarked
public class RiftAggregatorBlock extends BaseEntityBlock {

	public RiftAggregatorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RiftAggregatorBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.getItemInHand(hand).getItem() instanceof RiftPearlItem) return InteractionResult.PASS;

		if (player instanceof ServerPlayer serverPlayer && this.isPossibleToRespawnInThis()) {
			if (serverPlayer.getRespawnDimension() != level.dimension() || !pos.equals(serverPlayer.getRespawnPosition())) {
				serverPlayer.setRespawnPosition(level.dimension(), pos, 0.0F, false, true);
				level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ModSounds.RIFT_AGGREGATOR_SET_SPAWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public boolean isPossibleToRespawnInThis() {
		return Services.CONFIG.canSetSpawn();
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(100) == 0) {
			level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ModSounds.RIFT_AGGREGATOR_AMBIENT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		if (random.nextInt(4) != 0) return;

		double x = (double)pos.getX() + 0.5D + (0.5D - random.nextDouble());
		double y = (double)pos.getY() + 1.0D;
		double z = (double)pos.getZ() + 0.5D + (0.5D - random.nextDouble());
		double yV = (double)random.nextFloat() * 0.04D;
		level.addParticle(ModParticles.RIFT_SMOKE.get(), x, y, z, 0.0D, yV, 0.0D);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return Services.EVENT::tickAggregator;
	}
}
