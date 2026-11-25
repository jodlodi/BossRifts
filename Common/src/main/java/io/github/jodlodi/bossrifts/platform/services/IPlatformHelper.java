package io.github.jodlodi.bossrifts.platform.services;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

	boolean isClientSide();

	default @Nullable Player safeGetLocalPlayer() {
		if (this.isClientSide()) return PlayerGetter.getLocalPlayer();
		else return null;
	}

	default @Nullable Player safeGetLocalPlayer(@Nullable Level level, ItemDisplayContext context, ItemStack stack) {
		if (level == null || !level.isClientSide()) return null;
		return PlayerGetter.getLocalPlayer(level, context, stack);
	}

	class PlayerGetter {
		public static @Nullable Player getLocalPlayer() {
			return Minecraft.getInstance().player;
		}

		public static @Nullable Player getLocalPlayer(Level level, ItemDisplayContext context, ItemStack stack) {
			if (context == ItemDisplayContext.GUI || context.firstPerson()) return Minecraft.getInstance().player;

			for (Player player : level.players()) {
				if (ItemStack.isSameItemSameComponents(player.getUseItem(), stack) ||
						ItemStack.isSameItemSameComponents(player.getMainHandItem(), stack) ||
						ItemStack.isSameItemSameComponents(player.getOffhandItem(), stack)) {
					return player;
				}
			}

			return null;
		}
	}
}