/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.data;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.circuits.ICircuit;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.climatology.features.ClimatologyItems;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Constants;
import forestry.core.config.Preference;
import forestry.core.data.builder.CarpenterRecipeBuilder;
import forestry.core.data.builder.CentrifugeRecipeBuilder;
import forestry.core.data.builder.FabricatorRecipeBuilder;
import forestry.core.data.builder.FabricatorSmeltingRecipeBuilder;
import forestry.core.data.builder.FermenterRecipeBuilder;
import forestry.core.data.builder.HygroregulatorRecipeBuilder;
import forestry.core.data.builder.MoistenerRecipeBuilder;
import forestry.core.data.builder.SqueezerContainerRecipeBuilder;
import forestry.core.data.builder.SqueezerRecipeBuilder;
import forestry.core.data.builder.StillRecipeBuilder;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.ItemFruit;
import forestry.core.items.definitions.EnumContainerType;
import forestry.core.items.definitions.EnumCraftingMaterial;
import forestry.core.items.definitions.EnumElectronTube;
import forestry.mail.features.MailItems;
import forestry.mail.items.ItemLetter;
import forestry.modules.features.FeatureItem;
import forestry.storage.features.BackpackItems;
import forestry.storage.features.CrateItems;
import forestry.storage.items.ItemCrated;

public class ForestryMachineRecipeProvider extends RecipeProvider {

	public ForestryMachineRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	public String getName() {
		return "Machine Recipes";
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		registerCarpenter(consumer);
		registerCentrifuge(consumer);
		registerFabricator(consumer);
		registerFabricatorSmelting(consumer);
		registerFermenter(consumer);
		registerHygroregulator(consumer);
		registerMoistener(consumer);
		registerSqueezerContainer(consumer);
		registerSqueezer(consumer);
		registerStill(consumer);
	}

