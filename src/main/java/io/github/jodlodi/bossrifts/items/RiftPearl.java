package io.github.jodlodi.bossrifts.items;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.entities.RiftPearlEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftPearl extends Item {
	public RiftPearl(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext p_195939_1_) {
		World world = p_195939_1_.getLevel();
		BlockPos blockpos = p_195939_1_.getClickedPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (blockstate.is(Blocks.END_PORTAL_FRAME) && !blockstate.getValue(EndPortalFrameBlock.HAS_EYE)) {
			if (world.isClientSide) {
				return ActionResultType.SUCCESS;
			} else {
				BlockState blockstate1 = blockstate.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(true));
				Block.pushEntitiesUp(blockstate, blockstate1, world, blockpos);
				world.setBlock(blockpos, blockstate1, 2);
				world.updateNeighbourForOutputSignal(blockpos, Blocks.END_PORTAL_FRAME);
				p_195939_1_.getItemInHand().shrink(1);
				world.levelEvent(1503, blockpos, 0);
				BlockPattern.PatternHelper blockpattern$patternhelper = EndPortalFrameBlock.getOrCreatePortalShape().find(world, blockpos);
				if (blockpattern$patternhelper != null) {
					BlockPos blockpos1 = blockpattern$patternhelper.getFrontTopLeft().offset(-3, 0, -3);

					for(int i = 0; i < 3; ++i) {
						for(int j = 0; j < 3; ++j) {
							world.setBlock(blockpos1.offset(i, 0, j), Blocks.END_PORTAL.defaultBlockState(), 2);
						}
					}

					world.globalLevelEvent(1038, blockpos1.offset(1, 0, 1), 0);
				}

				return ActionResultType.CONSUME;
			}
		} else {
			return ActionResultType.PASS;
		}
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		RayTraceResult raytraceresult = getPlayerPOVHitResult(level, player, RayTraceContext.FluidMode.NONE);
		if (raytraceresult.getType() == RayTraceResult.Type.BLOCK && level.getBlockState(((BlockRayTraceResult)raytraceresult).getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
			return ActionResult.pass(stack);
		} else {
			player.startUsingItem(hand);
			if (level instanceof ServerWorld) {
				BlockPos pos = ((ServerWorld)level).getChunkSource().getGenerator().findNearestMapFeature((ServerWorld)level, Structure.STRONGHOLD, player.blockPosition(), 100, false);
				if (pos != null) {
					RiftPearlEntity riftPearl = new RiftPearlEntity(level, player.getX(), player.getY(0.5D), player.getZ());
					if (player instanceof ServerPlayerEntity) {
						riftPearl.signalTo(BossRiftEntity.getSpawnPos(((ServerWorld)level).getServer(), (ServerPlayerEntity) player));
					}
					level.addFreshEntity(riftPearl);

					level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
					level.levelEvent(null, 1003, player.blockPosition(), 0);
					if (!player.abilities.instabuild) {
						stack.shrink(1);
					}

					player.awardStat(Stats.ITEM_USED.get(this));
					player.swing(hand, true);
					return ActionResult.success(stack);
				}
			}

			return ActionResult.consume(stack);
		}
	}
}
