package io.github.jodlodi.bossrifts.platform;

import io.github.jodlodi.bossrifts.Constants;
import io.github.jodlodi.bossrifts.platform.services.IConfigHelper;
import io.github.jodlodi.bossrifts.platform.services.IEventHelper;
import io.github.jodlodi.bossrifts.platform.services.IPlatformHelper;
import io.github.jodlodi.bossrifts.platform.services.IRegistryHelper;
import org.jspecify.annotations.NullMarked;

import java.util.ServiceLoader;

@NullMarked
public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);
    public static final IConfigHelper CONFIG = load(IConfigHelper.class);
    public static final IEventHelper EVENT = load(IEventHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}