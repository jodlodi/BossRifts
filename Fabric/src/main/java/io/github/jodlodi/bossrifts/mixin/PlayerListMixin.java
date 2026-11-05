package io.github.jodlodi.bossrifts.mixin;

import io.github.jodlodi.bossrifts.network.client.SendRespawnDataPacket;
import io.github.jodlodi.bossrifts.platform.Services;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@NullMarked
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Inject(method = "placeNewPlayer", at = @At(value = "TAIL"))
	private void onPlaceNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
		Services.NETWORK.sendToClient(serverPlayer, new SendRespawnDataPacket(
				Optional.ofNullable(serverPlayer.getRespawnPosition()),
				Optional.of(serverPlayer.getRespawnDimension())
		));
	}
}
