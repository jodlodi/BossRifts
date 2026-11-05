package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.platform.FabricConfigHelper;
import io.github.jodlodi.bossrifts.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BossRifts implements ModInitializer {
	public static final Tag.Named<EntityType<?>> LEGACY_BOSSES = bind("legacy_bosses");
	public static final Tag.Named<EntityType<?>> RIFT_BOSSES = bind("rift_bosses");

	@Override
	public void onInitialize() {
		CommonClass.init();
		FabricConfigHelper.getHelper().generateDefaultConfig();
	}

	private static Tag.Named<EntityType<?>> bind(String string) {
		StaticTagHelper.Wrapper<EntityType<?>> wrapper = new StaticTagHelper.Wrapper<>(Constants.prefix(string));
		EntityTypeTags.HELPER.wrappers.add(wrapper);
		return wrapper;
	}
}
