package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public boolean isClientSide() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}
}