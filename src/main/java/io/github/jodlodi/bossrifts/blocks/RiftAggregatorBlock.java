package io.github.jodlodi.bossrifts.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftAggregatorBlock extends Block {
	public RiftAggregatorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new RiftAggregatorBlockEntity();
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			if (serverplayerentity.getRespawnDimension() != level.dimension() || !pos.equals(serverplayerentity.getRespawnPosition())) {
				serverplayerentity.setRespawnPosition(level.dimension(), pos, 0.0F, false, true);
				level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.CONSUME;
	}

	@Override
	public boolean isPossibleToRespawnInThis() {
		return true;
	}

	@Override
	public Optional<Vector3d> getRespawnPosition(BlockState state, EntityType<?> type, IWorldReader world, BlockPos pos, float orientation, @Nullable LivingEntity entity) {
		return Optional.of(Vector3d.atBottomCenterOf(pos.above()));
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World level, BlockPos pos, Random random) {
		if (random.nextInt(100) == 0) {
			level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

		if (random.nextInt(4) != 0) return;

		double x = (double)pos.getX() + 0.5D + (0.5D - random.nextDouble());
		double y = (double)pos.getY() + 1.0D;
		double z = (double)pos.getZ() + 0.5D + (0.5D - random.nextDouble());
		double yV = (double)random.nextFloat() * 0.04D;
		level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, yV, 0.0D);
	}
}
