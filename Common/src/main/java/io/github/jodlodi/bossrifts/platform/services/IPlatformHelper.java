package io.github.jodlodi.bossrifts.platform.services;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public interface IPlatformHelper {

	String getPlatformName();

	boolean isModLoaded(String modId);

	boolean isDevelopmentEnvironment();

	default String getEnvironmentName() {
		return isDevelopmentEnvironment() ? "development" : "production";
	}

	Path getConfigDir();

	Packet<?> getAddEntityPacket(Entity entity);
}