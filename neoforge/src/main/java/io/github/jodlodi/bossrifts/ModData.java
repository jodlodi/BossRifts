package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.client.V2ModelProperty;
import io.github.jodlodi.bossrifts.client.rift.AnimatedItemModel;
import io.github.jodlodi.bossrifts.client.rift.RiftItemRenderer;
import io.github.jodlodi.bossrifts.client.rift.iRiftRenderer;
import io.github.jodlodi.bossrifts.entities.BossRiftEntity;
import io.github.jodlodi.bossrifts.items.RiftAggregatorItem;
import io.github.jodlodi.bossrifts.items.RiftPearlItem;
import io.github.jodlodi.bossrifts.platform.NeoForgeRegistryHelper;
import io.github.jodlodi.bossrifts.registry.*;
import io.github.jodlodi.bossrifts.util.MultiSupplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
@EventBusSubscriber(modid = Constants.MOD_ID)
public class ModData {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getReloadableLookupProvider();

		generator.addProvider(true, new ModModelGen(output));
		generator.addProvider(true, ModRegistries.forReloadableLayer(output, event.getWorldLookupProvider(), lookupProvider));
		generator.addProvider(true, new ModSoundDefinitions(output));
		generator.addProvider(true, new ModEntityTags(output, lookupProvider));
		generator.addProvider(true, new ModLang(output));
	}

	private static class ModRegistries {
		public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder().add(new MultiRegistryBootstrap() {
			@Override
			public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
				return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
			}

			@Override
			public void run(MultiRegistryBootstrap.BootstrapGetter registries) {
				new ModCrafting(registries.get(Registries.RECIPE), registries.get(Registries.ADVANCEMENT)).buildRecipes();
			}
		});

		public static DatapackBuiltinEntriesProvider forReloadableLayer(PackOutput output, CompletableFuture<HolderLookup.Provider> worldRegistries, CompletableFuture<HolderLookup.Provider> reloadableRegistries) {
			return new DatapackBuiltinEntriesProvider(
					output,
					Constants.MOD_ID + "_reloadable",
					RegistryDataLoader.RELOADABLE_REGISTRIES,
					RegistryPatchGenerator.createReloadableLookup(worldRegistries, reloadableRegistries, RELOADABLE_BUILDER),
					Set.of("minecraft", Constants.MOD_ID)
			);
		}
	}

	private static class ModCrafting extends VanillaRecipeProvider {

		public ModCrafting(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
			super(recipeOutput, advancementOutput);
		}

		@Override
		protected void buildRecipes() {
			this.shaped(RecipeCategory.MISC, ModItems.RIFT_PEARL.get(), 1)
					.pattern("#X#")
					.pattern("X0X")
					.pattern("#X#")
					.define('#', Ingredient.of(Items.END_STONE))
					.define('X', Ingredient.of(Items.GLOWSTONE_DUST))
					.define('0', Ingredient.of(Items.ENDER_EYE))
					.unlockedBy("has_item", has(Items.ENDER_EYE))
					.save(output);

			this.shaped(RecipeCategory.MISC, ModItems.RIFT_AGGREGATOR.get(), 1)
					.pattern("#X#")
					.pattern("#0#")
					.pattern("#H#")
					.define('#', Ingredient.of(Items.END_STONE))
					.define('0', Ingredient.of(ModItems.RIFT_PEARL.get()))
					.define('X', Ingredient.of(Items.RESPAWN_ANCHOR))
					.define('H', Ingredient.of(Items.LODESTONE))
					.unlockedBy("has_item", has(ModItems.RIFT_PEARL.get()))
					.save(output);
		}
	}

	private static class ModModelGen extends ModelProvider {
		private final PackOutput.PathProvider blocks;
		private final PackOutput.PathProvider items;
		private final PackOutput.PathProvider models;

		public ModModelGen(PackOutput output) {
			super(output, Constants.MOD_ID);
			this.blocks = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
			this.items = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
			this.models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		}

		@Override
		public CompletableFuture<?> run(CachedOutput output) {
			ItemInfoCollector itemModelOutput = new ItemInfoCollector(this::getKnownItems);
			BlockStateGeneratorCollector blockModelOutput = new BlockStateGeneratorCollector(this::getKnownBlocks);
			SimpleModelCollector modelOutput = new SimpleModelCollector();
			this.registerModels(new ModBlockStates(blockModelOutput, itemModelOutput, modelOutput), new ModItemModels(itemModelOutput, modelOutput));
			blockModelOutput.validate();
			itemModelOutput.finalizeAndValidate();
			return CompletableFuture.allOf(blockModelOutput.save(output, this.blocks), modelOutput.save(output, this.models), itemModelOutput.save(output, this.items));
		}

		@Override
		protected Stream<? extends Holder<Block>> getKnownBlocks() {
			return NeoForgeRegistryHelper.BLOCKS.getEntries().stream();
		}

		@Override
		protected Stream<? extends Holder<Item>> getKnownItems() {
			return NeoForgeRegistryHelper.ITEMS.getEntries().stream();
		}

		private static class ModBlockStates extends BlockModelGenerators {
			public static final ExtendedModelTemplate RIFT_AGGREGATOR = ExtendedModelTemplateBuilder.builder()
					.parent(Identifier.withDefaultNamespace("block/block"))
					.requiredTextureSlot(TextureSlot.PARTICLE)
					.requiredTextureSlot(TextureSlot.SIDE)
					.requiredTextureSlot(TextureSlot.BOTTOM)
					.requiredTextureSlot(TextureSlot.TOP)
					.requiredTextureSlot(TextureSlot.INSIDE)
					.element(elementBuilder -> elementBuilder
							.from(0.0F, 0.0F, 0.0F).to(16.0F, 14.0F, 16.0F)
							.face(Direction.UP, faceBuilder -> faceBuilder.texture(TextureSlot.INSIDE))
							.face(Direction.DOWN, faceBuilder -> faceBuilder.texture(TextureSlot.BOTTOM).cullface(Direction.DOWN))
							.face(Direction.NORTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE).cullface(Direction.NORTH))
							.face(Direction.SOUTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE).cullface(Direction.SOUTH))
							.face(Direction.WEST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE).cullface(Direction.WEST))
							.face(Direction.EAST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE).cullface(Direction.EAST))
					).element(elementBuilder -> elementBuilder
							.from(0.0F, 14.0F, 0.0F).to(16.0F, 16.0F, 2.0F)
							.face(Direction.UP, faceBuilder -> faceBuilder.texture(TextureSlot.TOP).cullface(Direction.UP))
							.face(Direction.NORTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.SOUTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.WEST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.EAST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
					).element(elementBuilder -> elementBuilder
							.from(0.0F, 14.0F, 14.0F).to(16.0F, 16.0F, 16.0F)
							.face(Direction.UP, faceBuilder -> faceBuilder.texture(TextureSlot.TOP).cullface(Direction.UP))
							.face(Direction.NORTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.SOUTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.WEST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.EAST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
					).element(elementBuilder -> elementBuilder
							.from(0.0F, 14.0F, 2.0F).to(2.0F, 16.0F, 14.0F)
							.face(Direction.UP, faceBuilder -> faceBuilder.texture(TextureSlot.TOP).cullface(Direction.UP))
							.face(Direction.NORTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.SOUTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.WEST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.EAST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
					).element(elementBuilder -> elementBuilder
							.from(14.0F, 14.0F, 2.0F).to(16.0F, 16.0F, 14.0F)
							.face(Direction.UP, faceBuilder -> faceBuilder.texture(TextureSlot.TOP).cullface(Direction.UP))
							.face(Direction.NORTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.SOUTH, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.WEST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
							.face(Direction.EAST, faceBuilder -> faceBuilder.texture(TextureSlot.SIDE))
					).element(elementBuilder -> elementBuilder
							.from(2.0F, 16.0F, 2.0F).to(14.0F, 16.0F, 14.0F)
							.face(Direction.UP, faceBuilder -> faceBuilder.texture(TextureSlot.TOP).cullface(Direction.UP))
					).build();

			public ModBlockStates(Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
				super(blockStateOutput, itemModelOutput, modelOutput);
			}

			@Override
			public void run() {
				TextureMapping mapping = TextureMapping.cube(ModBlocks.RIFT_AGGREGATOR.get())
						.put(TextureSlot.PARTICLE, new Material(Constants.prefix("block/rift_aggregator_side0")))
						.put(TextureSlot.SIDE, new Material(Constants.prefix("block/rift_aggregator_side0")))
						.put(TextureSlot.BOTTOM, new Material(Constants.prefix("block/rift_aggregator_bottom")))
						.put(TextureSlot.TOP, new Material(Constants.prefix("block/rift_aggregator_top_off")))
						.put(TextureSlot.INSIDE, new Material(Constants.prefix("block/rift_aggregator_top")));

				Identifier aggregator = RIFT_AGGREGATOR.create(Constants.prefix("block/" + ModBlocks.RIFT_AGGREGATOR.id().getPath()), mapping, this.modelOutput);
				this.itemModelOutput.accept(ModItems.RIFT_AGGREGATOR.get(), ItemModelUtils.plainModel(aggregator));
				this.blockStateOutput.accept(createSimpleBlock(ModBlocks.RIFT_AGGREGATOR.get(), variant(plainModel(aggregator))));
			}
		}

		private static class ModItemModels extends ItemModelGenerators {

			public ModItemModels(ItemModelOutput output, BiConsumer<Identifier, ModelInstance> modelOutput) {
				super(output, modelOutput);
			}

			@Override
			public void run() {
				this.itemModelOutput.accept(ModItems.RIFT_PEARL.get(), ItemModelUtils.select(
						new V2ModelProperty(),
						ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(ModItems.RIFT_PEARL.get()).withSuffix("_flat"), TextureMapping.layer0(ModItems.RIFT_PEARL.get()), this.modelOutput)),
						ItemModelUtils.when(
								true,
								ItemModelUtils.select(
										new DisplayContext(),
										this.pearlModel(ItemDisplayContext.NONE, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM),
										List.of(
												this.pearlWhen(
														ItemDisplayContext.GUI,
														new ItemTransform(
																new Vector3f(30.0F, 225.0F, 0.0F),
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.625F)
														),
														new ItemTransform(
																new Vector3f(30.0F, 225.0F, 0.0F),
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.625F)
														)
												),
												this.pearlWhen(
														ItemDisplayContext.GROUND,
														new ItemTransform(
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.0F, 3.0F, 0.0F),
																new Vector3f(0.25F)
														),
														new ItemTransform(
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.0F, 3.0F, 0.0F),
																new Vector3f(0.15F)
														)
												),
												this.pearlWhen(
														ItemDisplayContext.FIXED,
														new ItemTransform(
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.75F)
														),
														new ItemTransform(
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.0F, 0.0F, 0.0F),
																new Vector3f(0.75F)
														)
												),
												this.pearlWhen(
														ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
														new ItemTransform(
																new Vector3f(75.0F, 45.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.375F)
														),
														new ItemTransform(
																new Vector3f(75.0F, 45.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.33F)
														)
												),
												this.pearlWhen(
														ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
														new ItemTransform(
																new Vector3f(75.0F, 225.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.375F)
														),
														new ItemTransform(
																new Vector3f(75.0F, 225.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.33F)
														)
												),
												this.pearlWhen(
														ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
														new ItemTransform(
																new Vector3f(0.0F, 45.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.40F)
														),
														new ItemTransform(
																new Vector3f(0.0F, 45.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.33F)
														)
												),
												this.pearlWhen(
														ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
														new ItemTransform(
																new Vector3f(0.0F, 225.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.40F)
														),
														new ItemTransform(
																new Vector3f(0.0F, 225.0F, 0.0F),
																new Vector3f(0.0F, 2.5F, 0.0F),
																new Vector3f(0.33F)
														)
												)
										)
								)
						)
				));

				ModelTemplates.FLAT_ITEM.create(iRiftRenderer.PEARL_ITEM_MODEL, TextureMapping.layer0(new Material(iRiftRenderer.PEARL_ITEM_MODEL)), this.modelOutput);
			}

			private SelectItemModel.SwitchCase<ItemDisplayContext> pearlWhen(ItemDisplayContext context, ItemTransform shellTransform, ItemTransform eyeTransform) {
				return ItemModelUtils.when(context, this.pearlModel(context, shellTransform, eyeTransform));
			}

			private ItemModel.Unbaked pearlModel(ItemDisplayContext context, ItemTransform shellTransform, ItemTransform eyeTransform) {
				ItemModel.Unbaked model = ItemModelUtils.specialModel(
						ModelLocationUtils.getModelLocation(ModItems.RIFT_PEARL.get()).withSuffix("_" + context.getSerializedName()),
						new RiftItemRenderer.Unbaked(context, shellTransform, eyeTransform)
				);
				return context == ItemDisplayContext.GUI ? new AnimatedItemModel(model) : model;
			}
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

			this.add(ModItems.RIFT_AGGREGATOR.get(), "Rift Aggregator");
			this.add(ModBlocks.RIFT_AGGREGATOR.get(), "Rift Aggregator");
		}
	}

	private static class ModEntityTags extends EntityTypeTagsProvider {

		public ModEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Constants.MOD_ID);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			this.getOrCreateRawBuilder(ModTags.RIFT_BOSSES)
					.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityTypes.WITHER))
					.addElement(BuiltInRegistries.ENTITY_TYPE.getKey(EntityTypes.ENDER_DRAGON))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "lich"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "lich"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "yeti"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "snow_queen"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "minoshroom"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "hydra"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "knight_phantom"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("twilightforest", "ur_ghast"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("atum", "pharaoh"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("mowziesmobs", "barako"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("mowziesmobs", "ferrous_wroughtnaut"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("mowziesmobs", "frostmaw"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "baroness"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "clunkhead"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "craexxeus"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "crystocore"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "creep"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "dracyon"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "elusive"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "graw"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "hive_king"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "kror"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "mechbot"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "okazor"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "shadowlord"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "skeletron"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "vinocorne"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "voxxulon"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "flash"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "red_guardian"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "green_guardian"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "blue_guardian"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("aoa3", "yellow_guardian"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("gaiadimension", "malachite_guard"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("blue_skies", "alchemist"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("blue_skies", "arachnarch"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("blue_skies", "starlit_crusher"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("blue_skies", "summoner"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("stalwart_dungeons", "awful_ghast"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("stalwart_dungeons", "nether_keeper"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("stalwart_dungeons", "shelterer_without_armor"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("dungeonsmod", "king"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("dungeonsmod", "deserted"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("dungeonsmod", "crawler"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("dungeonsmod", "ironslime"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("dungeonsmod", "kraken"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("dungeonsmod", "voidmaster"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("forestcraft", "beequeen"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("forestcraft", "iguana_king"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("cataclysm", "ender_golem"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("cataclysm", "ender_guardian"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("cataclysm", "ignis"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("cataclysm", "ignited_revenant"))
					.addOptionalElement(Identifier.fromNamespaceAndPath("cataclysm", "netherite_monstrosity"))
					.addOptionalTag(Tags.EntityTypes.BOSSES.location());
		}
	}

	private static class ModSoundDefinitions extends SoundDefinitionsProvider {

		protected ModSoundDefinitions(PackOutput output) {
			super(output, Constants.MOD_ID);
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

			this.generateWithExistingSound(ModSounds.RIFT_PEARL_LOCK, SoundEvents.LODESTONE_COMPASS_LOCK, "Rift Pearl links");
		}

		public void generateWithExistingSound(MultiSupplier<SoundEvent> event, SoundEvent referencedSound, String subtitle) {
			SoundDefinition definition = SoundDefinition.definition()
					.with(SoundDefinition.Sound.sound(referencedSound.location(), SoundDefinition.SoundType.EVENT));

			this.createSubtitleAndLangEntry(event, definition, subtitle);
			this.add(event.get(), definition);
		}

		public void generateNewSoundMC(MultiSupplier<SoundEvent> event, String subtitle, float volume, float pitch, String... baseSoundDirectory) {
			SoundDefinition definition = SoundDefinition.definition();
			for (String string : baseSoundDirectory) {
				definition.with(
						SoundDefinition.Sound
								.sound(Identifier.withDefaultNamespace(string), SoundDefinition.SoundType.SOUND)
								.volume(volume)
								.pitch(pitch)
				);
			}
			this.createSubtitleAndLangEntry(event, definition, subtitle);
			this.add(event.get(), definition);
		}

		private void createSubtitleAndLangEntry(MultiSupplier<SoundEvent> event, SoundDefinition definition, String subtitle) {
			String[] splitSoundName = Objects.requireNonNull(event.id()).getPath().split("\\.", 2);
			String subtitleKey = "subtitles." + Constants.MOD_ID + "." + splitSoundName[0] + "." + splitSoundName[1];
			definition.subtitle(subtitleKey);
			ModLang.AUTO_GENERATOR.put(subtitleKey, subtitle);
		}
	}
}
