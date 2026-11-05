package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import io.github.jodlodi.bossrifts.registry.ModSounds;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModData {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		generator.addProvider(new ModBlockStates(generator, helper));
		generator.addProvider(new ModItemModels(generator, helper));
		generator.addProvider(new ModCrafting(generator));
		generator.addProvider(new ModSoundDefinitions(generator, helper));
		generator.addProvider(new ModEntityTags(generator, helper));
		generator.addProvider(new ModLang(generator));
	}

	private static class ModCrafting extends RecipeProvider {
		public ModCrafting(DataGenerator generator) {
			super(generator);
		}

		@Override
		protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
			ShapedRecipeBuilder.shaped(ModItems.RIFT_PEARL.get(), 1)
					.pattern("#X#")
					.pattern("X0X")
					.pattern("#X#")
					.define('#', Ingredient.of(Items.END_STONE))
					.define('X', Ingredient.of(Items.GLOWSTONE_DUST))
					.define('0', Ingredient.of(Items.ENDER_EYE))
					.unlockedBy("has_item", has(Items.ENDER_EYE))
					.save(consumer);

			ShapedRecipeBuilder.shaped(ModItems.RIFT_AGGREGATOR.get(), 1)
					.pattern("#X#")
					.pattern("#0#")
					.pattern("#H#")
					.define('#', Ingredient.of(Items.END_STONE))
					.define('0', Ingredient.of(ModItems.RIFT_PEARL.get()))
					.define('X', Ingredient.of(Items.RESPAWN_ANCHOR))
					.define('H', Ingredient.of(Items.LODESTONE))
					.unlockedBy("has_item", has(ModItems.RIFT_PEARL.get()))
					.save(consumer);
		}
	}

	private static class ModBlockStates extends BlockStateProvider {
		public ModBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
			super(gen, Constants.MOD_ID, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels() {
			this.simpleBlock(ModBlocks.RIFT_AGGREGATOR.get(), this.models().withExistingParent(ModBlocks.RIFT_AGGREGATOR.id().getPath(), "block/block")
					.texture("top", Constants.prefix("block/rift_aggregator_top_off"))
					.texture("inner", Constants.prefix("block/rift_aggregator_top"))
					.texture("bottom", Constants.prefix("block/rift_aggregator_bottom"))
					.texture("side", Constants.prefix("block/rift_aggregator_side0"))
					.texture("particle", Constants.prefix("block/rift_aggregator_side0"))

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
			super(generator, Constants.MOD_ID, existingFileHelper);
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

		private <T extends Item> ItemModelBuilder singleTex(MultiSupplier<T> item) {
			return generated(item.id().getPath(), Constants.prefix("item/" + item.id().getPath()));
		}

		private <T extends Block> void toBlock(MultiSupplier<T> block) {
			toBlockModel(block.get(), Objects.requireNonNull(block.get().getRegistryName()).getPath());
		}

		private void toBlockModel(Block block, String model) {
			toBlockModel(block, Constants.prefix("block/" + model));
		}

		private void toBlockModel(Block block, ResourceLocation model) {
			withExistingParent(Objects.requireNonNull(block.getRegistryName()).getPath(), model);
		}
	}

	private static class ModLang extends LanguageProvider {
		public static final Map<String, String> AUTO_GENERATOR = new HashMap<>();

		public ModLang(DataGenerator generator) {
			super(generator, Constants.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			AUTO_GENERATOR.forEach(this::add);
			this.add(ModEntities.BOSS_RIFT.get(), "Boss Rift");

			this.add(BossRiftEntity.COST, "§7EXP cost: [%s§7]");
			this.add(RiftAggregatorItem.TOOLTIP, "§7Can be used as a spawn point.");
			this.add(RiftPearlItem.TOOLTIP_UNLINKED, "§7Use on a Rift Aggregator to link.");
			this.add(RiftPearlItem.TOOLTIP_V1_LINKED, "§7Use on a Rift to set destination to link.");
			this.add(RiftPearlItem.TOOLTIP_MODERN_1, "§7Hold-use to warp.");
			this.add(RiftPearlItem.TOOLTIP_MODERN_2, "§7Sneak-use on block to place Rift.");

			this.add(ModItems.RIFT_PEARL.get(), "Rift Pearl");
			this.add(ModItems.RIFT_PEARL.get().getDescriptionId() + ".aggregator", "Linked Rift Pearl");

			this.add(ModBlocks.RIFT_AGGREGATOR.get(), "Rift Aggregator");
		}
	}

	private static class ModEntityTags extends EntityTypeTagsProvider {

		public ModEntityTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
			super(generator, Constants.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags() {
			this.tag(BossRifts.LEGACY_BOSSES)
					.addOptional(new ResourceLocation("twilightforest", "lich"))
					.addOptional(new ResourceLocation("twilightforest", "lich"))
					.addOptional(new ResourceLocation("twilightforest", "yeti"))
					.addOptional(new ResourceLocation("twilightforest", "snow_queen"))
					.addOptional(new ResourceLocation("twilightforest", "minoshroom"))
					.addOptional(new ResourceLocation("twilightforest", "hydra"))
					.addOptional(new ResourceLocation("twilightforest", "knight_phantom"))
					.addOptional(new ResourceLocation("twilightforest", "ur_ghast"))
					.addOptional(new ResourceLocation("atum", "pharaoh"))
					.addOptional(new ResourceLocation("mowziesmobs", "barako"))
					.addOptional(new ResourceLocation("mowziesmobs", "ferrous_wroughtnaut"))
					.addOptional(new ResourceLocation("mowziesmobs", "frostmaw"))
					.addOptional(new ResourceLocation("aoa3", "baroness"))
					.addOptional(new ResourceLocation("aoa3", "clunkhead"))
					.addOptional(new ResourceLocation("aoa3", "craexxeus"))
					.addOptional(new ResourceLocation("aoa3", "crystocore"))
					.addOptional(new ResourceLocation("aoa3", "creep"))
					.addOptional(new ResourceLocation("aoa3", "dracyon"))
					.addOptional(new ResourceLocation("aoa3", "elusive"))
					.addOptional(new ResourceLocation("aoa3", "graw"))
					.addOptional(new ResourceLocation("aoa3", "hive_king"))
					.addOptional(new ResourceLocation("aoa3", "kror"))
					.addOptional(new ResourceLocation("aoa3", "mechbot"))
					.addOptional(new ResourceLocation("aoa3", "okazor"))
					.addOptional(new ResourceLocation("aoa3", "shadowlord"))
					.addOptional(new ResourceLocation("aoa3", "skeletron"))
					.addOptional(new ResourceLocation("aoa3", "vinocorne"))
					.addOptional(new ResourceLocation("aoa3", "voxxulon"))
					.addOptional(new ResourceLocation("aoa3", "flash"))
					.addOptional(new ResourceLocation("aoa3", "red_guardian"))
					.addOptional(new ResourceLocation("aoa3", "green_guardian"))
					.addOptional(new ResourceLocation("aoa3", "blue_guardian"))
					.addOptional(new ResourceLocation("aoa3", "yellow_guardian"))
					.addOptional(new ResourceLocation("gaiadimension", "malachite_guard"))
					.addOptional(new ResourceLocation("blue_skies", "alchemist"))
					.addOptional(new ResourceLocation("blue_skies", "arachnarch"))
					.addOptional(new ResourceLocation("blue_skies", "starlit_crusher"))
					.addOptional(new ResourceLocation("blue_skies", "summoner"))
					.addOptional(new ResourceLocation("stalwart_dungeons", "awful_ghast"))
					.addOptional(new ResourceLocation("stalwart_dungeons", "nether_keeper"))
					.addOptional(new ResourceLocation("stalwart_dungeons", "shelterer_without_armor"))
					.addOptional(new ResourceLocation("dungeonsmod", "king"))
					.addOptional(new ResourceLocation("dungeonsmod", "deserted"))
					.addOptional(new ResourceLocation("dungeonsmod", "crawler"))
					.addOptional(new ResourceLocation("dungeonsmod", "ironslime"))
					.addOptional(new ResourceLocation("dungeonsmod", "kraken"))
					.addOptional(new ResourceLocation("dungeonsmod", "voidmaster"))
					.addOptional(new ResourceLocation("forestcraft", "beequeen"))
					.addOptional(new ResourceLocation("forestcraft", "iguana_king"))
					.addOptional(new ResourceLocation("cataclysm", "ender_golem"))
					.addOptional(new ResourceLocation("cataclysm", "ender_guardian"))
					.addOptional(new ResourceLocation("cataclysm", "ignis"))
					.addOptional(new ResourceLocation("cataclysm", "ignited_revenant"))
					.addOptional(new ResourceLocation("cataclysm", "netherite_monstrosity"));

			this.tag(BossRifts.RIFT_BOSSES).add(
					EntityType.WITHER,
					EntityType.ENDER_DRAGON
			).addTag(BossRifts.LEGACY_BOSSES);
		}
	}

	private static class ModSoundDefinitions extends SoundDefinitionsProvider {

		protected ModSoundDefinitions(DataGenerator generator, ExistingFileHelper helper) {
			super(generator, Constants.MOD_ID, helper);
		}

		@Override
		public void registerSounds() {
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

			this.generateWithExistingSound(ModSounds.RIFT_AGGREGATOR_AMBIENT, SoundEvents.RESPAWN_ANCHOR_AMBIENT, "Rift Aggregator whooshes");
			this.generateWithExistingSound(ModSounds.RIFT_AGGREGATOR_SET_SPAWN, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, "Rift Aggregator sets spawn");
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
			String subtitleKey = "subtitles." + Constants.MOD_ID + "." + splitSoundName[0] + "." + splitSoundName[1];
			definition.subtitle(subtitleKey);
			ModLang.AUTO_GENERATOR.put(subtitleKey, subtitle);
		}
	}
}
