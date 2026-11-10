package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.platform.services.IPlatformHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkHooks;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Packet<?> getAddEntityPacket(Entity entity) {
        return NetworkHooks.getEntitySpawningPacket(entity);
    }
}