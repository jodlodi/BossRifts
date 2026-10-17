package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModData {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		generator.addProvider(new ModBlockStates(generator, helper));
		generator.addProvider(new ModItemModels(generator, helper));
		generator.addProvider(new ModCrafting(generator));
		generator.addProvider(new ModSoundDefinitions(generator, helper));
		generator.addProvider(new ModLang(generator));
	}

	private static class ModCrafting extends RecipeProvider {
		public ModCrafting(DataGenerator generator) {
			super(generator);
		}

		@Override
		protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
			ShapedRecipeBuilder.shaped(ModItems.RIFT_PEARL.get(), 1)
					.pattern("#∴#")
					.pattern("∴0∴")
					.pattern("#∴#")
					.define('#', Ingredient.of(Tags.Items.END_STONES))
					.define('∴', Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
					.define('0', Ingredient.of(Items.ENDER_EYE))
					.unlockedBy("has_item", has(Items.ENDER_EYE))
					.save(consumer);

			ShapedRecipeBuilder.shaped(ModItems.RIFT_AGGREGATOR.get(), 1)
					.pattern("#X#")
					.pattern("#0#")
					.pattern("#H#")
					.define('#', Ingredient.of(Tags.Items.END_STONES))
					.define('0', Ingredient.of(ModItems.RIFT_PEARL.get()))
					.define('X', Ingredient.of(Items.RESPAWN_ANCHOR))
					.define('H', Ingredient.of(Items.LODESTONE))
					.unlockedBy("has_item", has(ModItems.RIFT_PEARL.get()))
					.save(consumer);
		}
	}

	private static class ModBlockStates extends BlockStateProvider {
		public ModBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
			super(gen, BossRifts.MOD_ID, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels() {
			this.simpleBlock(ModBlocks.RIFT_AGGREGATOR.get(), this.models().withExistingParent(ModBlocks.RIFT_AGGREGATOR.getId().getPath(), "block/block")

					.texture("top", BossRifts.prefix("block/rift_aggregator_top_off"))
					.texture("inner", BossRifts.prefix("block/rift_aggregator_top"))
					.texture("bottom", BossRifts.prefix("block/rift_aggregator_bottom"))
					.texture("side", BossRifts.prefix("block/rift_aggregator_side0"))
					.texture("particle", BossRifts.prefix("block/rift_aggregator_side0"))

					.element()
					.from(0.0F, 0.0F, 0.0F)
					.to(16.0F, 14.0F, 16.0F)
					.face(Direction.UP).texture("#inner").end()
					.face(Direction.DOWN).texture("#bottom").end()
					.face(Direction.NORTH).texture("#side").end()
					.face(Direction.EAST).texture("#side").end()
					.face(Direction.SOUTH).texture("#side").end()
					.face(Direction.WEST).texture("#side").end()
					.end()

					.element()
					.from(0.0F, 14.0F, 0.0F)
					.to(16.0F, 16.0F, 2.0F)
					.face(Direction.UP).texture("#top").end()
					.face(Direction.NORTH).texture("#side").end()
					.face(Direction.EAST).texture("#side").end()
					.face(Direction.SOUTH).texture("#side").end()
					.face(Direction.WEST).texture("#side").end()
					.end()

					.element()
					.from(0.0F, 14.0F, 14.0F)
					.to(16.0F, 16.0F, 16.0F)
					.face(Direction.UP).texture("#top").end()
					.face(Direction.NORTH).texture("#side").end()
					.face(Direction.EAST).texture("#side").end()
					.face(Direction.SOUTH).texture("#side").end()
					.face(Direction.WEST).texture("#side").end()
					.end()

					.element()
					.from(0.0F, 14.0F, 2.0F)
					.to(2.0F, 16.0F, 14.0F)
					.face(Direction.UP).texture("#top").end()
					.face(Direction.NORTH).texture("#side").end()
					.face(Direction.EAST).texture("#side").end()
					.face(Direction.SOUTH).texture("#side").end()
					.face(Direction.WEST).texture("#side").end()
					.end()

					.element()
					.from(14.0F, 14.0F, 2.0F)
					.to(16.0F, 16.0F, 14.0F)
					.face(Direction.UP).texture("#top").end()
					.face(Direction.NORTH).texture("#side").end()
					.face(Direction.EAST).texture("#side").end()
					.face(Direction.SOUTH).texture("#side").end()
					.face(Direction.WEST).texture("#side").end()
					.end()

					.element()
					.from(2.0F, 16.0F, 2.0F)
					.to(14.0F, 16.0F, 14.0F)
					.face(Direction.UP).texture("#top").end()
					.end()
			);
		}
	}

	private static class ModItemModels extends ItemModelProvider {

		public ModItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
			super(generator, BossRifts.MOD_ID, existingFileHelper);
		}

		@Override
		protected void registerModels() {
			this.singleTex(ModItems.RIFT_PEARL);
			this.toBlock(ModBlocks.RIFT_AGGREGATOR);
		}

		private ItemModelBuilder generated(String name, ResourceLocation... layers) {
			ItemModelBuilder builder = withExistingParent(name, "item/generated");
			for (int i = 0; i < layers.length; i++) {
				builder = builder.texture("layer" + i, layers[i]);
			}
			return builder;
		}

		private <T extends Item> ItemModelBuilder singleTex(RegistryObject<T> item) {
			return generated(item.getId().getPath(), BossRifts.prefix("item/" + item.getId().getPath()));
		}

		private <T extends Block> void toBlock(RegistryObject<T> block) {
			toBlockModel(block.get(), Objects.requireNonNull(block.get().getRegistryName()).getPath());
		}

		private void toBlockModel(Block block, String model) {
			toBlockModel(block, BossRifts.prefix("block/" + model));
		}

		private void toBlockModel(Block block, ResourceLocation model) {
			withExistingParent(Objects.requireNonNull(block.getRegistryName()).getPath(), model);
		}
	}

	private static class ModLang extends LanguageProvider {
		public static final Map<String, String> AUTO_GENERATOR = new HashMap<>();

		public ModLang(DataGenerator generator) {
			super(generator, BossRifts.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			AUTO_GENERATOR.forEach(this::add);
			this.add(ModEntities.BOSS_RIFT.get(), "Boss Rift");

			this.add(ModItems.RIFT_PEARL.get(), "Rift Pearl");
			this.add(ModItems.RIFT_PEARL.get().getDescriptionId() + ".aggregator", "Linked Rift Pearl");

			this.add(ModBlocks.RIFT_AGGREGATOR.get(), "Rift Aggregator");
		}
	}

	private static class ModSoundDefinitions extends SoundDefinitionsProvider {

		protected ModSoundDefinitions(DataGenerator generator, ExistingFileHelper helper) {
			super(generator, BossRifts.MOD_ID, helper);
		}

		@Override
		public void registerSounds() {
			//this.generateWithExistingSound(ModSounds.RIFT_EXPIRE, SoundEvents.LODESTONE_COMPASS_LOCK, "Rift expires");

			this.generateNewSoundMC(
					ModSounds.RIFT_SPAWN,
					"Rift spawns",
					1.0F, 1.0F,
					"block/respawn_anchor/set_spawn1", "block/respawn_anchor/set_spawn2", "block/respawn_anchor/set_spawn3"
			);
			this.generateNewSoundMC(
					ModSounds.RIFT_OPEN,
					"Rift opens",
					1.0F, 1.0F,
					"mob/evocation_illager/cast1", "mob/evocation_illager/cast2"
			);
			this.generateNewSoundMC(
					ModSounds.RIFT_CLOSE,
					"Rift closes",
					1.0F, 1.0F,
					"block/beacon/deactivate"
			);
			this.generateNewSoundMC(
					ModSounds.RIFT_REV_UP,
					"Rift is revving",
					1.0F, 1.0F,
					"mob/wither/shoot"
			);
			this.generateNewSoundMC(
					ModSounds.RIFT_WARP,
					"Rift warps",
					0.75F, 0.75F,
					"mob/illusion_illager/mirror_move1", "mob/illusion_illager/mirror_move2"
			);
			this.generateNewSoundMC(
					ModSounds.RIFT_EXPIRE,
					"Rift expires",
					1.0F, 1.0F,
					"mob/illusion_illager/prepare_mirror"
			);
		}

		public void generateWithExistingSound(Supplier<SoundEvent> event, SoundEvent referencedSound, String subtitle) {
			SoundDefinition definition = SoundDefinition.definition()
					.with(SoundDefinition.Sound.sound(referencedSound.getLocation(), SoundDefinition.SoundType.EVENT));

			this.createSubtitleAndLangEntry(event, definition, subtitle);
			this.add(event, definition);
		}

		public void generateNewSoundMC(Supplier<SoundEvent> event, String subtitle, float volume, float pitch, String... baseSoundDirectory) {
			SoundDefinition definition = SoundDefinition.definition();
			for (String string : baseSoundDirectory) {
				definition.with(
						SoundDefinition.Sound
								.sound(new ResourceLocation(string), SoundDefinition.SoundType.SOUND)
								.volume(volume)
								.pitch(pitch)
				);
			}
			this.createSubtitleAndLangEntry(event, definition, subtitle);
			this.add(event.get(), definition);
		}

		private void createSubtitleAndLangEntry(Supplier<SoundEvent> event, SoundDefinition definition, String subtitle) {
			String[] splitSoundName = Objects.requireNonNull(event.get().getLocation()).getPath().split("\\.", 2);
			String subtitleKey = "subtitles." + BossRifts.MOD_ID + "." + splitSoundName[0] + "." + splitSoundName[1];
			definition.subtitle(subtitleKey);
			ModLang.AUTO_GENERATOR.put(subtitleKey, subtitle);
		}
	}
}
