package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.blocks.RiftAggregatorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(method = "findRespawnAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private static void onFindRespawnAndUseSpawnBlock(ServerLevel level, BlockPos pos, float respawnAngle, boolean respawnForced, boolean wonGame, CallbackInfoReturnable<Optional<ServerPlayer.RespawnPosAngle>> cir, BlockState state) {
		if (state.getBlock() instanceof RiftAggregatorBlock aggregator && aggregator.isPossibleToRespawnInThis(state)) {
			cir.setReturnValue(Optional.of(new ServerPlayer.RespawnPosAngle(Vec3.atBottomCenterOf(pos.above()), respawnAngle)));
		}
	}
}
