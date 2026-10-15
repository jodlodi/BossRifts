package io.github.jodlodi.bossrifts;

import io.github.jodlodi.bossrifts.registry.ModBlocks;
import io.github.jodlodi.bossrifts.registry.ModEntities;
import io.github.jodlodi.bossrifts.registry.ModItems;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = BossRifts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModData {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(new ModBlockStates(generator, event.getExistingFileHelper()));
		generator.addProvider(new ModItemModels(generator, event.getExistingFileHelper()));
		generator.addProvider(new ModCrafting(generator));
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
			this.simpleBlock(ModBlocks.RIFT_AGGREGATOR.get(),  this.models().withExistingParent(ModBlocks.RIFT_AGGREGATOR.getId().getPath(), "block/block")

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

		public ModLang(DataGenerator generator) {
			super(generator, BossRifts.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(ModEntities.BOSS_RIFT.get(), "Boss Rift");

			this.add(ModItems.RIFT_PEARL.get(), "Rift Pearl");
			this.add(ModItems.RIFT_PEARL.get().getDescriptionId() + ".aggregator", "Linked Rift Pearl");

			this.add(ModBlocks.RIFT_AGGREGATOR.get(), "Rift Aggregator");

			this.add("subtitles.bossrifts.entity.boss_rift.spawn", "Rift spawns");
			this.add("subtitles.bossrifts.entity.boss_rift.open", "Rift opens");
			this.add("subtitles.bossrifts.entity.boss_rift.close", "Rift closes");
			this.add("subtitles.bossrifts.entity.boss_rift.rev_up", "Rift is revving");
			this.add("subtitles.bossrifts.entity.boss_rift.warp", "Rift warps");
			this.add("subtitles.bossrifts.entity.boss_rift.expire", "Rift expires");
		}
	}
}
