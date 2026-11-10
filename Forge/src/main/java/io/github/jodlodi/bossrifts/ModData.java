package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.registry.*;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
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
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NullMarked
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModData {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new ModBlockStates(output, helper));
		generator.addProvider(true, new ModItemModels(output, helper));
		generator.addProvider(true, new ModCrafting(output));
		generator.addProvider(true, new ModSoundDefinitions(output, helper));
		generator.addProvider(true, new ModEntityTags(output, lookupProvider, helper));
		generator.addProvider(true, new ModLang(output));
	}

	private static class ModCrafting extends RecipeProvider {

		public ModCrafting(PackOutput output) {
			super(output);
		}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_PEARL.get(), 1)
					.pattern("#X#")
					.pattern("X0X")
					.pattern("#X#")
					.define('#', Ingredient.of(Items.END_STONE))
					.define('X', Ingredient.of(Items.GLOWSTONE_DUST))
					.define('0', Ingredient.of(Items.ENDER_EYE))
					.unlockedBy("has_item", has(Items.ENDER_EYE))
					.save(consumer);

			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_AGGREGATOR.get(), 1)
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
		public ModBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
			super(output, Constants.MOD_ID, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels() {
			this.simpleBlock(ModBlocks.RIFT_AGGREGATOR.get(), this.models().withExistingParent(ModBlocks.RIFT_AGGREGATOR.id().getPath(), "block/block")
					.texture("top", Constants.prefix("block/rift_aggregator_top_off"))
					.texture("inner", Constants.prefix("block/rift_aggregator_top"))
					.texture("bottom", Constants.prefix("block/rift_aggregator_bottom"))
					.texture("side", Constants.prefix("block/rift_aggregator_side0"))
					.texture("particle", Constants.prefix("block/rift_aggregator_side0"))
					.renderType(ResourceLocation.withDefaultNamespace("cutout"))

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

		public ModItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
			super(output, Constants.MOD_ID, existingFileHelper);
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
			withExistingParent(block.id().getPath(), block.id().withPrefix("block/"));
		}
	}

	private static class ModLang extends LanguageProvider {
		public static final Map<String, String> AUTO_GENERATOR = new HashMap<>();

		public ModLang(PackOutput output) {
			super(output, Constants.MOD_ID, "en_us");
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

		public ModEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
			super(output, lookupProvider, Constants.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			this.tag(ModTags.RIFT_BOSSES)
					.add(EntityType.WITHER)
					.add(EntityType.ENDER_DRAGON)
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "lich"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "lich"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "yeti"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "snow_queen"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "minoshroom"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "hydra"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "knight_phantom"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("twilightforest", "ur_ghast"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("atum", "pharaoh"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("mowziesmobs", "barako"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("mowziesmobs", "ferrous_wroughtnaut"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("mowziesmobs", "frostmaw"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "baroness"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "clunkhead"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "craexxeus"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "crystocore"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "creep"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "dracyon"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "elusive"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "graw"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "hive_king"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "kror"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "mechbot"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "okazor"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "shadowlord"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "skeletron"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "vinocorne"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "voxxulon"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "flash"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "red_guardian"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "green_guardian"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "blue_guardian"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("aoa3", "yellow_guardian"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("gaiadimension", "malachite_guard"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("blue_skies", "alchemist"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("blue_skies", "arachnarch"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("blue_skies", "starlit_crusher"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("blue_skies", "summoner"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "awful_ghast"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "nether_keeper"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "shelterer_without_armor"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("dungeonsmod", "king"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("dungeonsmod", "deserted"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("dungeonsmod", "crawler"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("dungeonsmod", "ironslime"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("dungeonsmod", "kraken"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("dungeonsmod", "voidmaster"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("forestcraft", "beequeen"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("forestcraft", "iguana_king"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("cataclysm", "ender_golem"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("cataclysm", "ender_guardian"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("cataclysm", "ignis"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("cataclysm", "ignited_revenant"))
					.addOptional(ResourceLocation.fromNamespaceAndPath("cataclysm", "netherite_monstrosity"))
					.addOptionalTag(Tags.EntityTypes.BOSSES.location());
		}
	}

	private static class ModSoundDefinitions extends SoundDefinitionsProvider {

		protected ModSoundDefinitions(PackOutput output, ExistingFileHelper helper) {
			super(output, Constants.MOD_ID, helper);
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
								.sound(ResourceLocation.withDefaultNamespace(string), SoundDefinition.SoundType.SOUND)
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
