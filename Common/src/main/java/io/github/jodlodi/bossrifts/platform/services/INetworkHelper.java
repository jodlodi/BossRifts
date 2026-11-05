package io.github.jodlodi.bossrifts.platform.services;

import io.github.jodlodi.bossrifts.network.IPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface INetworkHelper {

	void sendToClient(ServerPlayer player, IPacket packet);
}