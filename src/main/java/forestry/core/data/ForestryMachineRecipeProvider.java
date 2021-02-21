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

import forestry.api.core.ForestryAPI;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
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
import forestry.core.items.EnumContainerType;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.items.EnumElectronTube;

public class ForestryMachineRecipeProvider extends RecipeProvider {

	public ForestryMachineRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
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
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.IMPREGNATED_CASING)
						.patternLine("###")
						.patternLine("# #")
						.patternLine("###")
						.key('#', ItemTags.LOGS))
				.build(consumer, id("carpenter", "impregnated_casing"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.SEED_OIL.getFluid(500))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreBlocks.BASE.get(BlockTypeCoreTesr.ESCRITOIRE).item())
						.patternLine("#  ")
						.patternLine("###")
						.patternLine("# #")
						.key('#', ItemTags.PLANKS))
				.build(consumer, id("carpenter", "escritoire"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.SEED_OIL.getFluid(100))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.STICK_IMPREGNATED, 2)
						.patternLine("#")
						.patternLine("#")
						.key('#', ItemTags.LOGS))
				.build(consumer, id("carpenter", "impregnated_stick"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 250))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.WOOD_PULP, 4)
						.patternLine("#E")
						.key('#', ItemTags.LOGS)
						.key('E', Ingredient.EMPTY)) // Work around shaped recipes not wanting single item recipes
				.build(consumer, id("carpenter", "wood_pulp"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreBlocks.HUMUS, 9)
						.patternLine("###")
						.patternLine("#X#")
						.patternLine("###")
						.key('#', Items.DIRT)
						.key('X', CoreItems.MULCH))
				.build(consumer, id("carpenter", "humus"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreBlocks.BOG_EARTH, 8)
						.patternLine("#X#")
						.patternLine("XYX")
						.patternLine("#X#")
						.key('#', Items.DIRT)
						.key('X', Tags.Items.SAND)
						.key('Y', CoreItems.MULCH))
				.build(consumer, id("carpenter", "bog_earth"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(75)
				.setLiquid(new FluidStack(Fluids.WATER, 5000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.HARDENED_CASING)
						.patternLine("X X")
						.patternLine(" Y ")
						.patternLine("X X")
						.key('X', Tags.Items.GEMS_DIAMOND)
						.key('Y', CoreItems.STURDY_CASING))
				.build(consumer, id("carpenter", "hardened_casing"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.IODINE_CHARGE)
						.patternLine("Z#Z")
						.patternLine("#Y#")
						.patternLine("X#X")
						.key('#', ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL).getItem())
						.key('X', Items.GUNPOWDER)
						.key('Y', FluidsItems.CONTAINERS.get(EnumContainerType.CAN))
						.key('Z', ApicultureItems.HONEY_DROPS.stack(EnumHoneyDrop.HONEY).getItem()))
				.build(consumer, id("carpenter", "iodine_charge"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(new FluidStack(Fluids.WATER, 1000))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.DISSIPATION_CHARGE).getItem())
						.patternLine("Z#Z")
						.patternLine("#Y#")
						.patternLine("X#X")
						.key('#', ApicultureItems.ROYAL_JELLY)
						.key('X', Items.GUNPOWDER)
						.key('Y', FluidsItems.CONTAINERS.get(EnumContainerType.CAN))
						.key('Z', ApicultureItems.HONEYDEW))
				.build(consumer, id("carpenter", "dissipation_charge"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(100)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(Items.ENDER_PEARL)
						.patternLine(" # ")
						.patternLine("###")
						.patternLine(" # ")
						.key('#', CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.PULSATING_MESH).getItem()))
				.build(consumer, id("carpenter", "ender_pearl"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(10)
				.setLiquid(new FluidStack(Fluids.WATER, 500))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.WOVEN_SILK).getItem())
						.patternLine("XXX")
						.patternLine("XXX")
						.patternLine("XXX")
						.key('X', CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP).getItem()))
				.build(consumer, id("carpenter", "woven_silk"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.INGOT_BRONZE, 2)
						.patternLine("#E")
						.key('#', CoreItems.BRONZE_PICKAXE)
						.key('E', Ingredient.EMPTY)) // Work around shaped recipes not wanting single item recipes
				.build(consumer, id("carpenter", "reclaim_bronze_pickaxe"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(5)
				.setLiquid(null)
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.INGOT_BRONZE, 1)
						.patternLine("#E")
						.key('#', CoreItems.BRONZE_SHOVEL)
						.key('E', Ingredient.EMPTY)) // Work around shaped recipes not wanting single item recipes
				.build(consumer, id("carpenter", "reclaim_bronze_shovel"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(50)
				.setLiquid(ForestryFluids.HONEY.getFluid(500))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SCENTED_PANELING).getItem())
						.patternLine(" J ")
						.patternLine("###")
						.patternLine("WPW")
						.key('#', ItemTags.PLANKS)
						.key('J', ApicultureItems.ROYAL_JELLY)
						.key('W', CoreItems.BEESWAX)
						.key('P', ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL).getItem()))
				.build(consumer, id("carpenter", "scented_paneling"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(30)
				.setLiquid(new FluidStack(Fluids.WATER, 600))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BASE.stack(BlockTypeApiculture.APIARY).getItem(), 24)
						.patternLine(" X ")
						.patternLine("###")
						.patternLine("###")
						.key('#', CoreItems.BEESWAX)
						.key('X', Items.STRING))
				.build(consumer, id("carpenter", "apiary_string"));
		new CarpenterRecipeBuilder()
				.setPackagingTime(10)
				.setLiquid(new FluidStack(Fluids.WATER, 200))
				.setBox(Ingredient.EMPTY)
				.recipe(ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BASE.stack(BlockTypeApiculture.APIARY).getItem(), 6)
						.patternLine("#X#")
						.key('#', CoreItems.BEESWAX)
						.key('X', CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP).getItem()))
				.build(consumer, id("carpenter", "apiary_silk_wisp"));
	}

	private void registerCentrifuge(Consumer<IFinishedRecipe> consumer) {
		new CentrifugeRecipeBuilder()
				.setProcessingTime(5)
				.setInput(new ItemStack(Items.STRING))
				.product(0.15F, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1))
				.build(consumer, id("centrifuge", "string"));

		ItemStack honeyDrop = ApicultureItems.HONEY_DROPS.stack(EnumHoneyDrop.HONEY, 1);

		// Honey combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9F, honeyDrop)
				.build(consumer, id("centrifuge", "honey_comb"));

		// Cocoa combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.COCOA, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.5f, new ItemStack(Items.COCOA_BEANS))
				.build(consumer, id("centrifuge", "cocoa_comb"));

		// Simmering combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SIMMERING, 1))
				.product(1.0f, CoreItems.REFRACTORY_WAX.stack())
				.product(0.7f, CoreItems.PHOSPHOR.stack(2))
				.build(consumer, id("centrifuge", "simmering_comb"));

		// Stringy combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.STRINGY, 1))
				.product(1.0f, ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.product(0.4f, honeyDrop)
				.build(consumer, id("centrifuge", "stringy_comb"));

		// Dripping combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.DRIPPING, 1))
				.product(1.0f, ApicultureItems.HONEYDEW.stack())
				.product(0.4f, honeyDrop)
				.build(consumer, id("centrifuge", "dripping_comb"));

		// Frozen combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.FROZEN, 1))
				.product(0.8f, CoreItems.BEESWAX.stack())
				.product(0.7f, honeyDrop)
				.product(0.4f, new ItemStack(Items.SNOWBALL))
				.product(0.2f, ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1))
				.build(consumer, id("centrifuge", "frozen_comb"));

		// Silky combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SILKY, 1))
				.product(1.0f, honeyDrop)
				.product(0.8f, ApicultureItems.PROPOLIS.stack(EnumPropolis.SILKY, 1))
				.build(consumer, id("centrifuge", "silky_comb"));

		// Parched combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.PARCHED, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9f, honeyDrop)
				.build(consumer, id("centrifuge", "parched_comb"));

		// Mysterious combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MYSTERIOUS, 1))
				.product(1.0f, ApicultureItems.PROPOLIS.stack(EnumPropolis.PULSATING, 1))
				.product(0.4f, honeyDrop)
				.build(consumer, id("centrifuge", "mysterious_comb"));

		// Irradiated combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.IRRADIATED, 1))
				.build(consumer, id("centrifuge", "irradiated_comb"));

		// Powdery combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.POWDERY, 1))
				.product(0.2f, honeyDrop)
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.9f, new ItemStack(Items.GUNPOWDER))
				.build(consumer, id("centrifuge", "powdery_comb"));

		// Wheaten Combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.WHEATEN, 1))
				.product(0.2f, honeyDrop)
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.8f, new ItemStack(Items.WHEAT))
				.build(consumer, id("centrifuge", "wheaten_comb"));

		// Mossy Combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MOSSY, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9f, honeyDrop)
				.build(consumer, id("centrifuge", "mossy_comb"));

		// Mellow Combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MELLOW, 1))
				.product(0.6f, ApicultureItems.HONEYDEW.stack())
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.3f, new ItemStack(Items.QUARTZ))
				.build(consumer, id("centrifuge", "mellow_comb"));

		// Silky Propolis
		new CentrifugeRecipeBuilder()
				.setProcessingTime(5)
				.setInput(ApicultureItems.PROPOLIS.stack(EnumPropolis.SILKY, 1))
				.product(0.6f, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1))
				.product(0.1f, ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.build(consumer, id("centrifuge", "silky_propolis"));
	}

	private void registerFabricator(Consumer<IFinishedRecipe> consumer) {
		FluidStack liquidGlass = ForestryFluids.GLASS.getFluid(500);

		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Tags.Items.INGOTS_IRON))
				.build(consumer, id("fabricator", "electron_tubes", "iron"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Tags.Items.INGOTS_GOLD))
				.build(consumer, id("fabricator", "electron_tubes", "gold"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.DIAMOND), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Tags.Items.GEMS_DIAMOND))
				.build(consumer, id("fabricator", "electron_tubes", "diamond"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.OBSIDIAN), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Items.OBSIDIAN))
				.build(consumer, id("fabricator", "electron_tubes", "obsidian"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.BLAZE), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Items.BLAZE_POWDER))
				.build(consumer, id("fabricator", "electron_tubes", "blaze"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.EMERALD), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Tags.Items.GEMS_EMERALD))
				.build(consumer, id("fabricator", "electron_tubes", "emerald"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.LAPIS), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.key('X', Tags.Items.GEMS_LAPIS))
				.build(consumer, id("fabricator", "electron_tubes", "lapis"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.ENDER), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Items.ENDER_EYE)
						.key('X', Items.END_STONE))
				.build(consumer, id("fabricator", "electron_tubes", "ender"));
		new FabricatorRecipeBuilder()
				.setPlan(Ingredient.EMPTY)
				.setMolten(liquidGlass)
				.recipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.ELECTRON_TUBES.get(EnumElectronTube.ORCHID), 4)
						.patternLine(" X ")
						.patternLine("#X#")
						.patternLine("XXX")
						.key('#', Items.REPEATER)
						.key('X', Items.REDSTONE_ORE))
				.build(consumer, id("fabricator", "electron_tubes", "orchid"));
	}

	private void registerFabricatorSmelting(Consumer<IFinishedRecipe> consumer) {
		FluidStack liquidGlassBucket = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME);
		FluidStack liquidGlassX4 = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME * 4);
		FluidStack liquidGlass375 = ForestryFluids.GLASS.getFluid(375);

		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.GLASS))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(1000)
				.build(consumer, id("fabricator", "smelting", "glass"));
		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.GLASS_PANE))
				.setProduct(liquidGlass375)
				.setMeltingPoint(1000)
				.build(consumer, id("fabricator", "smelting", "glass_pane"));
		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.SAND, Items.RED_SAND))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(3000)
				.build(consumer, id("fabricator", "smelting", "sand"));
		new FabricatorSmeltingRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.SANDSTONE, Items.SMOOTH_SANDSTONE, Items.CHISELED_SANDSTONE))
				.setProduct(liquidGlassX4)
				.setMeltingPoint(4800)
				.build(consumer, id("fabricator", "smelting", "sandstone"));
	}

	private void registerFermenter(Consumer<IFinishedRecipe> consumer) {
		ForestryAPI.activeMode = new GameMode("EASY");

		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.BROWN_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.HONEY.getFluid(1))
				.build(consumer, id("fermenter", "brown_mushroom_honey"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.BROWN_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.JUICE.getFluid(1))
				.build(consumer, id("fermenter", "brown_mushroom_juice"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.RED_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.HONEY.getFluid(1))
				.build(consumer, id("fermenter", "red_mushroom_honey"));
		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.RED_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.JUICE.getFluid(1))
				.build(consumer, id("fermenter", "red_mushroom_juice"));

		FluidStack shortMead = ForestryFluids.SHORT_MEAD.getFluid(1);
		FluidStack honey = ForestryFluids.HONEY.getFluid(1);

		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(ApicultureItems.HONEYDEW))
				.setFermentationValue(500)
				.setModifier(1.0f)
				.setOutput(shortMead.getFluid())
				.setFluidResource(honey)
				.build(consumer, id("fermenter", "honeydew"));
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
				.setResource(Ingredient.fromItems(Items.WHEAT_SEEDS))
				.setProduct(new ItemStack(Items.MYCELIUM))
				.setTimePerItem(5000)
				.build(consumer, id("moistener", "mycelium"));
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.COBBLESTONE))
				.setProduct(new ItemStack(Items.MOSSY_COBBLESTONE))
				.setTimePerItem(20000)
				.build(consumer, id("moistener", "mossy_cobblestone"));
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.STONE_BRICKS))
				.setProduct(new ItemStack(Items.MOSSY_STONE_BRICKS))
				.setTimePerItem(20000)
				.build(consumer, id("moistener", "mossy_stone_bricks"));
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.SPRUCE_LEAVES))
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
		ItemStack honeyDrop = ApicultureItems.HONEY_DROPS.stack(EnumHoneyDrop.HONEY, 1);
		FluidStack honeyDropFluid = ForestryFluids.HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromStacks(honeyDrop)))
				.setFluidOutput(honeyDropFluid)
				.setRemnants(ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.setRemnantsChance(5 / 100f)
				.build(consumer, id("squeezer", "honey_drop"));

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(ApicultureItems.HONEYDEW)))
				.setFluidOutput(honeyDropFluid)
				.build(consumer, id("squeezer", "honey_dew"));

		ItemStack phosphor = CoreItems.PHOSPHOR.stack(2);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.fromStacks(phosphor));
					ingredients.add(Ingredient.fromItems(Items.SAND, Items.RED_SAND));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 2000))
				.build(consumer, id("squeezer", "lava_sand"));

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.fromStacks(phosphor));
					ingredients.add(Ingredient.fromItems(Items.DIRT, Items.COBBLESTONE));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 1600))
				.build(consumer, id("squeezer", "lava"));

		int seedOilAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = ForestryFluids.SEED_OIL.getFluid(seedOilAmount);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromTag(Tags.Items.SEEDS)))
				.setFluidOutput(seedOil)
				.build(consumer, id("squeezer", "seeds"));

		int appleMulchAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		FluidStack appleJuice = ForestryFluids.JUICE.getFluid(appleJuiceAmount);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.APPLE, Items.CARROT)))
				.setFluidOutput(appleJuice)
				.setRemnants(CoreItems.MULCH.stack())
				.setRemnantsChance(appleMulchAmount / 100f)
				.build(consumer, id("squeezer", "mulch"));

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.CACTUS)))
				.setFluidOutput(new FluidStack(Fluids.WATER, 500))
				.build(consumer, id("squeezer", "cactus"));

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), ingredients -> {
					ingredients.add(Ingredient.fromItems(Items.SNOWBALL));
					ingredients.add(Ingredient.fromStacks(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.ICE_SHARD, 4)));
				}))
				.setFluidOutput(ForestryFluids.ICE.getFluid(4000))
				.build(consumer, id("squeezer", "ice"));

		new SqueezerRecipeBuilder()
				.setProcessingTime(8)
				.setResources(NonNullList.withSize(1, Ingredient.fromStacks(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.CAMOUFLAGED_PANELING))))
				.setFluidOutput(ForestryFluids.BIOMASS.getFluid(150))
				.build(consumer, id("squeezer", "biomass"));
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
