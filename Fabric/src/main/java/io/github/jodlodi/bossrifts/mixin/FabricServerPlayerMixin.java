package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class FabricServerPlayerMixin {

	@Inject(method = "setRespawnPosition", at = @At(value = "HEAD"))
	private void onSetRespawnPosition(ResourceKey<Level> resourceKey, @Nullable BlockPos blockPos, float f, boolean bl, boolean bl2, CallbackInfo ci) {
		Services.NETWORK.sendToClient((ServerPlayer) (Object) this, new SendRespawnDataPacket(blockPos, resourceKey));
	}
}
