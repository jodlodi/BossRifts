package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.network.client.ClearRiftCachePacket;
import io.github.jodlodi.bossrifts.network.client.ClientBoundConfigPacket;
import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.FabricConfigHelper;
import io.github.jodlodi.bossrifts.platform.Services;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@NullMarked
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

	@Inject(method = "placeNewPlayer", at = @At(value = "TAIL"))
	private void onPlaceNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		Services.NETWORK.sendToClient(player, new ClientBoundConfigPacket(FabricConfigHelper.getHelper().saveToTag()));
		Services.NETWORK.sendToClient(player, new ClearRiftCachePacket());
		Services.NETWORK.sendToClient(player, new SendRespawnDataPacket(player.getRespawnPosition(), player.getRespawnDimension()));
	}
}
