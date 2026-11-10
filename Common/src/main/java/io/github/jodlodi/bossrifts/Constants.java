package io.github.jodlodi.bossrifts;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Constants {
	public static final String MOD_ID = "bossrifts";
	public static final String MOD_NAME = "Boss Rifts";
	public static final Logger LOG = LogManager.getLogger(MOD_NAME);

	public static ResourceLocation prefix(String key) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, key);
	}
}