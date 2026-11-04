package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@NullMarked
@Mixin(Player.class)
public class PlayerMixin {
	@Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private static void onFindRespawnPositionAndUseSpawnBlock(ServerLevel level, BlockPos pos, float respawnAngle, boolean respawnForced, boolean wonGame, CallbackInfoReturnable<Optional<Vec3>> cir, BlockState state) {
		if (state.getBlock() instanceof RiftAggregatorBlock aggregator && aggregator.isPossibleToRespawnInThis()) {
			cir.setReturnValue(Optional.of(Vec3.atBottomCenterOf(pos.above())));
		}
	}
}
