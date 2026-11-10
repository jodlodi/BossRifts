package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.registry.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommonClass {

	public static void init() {
		ModParticles.init();
		ModBlocks.init();
		ModBlockEntities.init();
		ModEntities.init();
		ModDataComponents.init();
		ModItems.init();
		ModSounds.init();
	}
}