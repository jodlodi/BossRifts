package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.iRift;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID)
public class RiftConfig {
	public static final Server CONFIG;
	public static final ForgeConfigSpec SPEC;

	static {
		final Pair<Server, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Server::new);
		CONFIG = pair.getLeft();
		SPEC = pair.getRight();
	}

	public static class Server {
		public final ConfigValue<Boolean> legacyModeConfig;
		public final ConfigValue<Integer> expireSpanConfig;
		public final ConfigValue<Boolean> oneTimeUse;
		public final ConfigValue<Double> expCost;
		public final ConfigValue<Double> range;
		public final ConfigValue<Boolean> riftAggregatorSetSpawn;

		private Server(ForgeConfigSpec.Builder builder) {
			builder.comment("Should the mod behave like it did in mod version 1.0?" +
					"\nWith this, instead of being items, rifts appear as entities when tagged bosses are slayed.");
			this.legacyModeConfig = builder.define(of("Enable legacy mode:"), false);

			builder.comment("How long should a Rift remain charged for? (20 ticks is 1 second)" +
					"\nAlternatively, a negative value makes it never expire.");
			this.expireSpanConfig = builder.define(of("Rift empowerment tick count:"), AnimUtil.minutesToTicks(5));

			builder.comment("Should a Rift be one time use?" +
					"\nThe item will not leave your inventory upon use. On legacy mode, the Rift entity will remain there after it's use, until it expires.");
			this.oneTimeUse = builder.define(of("Rift is one time use:"), true);

			builder.comment("Should activating a Rift Pearl cost exp instead of killing a boss?" +
					"\nIf legacy behavior is enabled, a rift will both require a killed boss and an exp cost to activate." +
					"\nA negative value disables this. A positive number indicates the level cost per chunk. 0 makes rifts free to use.");
			this.expCost = builder.define(of("Level cost per chunk:"), -1.0D);

			builder.comment("When placed as en entity, or when playing on legacy mode, how big of a range should a Rift have?");
			this.range = builder.defineInRange(of("Rift entity range (in blocks):"), 2.5D, 1.0D, 32.0D);

			builder.comment("Should the Rift Aggregators be usable as spawn points?" +
					"\nWith this setting off, they still have a use as locations you can link a Rift Pearl to.");
			this.riftAggregatorSetSpawn = builder.define(of("Rift Aggregator spawn setting:"), true);
		}

		protected static List<String> of(String... s) {
			return Arrays.asList(s);
		}
	}

	public static boolean isV1() {
		return CONFIG.legacyModeConfig.get();
	}

	public static boolean isExpired(long time) {
		int span = CONFIG.expireSpanConfig.get();
		if (span < 0) return false;
		return empowerDuration() < time;
	}

	public static int empowerDuration() {
		return Math.max(CONFIG.expireSpanConfig.get(), 0) + iRift.TRANSITION_TIME;
	}

	public static boolean isOneTimeUse() {
		return CONFIG.oneTimeUse.get();
	}

	public static double getExpCost() {
		return CONFIG.expCost.get();
	}

	public static double getRange() {
		return CONFIG.range.get();
	}

	public static Tags.IOptionalNamedTag<EntityType<?>> getTag() {
		return isV1() ? BossRifts.LEGACY_BOSSES : BossRifts.RIFT_BOSSES;
	}

	public static boolean canSetSpawn() {
		return CONFIG.riftAggregatorSetSpawn.get();
	}
}
