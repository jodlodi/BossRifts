package io.github.jodlodi.bossrifts.mixin.accessor;

import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelResource.class)
public interface LevelResourceAccessor {

	@Invoker("<init>")
	static LevelResource invokeInit(String string) {
		throw new RuntimeException();
	}
}
