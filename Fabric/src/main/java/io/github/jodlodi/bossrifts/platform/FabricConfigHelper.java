package io.github.jodlodi.bossrifts.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.jodlodi.bossrifts.mixin.accessor.LevelResourceAccessor;
import io.github.jodlodi.bossrifts.platform.services.IConfigHelper;
import io.github.jodlodi.bossrifts.util.AnimUtil;
import io.github.jodlodi.bossrifts.util.iRift;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@NullMarked
public class FabricConfigHelper implements IConfigHelper {
	private static final String FILE_NAME = "boss_rifts.json";
	private static final String DEFAULT_CONFIGS_DIR = "defaultconfigs";
	private static final LevelResource CONFIG = LevelResourceAccessor.invokeInit("config/" + FILE_NAME);
	private static final String KEY_LEGACY = "legacyMode";
	private static final String KEY_EXPIRE = "riftEmpowermentTicks";
	private static final String KEY_ONE_TIME = "oneTimeUse";
	private static final String KEY_EXP_COST = "levelCostPerChunk";
	private static final String KEY_RANGE = "riftEntityRange";
	private static final String KEY_SPAWN = "aggregatorSetsSpawn";

	private boolean legacyMode = DEFAULT_LEGACY_MODE;
	private int expireSpan = DEFAULT_EXPIRE_SPAN;
	private boolean oneTimeUse = DEFAULT_ONE_TIME_USE;
	private double expCost = DEFAULT_EXP_COST;
	private double range = DEFAULT_RANGE;
	private boolean riftAggregatorSetSpawn = DEFAULT_RIFT_AGGREGATOR_SET_SPAWN;

	public static FabricConfigHelper getHelper() {
		return (FabricConfigHelper) Services.CONFIG;
	}

	public void onServerBoot(MinecraftServer server) {
		Path path = server.getWorldPath(CONFIG).normalize();
		try {
			Path parent = path.getParent();
			if (parent != null) Files.createDirectories(parent);

			if (Files.notExists(path)) {
				Path template = FabricLoader.getInstance().getGameDir().resolve(DEFAULT_CONFIGS_DIR).resolve(FILE_NAME);
				if (Files.isRegularFile(template)) Files.copy(template, path);
			}

			if (Files.exists(path)) {
				try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
					JsonElement root = new JsonParser().parse(reader);
					if (root.isJsonObject()) this.load(root.getAsJsonObject());
				}
			}

			Files.writeString(path, this.genString(), StandardCharsets.UTF_8);
		} catch (Exception ignored) {
			//Didn't ask
		}
	}

	public CompoundTag saveToTag() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean(KEY_LEGACY, this.legacyMode);
		tag.putInt(KEY_EXPIRE, this.expireSpan);
		tag.putBoolean(KEY_ONE_TIME, this.oneTimeUse);
		tag.putDouble(KEY_EXP_COST, this.expCost);
		tag.putDouble(KEY_RANGE, this.range);
		tag.putBoolean(KEY_SPAWN, this.riftAggregatorSetSpawn);
		return tag;
	}

	public void loadFromTag(CompoundTag tag) {
		this.legacyMode = tag.getBoolean(KEY_LEGACY);
		this.expireSpan = tag.getInt(KEY_EXPIRE);
		this.oneTimeUse = tag.getBoolean(KEY_ONE_TIME);
		this.expCost = tag.getDouble(KEY_EXP_COST);
		this.range = tag.getDouble(KEY_RANGE);
		this.riftAggregatorSetSpawn = tag.getBoolean(KEY_SPAWN);
	}

	public void generateDefaultConfig() {
		Path template = FabricLoader.getInstance().getGameDir().resolve(DEFAULT_CONFIGS_DIR).resolve(FILE_NAME);
		try {
			if (Files.exists(template)) return;
			Files.createDirectories(template.getParent());
			Files.writeString(template, this.genString(), StandardCharsets.UTF_8);
		} catch (Exception ignored) {
			//Didn't ask
		}
	}

	private void load(JsonObject json) {
		this.legacyMode = readBoolean(json, KEY_LEGACY, false);
		this.expireSpan = readInt(json, KEY_EXPIRE, AnimUtil.minutesToTicks(5));
		this.oneTimeUse = readBoolean(json, KEY_ONE_TIME, true);
		this.expCost = readDouble(json, KEY_EXP_COST, -1.0D);
		this.range = Mth.clamp(readDouble(json, KEY_RANGE, 2.5D), MIN_RANGE, MAX_RANGE);
		this.riftAggregatorSetSpawn = readBoolean(json, KEY_SPAWN, true);
	}

	private String genString() {
		return """
				{
				  // Should the mod behave like it did in mod version 1.0?
				  // With this, instead of being items, rifts appear as entities when tagged bosses are slain.
				  "%s": %b,
				
				  // How long should a Rift remain charged for? (20 ticks is 1 second)
				  // Alternatively, a negative value makes it never expire.
				  "%s": %d,
				
				  // Should a Rift be one time use?
				  // The item will not leave your inventory upon use. On legacy mode, the Rift entity will remain there after its use, until it expires.
				  "%s": %b,
				
				  // Should activating a Rift Pearl cost exp instead of killing a boss?
				  // If legacy behavior is enabled, a rift will both require a killed boss and an exp cost to activate.
				  // A negative value disables this. A positive number indicates the level cost per chunk. 0 makes rifts free to use.
				  "%s": %s,
				
				  // When placed as an entity, or when playing on legacy mode, how big of a range should a Rift have?
				  // Allowed values: %s to %s
				  "%s": %s,
				
				  // Should the Rift Aggregators be usable as spawn points?
				  // With this setting off, they still have a use as locations you can link a Rift Pearl to.
				  "%s": %b
				}
				""".formatted(
				KEY_LEGACY, this.legacyMode,
				KEY_EXPIRE, this.expireSpan,
				KEY_ONE_TIME, this.oneTimeUse,
				KEY_EXP_COST, String.valueOf(this.expCost),
				String.valueOf(MIN_RANGE), String.valueOf(MAX_RANGE),
				KEY_RANGE, String.valueOf(this.range),
				KEY_SPAWN, this.riftAggregatorSetSpawn
		);
	}

	private static boolean readBoolean(JsonObject json, String key, boolean or) {
		if (json.get(key) instanceof JsonPrimitive primitive && primitive.isBoolean()) return primitive.getAsBoolean();
		return or;
	}

	private static int readInt(JsonObject json, String key, int or) {
		if (json.get(key) instanceof JsonPrimitive primitive && primitive.isNumber()) return primitive.getAsInt();
		return or;
	}

	private static double readDouble(JsonObject json, String key, double or) {
		if (json.get(key) instanceof JsonPrimitive primitive && primitive.isNumber()) return primitive.getAsDouble();
		return or;
	}

	@Override
	public boolean isV1() {
		return this.legacyMode;
	}

	@Override
	public boolean isExpired(long time) {
		if (this.expireSpan < 0) return false;
		return this.empowerDuration() < time;
	}

	@Override
	public int empowerDuration() {
		return Math.max(this.expireSpan, 0) + iRift.TRANSITION_TIME;
	}

	@Override
	public boolean isOneTimeUse() {
		return this.oneTimeUse;
	}

	@Override
	public double getExpCost() {
		return this.expCost;
	}

	@Override
	public double getRange() {
		return this.range;
	}

	@Override
	public boolean canSetSpawn() {
		return this.riftAggregatorSetSpawn;
	}
}