	private void registerCarpenter(Consumer<IFinishedRecipe> consumer) {
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.SEED_OIL.getFluid(250))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.IMPREGNATED_CASING)
						.pattern("###")
						.pattern("# #")
						.pattern("###")
						.define('#', ItemTags.LOGS))
				.build(consumer, id("carpenter", "impregnated_casing"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.SEED_OIL.getFluid(500))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreBlocks.BASE.get(BlockTypeCoreTesr.ESCRITOIRE).item())
						.pattern("#  ")
						.pattern("###")
						.pattern("# #")
						.define('#', ItemTags.PLANKS))
				.build(consumer, id("carpenter", "escritoire"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.SEED_OIL.getFluid(100))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.STICK_IMPREGNATED, 2)
						.pattern("#")
						.pattern("#")
						.define('#', ItemTags.LOGS))
				.build(consumer, id("carpenter", "impregnated_stick"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 250))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapelessRecipeBuilder.shapeless(CoreItems.WOOD_PULP, 4)
						.requires(ItemTags.LOGS))
				.build(consumer, id("carpenter", "wood_pulp"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreBlocks.HUMUS, 9)
						.pattern("###")
						.pattern("#X#")
						.pattern("###")
						.define('#', Items.DIRT)
						.define('X', CoreItems.MULCH))
				.build(consumer, id("carpenter", "humus"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreBlocks.BOG_EARTH, 8)
						.pattern("#X#")
						.pattern("XYX")
						.pattern("#X#")
						.define('#', Items.DIRT)
						.define('X', Tags.Items.SAND)
						.define('Y', CoreItems.MULCH))
				.build(consumer, id("carpenter", "bog_earth"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(75)
				.setLiquid(new FluidStack(Fluids.WATER, 5000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.HARDENED_CASING)
						.pattern("X X")
						.pattern(" Y ")
						.pattern("X X")
						.define('X', Tags.Items.GEMS_DIAMOND)
						.define('Y', CoreItems.STURDY_CASING))
				.build(consumer, id("carpenter", "hardened_casing"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.IODINE_CHARGE)
						.pattern("Z#Z")
						.pattern("#Y#")
						.pattern("X#X")
						.define('#', ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL))
						.define('X', Items.GUNPOWDER)
						.define('Y', FluidsItems.CONTAINERS.get(EnumContainerType.CAN))
						.define('Z', ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY)))
				.build(consumer, id("carpenter", "iodine_charge"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.DISSIPATION_CHARGE))
						.pattern("Z#Z")
						.pattern("#Y#")
						.pattern("X#X")
						.define('#', ApicultureItems.ROYAL_JELLY)
						.define('X', Items.GUNPOWDER)
						.define('Y', FluidsItems.CONTAINERS.get(EnumContainerType.CAN))
						.define('Z', ApicultureItems.HONEYDEW))
				.build(consumer, id("carpenter", "dissipation_charge"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(100)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(Items.ENDER_PEARL)
						.pattern(" # ")
						.pattern("###")
						.pattern(" # ")
						.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.PULSATING_MESH)))
				.build(consumer, id("carpenter", "ender_pearl"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(10)
				.setLiquid(new FluidStack(Fluids.WATER, 500))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK))
						.pattern("XXX")
						.pattern("XXX")
						.pattern("XXX")
						.define('X', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP)))
				.build(consumer, id("carpenter", "woven_silk"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_BRONZE, 2)
						.requires(CoreItems.BRONZE_PICKAXE))
				.build(consumer, id("carpenter", "reclaim_bronze_pickaxe"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_BRONZE, 1)
						.requires(CoreItems.BRONZE_SHOVEL))
				.build(consumer, id("carpenter", "reclaim_bronze_shovel"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.HONEY.getFluid(500))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SCENTED_PANELING))
						.pattern(" J ")
						.pattern("###")
						.pattern("WPW")
						.define('#', ItemTags.PLANKS)
						.define('J', ApicultureItems.ROYAL_JELLY)
						.define('W', CoreItems.BEESWAX)
						.define('P', ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL)))
				.build(consumer, id("carpenter", "scented_paneling"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(30)
				.setLiquid(new FluidStack(Fluids.WATER, 600))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(ApicultureBlocks.CANDLE, 24)
						.pattern(" X ")
						.pattern("###")
						.pattern("###")
						.define('#', CoreItems.BEESWAX)
						.define('X', Items.STRING))
				.build(consumer, id("carpenter", "candle_string"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(10)
				.setLiquid(new FluidStack(Fluids.WATER, 200))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(ApicultureBlocks.CANDLE, 6)
						.pattern("#X#")
						.define('#', CoreItems.BEESWAX)
						.define('X', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP)))
				.build(consumer, id("carpenter", "candle_silk_wisp"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(100)
				.setLiquid(new FluidStack(Fluids.WATER, 2000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.PORTABLE_ALYZER)
						.pattern("X#X")
						.pattern("X#X")
						.pattern("RDR")
						.define('#', Tags.Items.GLASS_PANES)
						.define('X', ForestryTags.Items.INGOTS_TIN)
						.define('R', Tags.Items.DUSTS_REDSTONE)
						.define('D', Tags.Items.GEMS_DIAMOND))
				.build(consumer, id("carpenter", "portable_analyzer"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(20)
				.setLiquid(null)
				.setBox(Ingredient.of(CoreItems.CARTON))
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.KIT_PICKAXE)
						.pattern("###")
						.pattern(" X ")
						.pattern(" X ")
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('X', Items.STICK))
				.build(consumer, id("carpenter", "kit_pickaxe"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(20)
				.setLiquid(null)
				.setBox(Ingredient.of(CoreItems.CARTON))
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.KIT_SHOVEL)
						.pattern(" # ")
						.pattern(" X ")
						.pattern(" X ")
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('X', Items.STICK))
				.build(consumer, id("carpenter", "kit_shovel"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(40)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.SOLDERING_IRON)
						.pattern(" # ")
						.pattern("# #")
						.pattern("  B")
						.define('#', Tags.Items.INGOTS_IRON)
						.define('B', ForestryTags.Items.INGOTS_BRONZE))
				.build(consumer, id("carpenter", "soldering_iron"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 250))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(Items.PAPER)
						.pattern("#")
						.pattern("#")
						.define('#', CoreItems.WOOD_PULP))
				.build(consumer, id("carpenter", "paper"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CARTON, 2)
						.pattern(" # ")
						.pattern("# #")
						.pattern(" # ")
						.define('#', CoreItems.WOOD_PULP))
				.build(consumer, id("carpenter", "carton"));

		ItemStack basic = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[]{});
		ItemStack enhanced = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[]{});
		ItemStack refined = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{});
		ItemStack intricate = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[]{});

		new CarpenterRecipeBuilder()
				.setPackagingTime(20)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.override(basic)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC))
						.pattern("R R")
						.pattern("R#R")
						.pattern("R R")
						.define('#', ForestryTags.Items.INGOTS_TIN)
						.define('R', Tags.Items.DUSTS_REDSTONE))
				.build(consumer, id("carpenter", "circuits", "basic"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(40)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.override(enhanced)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.ENHANCED))
						.pattern("R#R")
						.pattern("R#R")
						.pattern("R#R")
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('R', Tags.Items.DUSTS_REDSTONE))
				.build(consumer, id("carpenter", "circuits", "enhanced"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(80)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.override(refined)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.REFINED))
						.pattern("R#R")
						.pattern("R#R")
						.pattern("R#R")
						.define('#', Tags.Items.INGOTS_IRON)
						.define('R', Tags.Items.DUSTS_REDSTONE))
				.build(consumer, id("carpenter", "circuits", "refined"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(80)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.override(intricate)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.INTRICATE))
						.pattern("R#R")
						.pattern("R#R")
						.pattern("R#R")
						.define('#', Tags.Items.INGOTS_GOLD)
						.define('R', Tags.Items.DUSTS_REDSTONE))
				.build(consumer, id("carpenter", "circuits", "intricate"));

		new CarpenterRecipeBuilder()
				.setPackagingTime(100)
				.setLiquid(new FluidStack(Fluids.WATER, 2000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(ClimatologyItems.HABITAT_SCREEN)
						.pattern("IPI")
						.pattern("IPI")
						.pattern("GDG")
						.define('G', CoreItems.GEAR_BRONZE)
						.define('P', Tags.Items.GLASS_PANES)
						.define('I', CoreItems.INGOT_BRONZE)
						.define('D', Tags.Items.GEMS_DIAMOND))
				.build(consumer, id("carpenter", "habitat_screen"));
		// / Crates
		new CarpenterRecipeBuilder()
				.setPackagingTime(20)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(CrateItems.CRATE, 24)
						.pattern(" # ")
						.pattern("# #")
						.pattern(" # ")
						.define('#', ItemTags.LOGS))
				.build(consumer, id("carpenter", "crates", "empty"));

		crate(consumer, CrateItems.CRATED_PEAT.get(), Ingredient.of(CoreItems.PEAT));
		crate(consumer, CrateItems.CRATED_APATITE.get(), Ingredient.of(CoreItems.APATITE));
		crate(consumer, CrateItems.CRATED_FERTILIZER_COMPOUND.get(), Ingredient.of(CoreItems.FERTILIZER_COMPOUND));
		crate(consumer, CrateItems.CRATED_MULCH.get(), Ingredient.of(CoreItems.MULCH));
		crate(consumer, CrateItems.CRATED_PHOSPHOR.get(), Ingredient.of(CoreItems.PHOSPHOR));
		crate(consumer, CrateItems.CRATED_ASH.get(), Ingredient.of(CoreItems.ASH));
		crate(consumer, CrateItems.CRATED_TIN.get(), Ingredient.of(ForestryTags.Items.INGOTS_TIN));
		crate(consumer, CrateItems.CRATED_COPPER.get(), Ingredient.of(ForestryTags.Items.INGOTS_COPPER));
		crate(consumer, CrateItems.CRATED_BRONZE.get(), Ingredient.of(ForestryTags.Items.INGOTS_BRONZE));

		crate(consumer, CrateItems.CRATED_HUMUS.get(), Ingredient.of(CoreBlocks.HUMUS));
		crate(consumer, CrateItems.CRATED_BOG_EARTH.get(), Ingredient.of(CoreBlocks.BOG_EARTH));

		crate(consumer, CrateItems.CRATED_WHEAT.get(), Ingredient.of(Tags.Items.CROPS_WHEAT));
		crate(consumer, CrateItems.CRATED_COOKIE.get(), Ingredient.of(Items.COOKIE));
		crate(consumer, CrateItems.CRATED_REDSTONE.get(), Ingredient.of(Tags.Items.DUSTS_REDSTONE));
		crate(consumer, CrateItems.CRATED_LAPIS.get(), Ingredient.of(Tags.Items.GEMS_LAPIS));
		crate(consumer, CrateItems.CRATED_SUGAR_CANE.get(), Ingredient.of(Items.SUGAR_CANE));
		crate(consumer, CrateItems.CRATED_CLAY_BALL.get(), Ingredient.of(Items.CLAY_BALL));
		crate(consumer, CrateItems.CRATED_GLOWSTONE.get(), Ingredient.of(Tags.Items.DUSTS_GLOWSTONE));
		crate(consumer, CrateItems.CRATED_APPLE.get(), Ingredient.of(Items.APPLE));
		crate(consumer, CrateItems.CRATED_COAL.get(), Ingredient.of(Items.COAL));
		crate(consumer, CrateItems.CRATED_CHARCOAL.get(), Ingredient.of(Items.CHARCOAL));
		crate(consumer, CrateItems.CRATED_SEEDS.get(), Ingredient.of(Items.WHEAT_SEEDS));
		crate(consumer, CrateItems.CRATED_POTATO.get(), Ingredient.of(Tags.Items.CROPS_POTATO));
		crate(consumer, CrateItems.CRATED_CARROT.get(), Ingredient.of(Tags.Items.CROPS_CARROT));
		crate(consumer, CrateItems.CRATED_BEETROOT.get(), Ingredient.of(Tags.Items.CROPS_BEETROOT));
		crate(consumer, CrateItems.CRATED_NETHER_WART.get(), Ingredient.of(Tags.Items.CROPS_NETHER_WART));

		crate(consumer, CrateItems.CRATED_OAK_LOG.get(), Ingredient.of(Items.OAK_LOG));
		crate(consumer, CrateItems.CRATED_BIRCH_LOG.get(), Ingredient.of(Items.BIRCH_LOG));
		crate(consumer, CrateItems.CRATED_JUNGLE_LOG.get(), Ingredient.of(Items.JUNGLE_LOG));
		crate(consumer, CrateItems.CRATED_SPRUCE_LOG.get(), Ingredient.of(Items.SPRUCE_LOG));
		crate(consumer, CrateItems.CRATED_ACACIA_LOG.get(), Ingredient.of(Items.ACACIA_LOG));
		crate(consumer, CrateItems.CRATED_DARK_OAK_LOG.get(), Ingredient.of(Items.DARK_OAK_LOG));
		crate(consumer, CrateItems.CRATED_COBBLESTONE.get(), Ingredient.of(Tags.Items.COBBLESTONE));
		crate(consumer, CrateItems.CRATED_DIRT.get(), Ingredient.of(Items.DIRT));
		crate(consumer, CrateItems.CRATED_GRASS_BLOCK.get(), Ingredient.of(Items.GRASS_BLOCK));
		crate(consumer, CrateItems.CRATED_STONE.get(), Ingredient.of(Tags.Items.STONE));
		crate(consumer, CrateItems.CRATED_GRANITE.get(), Ingredient.of(Items.GRANITE));
		crate(consumer, CrateItems.CRATED_DIORITE.get(), Ingredient.of(Items.DIORITE));
		crate(consumer, CrateItems.CRATED_ANDESITE.get(), Ingredient.of(Items.ANDESITE));
		crate(consumer, CrateItems.CRATED_PRISMARINE.get(), Ingredient.of(Items.PRISMARINE));
		crate(consumer, CrateItems.CRATED_PRISMARINE_BRICKS.get(), Ingredient.of(Items.PRISMARINE_BRICKS));
		crate(consumer, CrateItems.CRATED_DARK_PRISMARINE.get(), Ingredient.of(Items.DARK_PRISMARINE));
		crate(consumer, CrateItems.CRATED_BRICKS.get(), Ingredient.of(Items.BRICKS));
		crate(consumer, CrateItems.CRATED_CACTUS.get(), Ingredient.of(Items.CACTUS));
		crate(consumer, CrateItems.CRATED_SAND.get(), Ingredient.of(Items.SAND));
		crate(consumer, CrateItems.CRATED_RED_SAND.get(), Ingredient.of(Items.RED_SAND));
		crate(consumer, CrateItems.CRATED_OBSIDIAN.get(), Ingredient.of(Tags.Items.OBSIDIAN));
		crate(consumer, CrateItems.CRATED_NETHERRACK.get(), Ingredient.of(Tags.Items.NETHERRACK));
		crate(consumer, CrateItems.CRATED_SOUL_SAND.get(), Ingredient.of(Items.SOUL_SAND));
		crate(consumer, CrateItems.CRATED_SANDSTONE.get(), Ingredient.of(Tags.Items.SANDSTONE));
		crate(consumer, CrateItems.CRATED_NETHER_BRICKS.get(), Ingredient.of(Items.NETHER_BRICKS));
		crate(consumer, CrateItems.CRATED_MYCELIUM.get(), Ingredient.of(Items.MYCELIUM));
		crate(consumer, CrateItems.CRATED_GRAVEL.get(), Ingredient.of(Tags.Items.GRAVEL));
		crate(consumer, CrateItems.CRATED_OAK_SAPLING.get(), Ingredient.of(Items.OAK_SAPLING));
		crate(consumer, CrateItems.CRATED_BIRCH_SAPLING.get(), Ingredient.of(Items.BIRCH_SAPLING));
		crate(consumer, CrateItems.CRATED_JUNGLE_SAPLING.get(), Ingredient.of(Items.JUNGLE_SAPLING));
		crate(consumer, CrateItems.CRATED_SPRUCE_SAPLING.get(), Ingredient.of(Items.SPRUCE_SAPLING));
		crate(consumer, CrateItems.CRATED_ACACIA_SAPLING.get(), Ingredient.of(Items.ACACIA_SAPLING));
		crate(consumer, CrateItems.CRATED_DARK_OAK_SAPLING.get(), Ingredient.of(Items.DARK_OAK_SAPLING));

		crate(consumer, CrateItems.CRATED_BEESWAX.get(), Ingredient.of(CoreItems.BEESWAX));
		crate(consumer, CrateItems.CRATED_REFRACTORY_WAX.get(), Ingredient.of(CoreItems.REFRACTORY_WAX));

		crate(consumer, CrateItems.CRATED_POLLEN_CLUSTER_NORMAL.get(), Ingredient.of(ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL)));
		crate(consumer, CrateItems.CRATED_POLLEN_CLUSTER_CRYSTALLINE.get(), Ingredient.of(ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.CRYSTALLINE)));
		crate(consumer, CrateItems.CRATED_PROPOLIS.get(), Ingredient.of(ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL)));
		crate(consumer, CrateItems.CRATED_HONEYDEW.get(), Ingredient.of(ApicultureItems.HONEYDEW));
		crate(consumer, CrateItems.CRATED_ROYAL_JELLY.get(), Ingredient.of(ApicultureItems.ROYAL_JELLY));

		for (EnumHoneyComb comb : EnumHoneyComb.VALUES) {
			crate(consumer, CrateItems.CRATED_BEE_COMBS.get(comb).get(), Ingredient.of(ApicultureItems.BEE_COMBS.get(comb)));
		}

		new CarpenterRecipeBuilder()
				.setPackagingTime(10)
				.setLiquid(new FluidStack(Fluids.WATER, 250))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(MailItems.LETTERS.get(ItemLetter.Size.EMPTY, ItemLetter.State.FRESH).item())
						.pattern("###")
						.pattern("###")
						.define('#', CoreItems.WOOD_PULP))
				.build(consumer, id("carpenter", "letter_pulp"));

		wovenBackpack(consumer, "miner", BackpackItems.MINER_BACKPACK, BackpackItems.MINER_BACKPACK_T_2);
		wovenBackpack(consumer, "digger", BackpackItems.DIGGER_BACKPACK, BackpackItems.DIGGER_BACKPACK_T_2);
		wovenBackpack(consumer, "forester", BackpackItems.FORESTER_BACKPACK, BackpackItems.FORESTER_BACKPACK_T_2);
		wovenBackpack(consumer, "hunter", BackpackItems.HUNTER_BACKPACK, BackpackItems.HUNTER_BACKPACK_T_2);
		wovenBackpack(consumer, "adventurer", BackpackItems.ADVENTURER_BACKPACK, BackpackItems.ADVENTURER_BACKPACK_T_2);
		wovenBackpack(consumer, "builder", BackpackItems.BUILDER_BACKPACK, BackpackItems.BUILDER_BACKPACK_T_2);
	}

	private void wovenBackpack(Consumer<IFinishedRecipe> consumer, String id, FeatureItem<?> tier1, FeatureItem<?> tier2) {
		new CarpenterRecipeBuilder()
				.setPackagingTime(200)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shaped(tier2)
						.pattern("WXW")
						.pattern("WTW")
						.pattern("WWW")
						.define('W', CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.WOVEN_SILK).getItem())
						.define('X', Items.DIAMOND)
						.define('T', tier1))
				.build(consumer, id("woven_backpack", id));
	}

	private void crate(Consumer<IFinishedRecipe> consumer, ItemCrated crated, Ingredient ingredient) {
		ItemStack contained = crated.getContained();
		ResourceLocation name = contained.getItem().getRegistryName();

		if (name == null) {
			return;
		}

		new CarpenterRecipeBuilder()
				.setPackagingTime(Constants.CARPENTER_CRATING_CYCLES)
				.setLiquid(new FluidStack(Fluids.WATER, Constants.CARPENTER_CRATING_LIQUID_QUANTITY))
				.setBox(Ingredient.of(CrateItems.CRATE))
				.recipe(ShapedRecipeBuilder.shaped(crated, 1)
						.pattern("###")
						.pattern("###")
						.pattern("###")
						.define('#', ingredient))
				.build(consumer, id("carpenter", "crates", "pack", name.getNamespace(), name.getPath()));
		new CarpenterRecipeBuilder()
				.setPackagingTime(Constants.CARPENTER_UNCRATING_CYCLES)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapelessRecipeBuilder.shapeless(contained.getItem(), 9).requires(crated))
				.build(consumer, id("carpenter", "crates", "unpack", name.getNamespace(), name.getPath()));
	}

	private void registerCentrifuge(Consumer<IFinishedRecipe> consumer) {
		new CentrifugeRecipeBuilder()
				.setProcessingTime(5)
				.setInput(Ingredient.of(Items.STRING))
				.product(0.15F, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1))
				.build(consumer, id("centrifuge", "string"));

		ItemStack honeyDrop = ApicultureItems.HONEY_DROPS.stack(EnumHoneyDrop.HONEY, 1);

		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.HONEY)))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9F, honeyDrop)
				.build(consumer, id("centrifuge", "honey_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.COCOA)))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.5f, new ItemStack(Items.COCOA_BEANS))
				.build(consumer, id("centrifuge", "cocoa_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.SIMMERING)))
				.product(1.0f, CoreItems.REFRACTORY_WAX.stack())
				.product(0.7f, CoreItems.PHOSPHOR.stack(2))
				.build(consumer, id("centrifuge", "simmering_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.STRINGY)))
				.product(1.0f, ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.product(0.4f, honeyDrop)
				.build(consumer, id("centrifuge", "stringy_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.DRIPPING)))
				.product(1.0f, ApicultureItems.HONEYDEW.stack())
				.product(0.4f, honeyDrop)
				.build(consumer, id("centrifuge", "dripping_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.FROZEN)))
				.product(0.8f, CoreItems.BEESWAX.stack())
				.product(0.7f, honeyDrop)
				.product(0.4f, new ItemStack(Items.SNOWBALL))
				.product(0.2f, ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1))
				.build(consumer, id("centrifuge", "frozen_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.SILKY)))
				.product(1.0f, honeyDrop)
				.product(0.8f, ApicultureItems.PROPOLIS.stack(EnumPropolis.SILKY, 1))
				.build(consumer, id("centrifuge", "silky_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.PARCHED)))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9f, honeyDrop)
				.build(consumer, id("centrifuge", "parched_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.MYSTERIOUS)))
				.product(1.0f, ApicultureItems.PROPOLIS.stack(EnumPropolis.PULSATING, 1))
				.product(0.4f, honeyDrop)
				.build(consumer, id("centrifuge", "mysterious_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.IRRADIATED)))
				.build(consumer, id("centrifuge", "irradiated_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.POWDERY)))
				.product(0.2f, honeyDrop)
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.9f, new ItemStack(Items.GUNPOWDER))
				.build(consumer, id("centrifuge", "powdery_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.WHEATEN)))
				.product(0.2f, honeyDrop)
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.8f, new ItemStack(Items.WHEAT))
				.build(consumer, id("centrifuge", "wheaten_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.MOSSY)))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9f, honeyDrop)
				.build(consumer, id("centrifuge", "mossy_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(Ingredient.of(ApicultureItems.BEE_COMBS.get(EnumHoneyComb.MELLOW)))
				.product(0.6f, ApicultureItems.HONEYDEW.stack())
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.3f, new ItemStack(Items.QUARTZ))
				.build(consumer, id("centrifuge", "mellow_comb"));
		new CentrifugeRecipeBuilder()
				.setProcessingTime(5)
				.setInput(Ingredient.of(ApicultureItems.PROPOLIS.get(EnumPropolis.SILKY)))
				.product(0.6f, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1))
				.product(0.1f, ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.build(consumer, id("centrifuge", "silky_propolis"));
	}

	private void registerFabricator(Consumer<IFinishedRecipe> consumer) {
		FluidStack liquidGlass = ForestryFluids.GLASS.getFluid(500);

		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Tags.Items.INGOTS_IRON))
				.build(consumer, id("fabricator", "electron_tubes", "iron"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Tags.Items.INGOTS_GOLD))
				.build(consumer, id("fabricator", "electron_tubes", "gold"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.DIAMOND), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Tags.Items.GEMS_DIAMOND))
				.build(consumer, id("fabricator", "electron_tubes", "diamond"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.OBSIDIAN), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Items.OBSIDIAN))
				.build(consumer, id("fabricator", "electron_tubes", "obsidian"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.BLAZE), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Items.BLAZE_POWDER))
				.build(consumer, id("fabricator", "electron_tubes", "blaze"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.EMERALD), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Tags.Items.GEMS_EMERALD))
				.build(consumer, id("fabricator", "electron_tubes", "emerald"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.LAPIS), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', Tags.Items.GEMS_LAPIS))
				.build(consumer, id("fabricator", "electron_tubes", "lapis"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.ENDER), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Items.ENDER_EYE)
						.define('X', Items.END_STONE))
				.build(consumer, id("fabricator", "electron_tubes", "ender"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.ORCHID), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Items.REPEATER)
						.define('X', Items.REDSTONE_ORE))
				.build(consumer, id("fabricator", "electron_tubes", "orchid"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.COPPER), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', ForestryTags.Items.INGOTS_COPPER))
				.build(consumer, id("fabricator", "electron_tubes", "copper"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.TIN), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', ForestryTags.Items.INGOTS_TIN))
				.build(consumer, id("fabricator", "electron_tubes", "tin"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.BRONZE), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', ForestryTags.Items.INGOTS_BRONZE))
				.build(consumer, id("fabricator", "electron_tubes", "bronze"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.APATITE), 4)
						.pattern(" X ")
						.pattern("#X#")
						.pattern("XXX")
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.define('X', ForestryTags.Items.GEMS_APATITE))
				.build(consumer, id("fabricator", "electron_tubes", "apatite"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(CoreItems.FLEXIBLE_CASING)
						.pattern("#E#")
						.pattern("B B")
						.pattern("#E#")
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('B', Tags.Items.SLIMEBALLS)
						.define('E', Tags.Items.GEMS_EMERALD))
				.build(consumer, id("fabricator", "electron_tubes", "flexible_casing"));

		for (EnumForestryWoodType type : EnumForestryWoodType.values()) {
			addFireproofRecipes(consumer, type);
		}

		for (EnumVanillaWoodType type : EnumVanillaWoodType.values()) {
			addFireproofRecipes(consumer, type);
		}
	}

	private void addFireproofRecipes(Consumer<IFinishedRecipe> consumer, IWoodType type) {
		FluidStack liquidGlass = ForestryFluids.GLASS.getFluid(500);

		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(TreeManager.woodAccess.getBlock(type, WoodBlockKind.LOG, true).getBlock())
						.pattern(" # ")
						.pattern("#X#")
						.pattern(" # ")
						.define('#', CoreItems.REFRACTORY_WAX)
						.define('X', TreeManager.woodAccess.getBlock(type, WoodBlockKind.LOG, false).getBlock()))
				.build(consumer, id("fabricator", "fireproof", "logs", type.toString()));

		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shaped(TreeManager.woodAccess.getBlock(type, WoodBlockKind.PLANKS, true).getBlock(), 5)
						.pattern("X#X")
						.pattern("#X#")
						.pattern("X#X")
						.define('#', CoreItems.REFRACTORY_WAX)
						.define('X', TreeManager.woodAccess.getBlock(type, WoodBlockKind.PLANKS, false).getBlock()))
				.build(consumer, id("fabricator", "fireproof", "planks", type.toString()));
	}

	private void registerFabricatorSmelting(Consumer<IFinishedRecipe> consumer) {
		FluidStack liquidGlassBucket = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME);
		FluidStack liquidGlassX4 = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME * 4);
		FluidStack liquidGlass375 = ForestryFluids.GLASS.getFluid(375);

		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.of(Items.GLASS))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(1000)
				.build(consumer, id("fabricator", "smelting", "glass"));
		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.of(Items.GLASS_PANE))
				.setProduct(liquidGlass375)
				.setMeltingPoint(1000)
				.build(consumer, id("fabricator", "smelting", "glass_pane"));
		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.of(Items.SAND, Items.RED_SAND))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(3000)
				.build(consumer, id("fabricator", "smelting", "sand"));
		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.of(Items.SANDSTONE, Items.SMOOTH_SANDSTONE, Items.CHISELED_SANDSTONE))
				.setProduct(liquidGlassX4)
				.setMeltingPoint(4800)
				.build(consumer, id("fabricator", "smelting", "sandstone"));
	}

	private void registerFermenter(Consumer<IFinishedRecipe> consumer) {
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Items.BROWN_MUSHROOM))
				.setFermentationValue(Preference.FERMENTED_MUSHROOM)
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.HONEY.getFluid(1))
				.build(consumer, id("fermenter", "brown_mushroom_honey"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Items.BROWN_MUSHROOM))
				.setFermentationValue(Preference.FERMENTED_MUSHROOM)
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.JUICE.getFluid(1))
				.build(consumer, id("fermenter", "brown_mushroom_juice"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Items.RED_MUSHROOM))
				.setFermentationValue(Preference.FERMENTED_MUSHROOM)
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.HONEY.getFluid(1))
				.build(consumer, id("fermenter", "red_mushroom_honey"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Items.RED_MUSHROOM))
				.setFermentationValue(Preference.FERMENTED_MUSHROOM)
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.JUICE.getFluid(1))
				.build(consumer, id("fermenter", "red_mushroom_juice"));

		FluidStack shortMead = ForestryFluids.SHORT_MEAD.getFluid(1);
		FluidStack honey = ForestryFluids.HONEY.getFluid(1);

		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(ApicultureItems.HONEYDEW))
				.setFermentationValue(500)
				.setModifier(1.0f)
				.setOutput(shortMead.getFluid())
				.setFluidResource(honey)
				.build(consumer, id("fermenter", "honeydew"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(ItemTags.SAPLINGS))
				.setFermentationValue(Preference.FERMENTED_SAPLING)
				.setModifier(1)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(new FluidStack(Fluids.WATER, 1000))
				.build(consumer, id("fermenter", "sapling"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Items.CACTUS))
				.setFermentationValue(Preference.FERMENTED_CACTI)
				.setModifier(1)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(new FluidStack(Fluids.WATER, 1000))
				.build(consumer, id("fermenter", "cactus"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Tags.Items.CROPS_WHEAT))
				.setFermentationValue(Preference.FERMENTED_WHEAT)
				.setModifier(1)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(new FluidStack(Fluids.WATER, 1000))
				.build(consumer, id("fermenter", "wheat"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Tags.Items.CROPS_POTATO))
				.setFermentationValue(2 * Preference.FERMENTED_WHEAT) // TODO: Its own thing?
				.setModifier(1)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(new FluidStack(Fluids.WATER, 1000))
				.build(consumer, id("fermenter", "potato"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.of(Items.SUGAR_CANE))
				.setFermentationValue(Preference.FERMENTED_CANE)
				.setModifier(1)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(new FluidStack(Fluids.WATER, 1000))
				.build(consumer, id("fermenter", "sugar_cane"));
	}

	private void registerHygroregulator(Consumer<IFinishedRecipe> consumer) {
		new HygroregulatorRecipeBuilder()
				.setLiquid(new FluidStack(Fluids.WATER, 1))
				.setTransferTime(1)
				.setTempChange(-0.005f)
				.setHumidChange(0.01f)
				.build(consumer, id("hygroregulator", "water"));
		new HygroregulatorRecipeBuilder()
				.setLiquid(new FluidStack(Fluids.LAVA, 1))
				.setTransferTime(10)
				.setTempChange(0.005f)
				.setHumidChange(-0.01f)
				.build(consumer, id("hygroregulator", "lava"));
		new HygroregulatorRecipeBuilder()
				.setLiquid(ForestryFluids.ICE.getFluid(1))
				.setTransferTime(10)
				.setTempChange(-0.01f)
				.setHumidChange(0.02f)
				.build(consumer, id("hygroregulator", "ice"));
	}

	private void registerMoistener(Consumer<IFinishedRecipe> consumer) {
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.of(Items.WHEAT_SEEDS))
				.setProduct(new ItemStack(Items.MYCELIUM))
				.setTimePerItem(5000)
				.build(consumer, id("moistener", "mycelium"));
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.of(Items.COBBLESTONE))
				.setProduct(new ItemStack(Items.MOSSY_COBBLESTONE))
				.setTimePerItem(20000)
				.build(consumer, id("moistener", "mossy_cobblestone"));
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.of(Items.STONE_BRICKS))
				.setProduct(new ItemStack(Items.MOSSY_STONE_BRICKS))
				.setTimePerItem(20000)
				.build(consumer, id("moistener", "mossy_stone_bricks"));
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.of(Items.SPRUCE_LEAVES))
				.setProduct(new ItemStack(Items.PODZOL))
				.setTimePerItem(5000)
				.build(consumer, id("moistener", "podzol"));
	}

	private void registerSqueezerContainer(Consumer<IFinishedRecipe> consumer) {
		new SqueezerContainerRecipeBuilder()
				.setProcessingTime(10)
				.setEmptyContainer(FluidsItems.CONTAINERS.stack(EnumContainerType.CAN))
				.setRemnants(CoreItems.INGOT_TIN.stack())
				.setRemnantsChance(0.05f)
				.build(consumer, id("squeezer", "container", "can"));
		new SqueezerContainerRecipeBuilder()
				.setProcessingTime(10)
				.setEmptyContainer(FluidsItems.CONTAINERS.stack(EnumContainerType.CAPSULE))
				.setRemnants(CoreItems.BEESWAX.stack())
				.setRemnantsChance(0.10f)
				.build(consumer, id("squeezer", "container", "capsule"));
		new SqueezerContainerRecipeBuilder()
				.setProcessingTime(10)
				.setEmptyContainer(FluidsItems.CONTAINERS.stack(EnumContainerType.REFRACTORY))
				.setRemnants(CoreItems.REFRACTORY_WAX.stack())
				.setRemnantsChance(0.10f)
				.build(consumer, id("squeezer", "container", "refractory"));
	}

	private void registerSqueezer(Consumer<IFinishedRecipe> consumer) {
		FluidStack honeyDropFluid = ForestryFluids.HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY))))
				.setFluidOutput(honeyDropFluid)
				.setRemnants(ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.setRemnantsChance(5 / 100f)
				.build(consumer, id("squeezer", "honey_drop"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(ApicultureItems.HONEYDEW)))
				.setFluidOutput(honeyDropFluid)
				.build(consumer, id("squeezer", "honey_dew"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.of(CoreItems.PHOSPHOR));
					ingredients.add(Ingredient.of(CoreItems.PHOSPHOR));
					ingredients.add(Ingredient.of(Items.SAND, Items.RED_SAND));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 2000))
				.build(consumer, id("squeezer", "lava_sand"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.of(CoreItems.PHOSPHOR));
					ingredients.add(Ingredient.of(CoreItems.PHOSPHOR));
					ingredients.add(Ingredient.of(Items.DIRT, Items.COBBLESTONE));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 1600))
				.build(consumer, id("squeezer", "lava"));

		int seedOilAmount = Preference.SQUEEZED_LIQUID_SEED;

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(Tags.Items.SEEDS)))
				.setFluidOutput(ForestryFluids.SEED_OIL.getFluid(seedOilAmount))
				.build(consumer, id("squeezer", "seeds"));

		int appleMulchAmount = Preference.SQUEEZED_MULCH_APPLE;
		int appleJuiceAmount = Preference.SQUEEZED_LIQUID_APPLE;
		FluidStack appleJuice = ForestryFluids.JUICE.getFluid(appleJuiceAmount);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(Items.APPLE, Items.CARROT)))
				.setFluidOutput(appleJuice)
				.setRemnants(CoreItems.MULCH.stack())
				.setRemnantsChance(appleMulchAmount / 100f)
				.build(consumer, id("squeezer", "mulch"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(Items.CACTUS)))
				.setFluidOutput(new FluidStack(Fluids.WATER, 500))
				.build(consumer, id("squeezer", "cactus"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), ingredients -> {
					ingredients.add(Ingredient.of(Items.SNOWBALL));
					ingredients.add(Ingredient.of(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.ICE_SHARD)));
					ingredients.add(Ingredient.of(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.ICE_SHARD)));
					ingredients.add(Ingredient.of(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.ICE_SHARD)));
					ingredients.add(Ingredient.of(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.ICE_SHARD)));
				}))
				.setFluidOutput(ForestryFluids.ICE.getFluid(4000))
				.build(consumer, id("squeezer", "ice"));

		int seedOilMultiplier = Preference.SQUEEZED_LIQUID_SEED;
		int juiceMultiplier = Preference.SQUEEZED_LIQUID_APPLE;

		ItemStack mulch = new ItemStack(CoreItems.MULCH);
		Fluid seedOil = ForestryFluids.SEED_OIL.getFluid();
		Fluid juice = ForestryFluids.JUICE.getFluid();

		new SqueezerRecipeBuilder()
				.setProcessingTime(20)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.CHERRY))))
				.setFluidOutput(new FluidStack(seedOil, seedOilMultiplier * 5))
				.setRemnants(mulch)
				.setRemnantsChance(0.05F)
				.build(consumer, id("squeezer", "fruit", "cherry"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(60)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.WALNUT))))
				.setFluidOutput(new FluidStack(seedOil, seedOilMultiplier * 18))
				.setRemnants(mulch)
				.setRemnantsChance(0.05F)
				.build(consumer, id("squeezer", "fruit", "walnut"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(70)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.CHESTNUT))))
				.setFluidOutput(new FluidStack(seedOil, seedOilMultiplier * 22))
				.setRemnants(mulch)
				.setRemnantsChance(0.02F)
				.build(consumer, id("squeezer", "fruit", "chestnut"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.LEMON))))
				.setFluidOutput(new FluidStack(juice, juiceMultiplier * 2))
				.setRemnants(mulch)
				.setRemnantsChance(1) // TODO: Fix
				.build(consumer, id("squeezer", "fruit", "lemon"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.PLUM))))
				.setFluidOutput(new FluidStack(juice, juiceMultiplier / 2))
				.setRemnants(mulch)
				.setRemnantsChance(1) // TODO: Fix
				.build(consumer, id("squeezer", "fruit", "plum"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.PAPAYA))))
				.setFluidOutput(new FluidStack(juice, juiceMultiplier * 3))
				.setRemnants(mulch)
				.setRemnantsChance(1) // TODO: Fix
				.build(consumer, id("squeezer", "fruit", "papaya"));
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.DATES))))
				.setFluidOutput(new FluidStack(juice, juiceMultiplier / 4))
				.setRemnants(mulch)
				.setRemnantsChance(1) // TODO: Fix
				.build(consumer, id("squeezer", "fruit", "dates"));
	}

	private void registerStill(Consumer<IFinishedRecipe> consumer) {
		FluidStack biomass = ForestryFluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT);
		FluidStack ethanol = ForestryFluids.BIO_ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT);

		new StillRecipeBuilder()
				.setTimePerUnit(Constants.STILL_DESTILLATION_DURATION)
				.setInput(biomass)
				.setOutput(ethanol)
				.build(consumer, id("still", "ethanol"));
	}

	private static ResourceLocation id(String... path) {
		return new ResourceLocation("forestry", String.join("/", path));
	}
}
