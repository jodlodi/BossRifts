package io.github.jodlodi.bossrifts.blocks;

import com.mojang.serialization.MapCodec;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.Services;
import io.github.jodlodi.bossrifts.registry.ModParticles;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;


@NullMarked
public class RiftAggregatorBlock extends BaseEntityBlock {
	public static final MapCodec<RiftAggregatorBlock> CODEC = simpleCodec(RiftAggregatorBlock::new);

	public RiftAggregatorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
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
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (player.getItemInHand(hand).getItem() instanceof RiftPearlItem pearlItem) {
			level.playSound(null, pos, ModSounds.RIFT_PEARL_LOCK.get(), SoundSource.PLAYERS, 0.65F, 1.0F);

			boolean consume = !player.getAbilities().instabuild && stack.getCount() == 1;
			if (consume) {
				pearlItem.makeAggregatorPearl(level.dimension(), pos, stack);
			} else {
				ItemStack newStack = stack.copy();
				if (!player.getAbilities().instabuild) stack.shrink(1);

				pearlItem.makeAggregatorPearl(level.dimension(), pos, newStack);
				if (!player.addItem(newStack)) player.drop(newStack, false);
			}

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (this.isPossibleToRespawnInThis(state)) {
			if (player instanceof ServerPlayer serverPlayer) {
				ServerPlayer.RespawnConfig config = serverPlayer.getRespawnConfig();

				if (config == null || config.respawnData().dimension() != level.dimension() || !pos.equals(config.respawnData().pos())) {
					serverPlayer.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(level.dimension(), pos, 0.0F, 0.0F), true), true);
					level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ModSounds.RIFT_AGGREGATOR_SET_SPAWN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public boolean isPossibleToRespawnInThis(BlockState state) {
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
