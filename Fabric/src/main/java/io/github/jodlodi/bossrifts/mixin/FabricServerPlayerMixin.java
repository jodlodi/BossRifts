package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class FabricServerPlayerMixin {

	@Inject(method = "setRespawnPosition", at = @At(value = "HEAD"))
	private void onSetRespawnPosition(ServerPlayer.RespawnConfig config, boolean showMessage, CallbackInfo ci) {
		Services.NETWORK.sendToClient((ServerPlayer) (Object) this, new SendRespawnDataPacket(
				config != null ? config.respawnData().pos() : null,
				config != null ? config.respawnData().dimension() : Level.OVERWORLD
		));
	}
}
