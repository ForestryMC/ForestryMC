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

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.data.builder.CentrifugeRecipeBuilder;
import forestry.core.data.builder.FabricatorSmeltingRecipeBuilder;
import forestry.core.data.builder.FermenterRecipeBuilder;
import forestry.core.data.builder.HygroregulatorRecipeBuilder;
import forestry.core.data.builder.MoistenerRecipeBuilder;
import forestry.core.data.builder.SqueezerContainerRecipeBuilder;
import forestry.core.data.builder.SqueezerRecipeBuilder;
import forestry.core.data.builder.StillRecipeBuilder;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.EnumContainerType;
import forestry.core.items.EnumCraftingMaterial;

import static forestry.api.recipes.IForestryRecipe.anonymous;

public class ForestryMachineRecipeProvider extends RecipeProvider {

	public ForestryMachineRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		registerCentrifuge(consumer);
		registerFabricatorSmelting(consumer);
		registerFermenter(consumer);
		registerHygroregulator(consumer);
		registerMoistener(consumer);
		registerSqueezerContainer(consumer);
		registerSqueezer(consumer);
		registerStill(consumer);
	}

	private void registerHygroregulator(Consumer<IFinishedRecipe> consumer) {
		new HygroregulatorRecipeBuilder()
				.setLiquid(new FluidStack(Fluids.WATER, 1))
				.setTransferTime(1)
				.setTempChange(-0.005f)
				.setHumidChange(0.01f)
				.build(consumer, anonymous());
		new HygroregulatorRecipeBuilder()
				.setLiquid(new FluidStack(Fluids.LAVA, 1))
				.setTransferTime(10)
				.setTempChange(0.005f)
				.setHumidChange(-0.01f)
				.build(consumer, anonymous());
		new HygroregulatorRecipeBuilder()
				.setLiquid(ForestryFluids.ICE.getFluid(1))
				.setTransferTime(10)
				.setTempChange(-0.01f)
				.setHumidChange(0.02f)
				.build(consumer, anonymous());
	}

	private void registerCentrifuge(Consumer<IFinishedRecipe> consumer) {
		new CentrifugeRecipeBuilder()
				.setProcessingTime(5)
				.setInput(new ItemStack(Items.STRING))
				.product(0.15F, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1))
				.build(consumer, anonymous());

		ItemStack honeyDrop = ApicultureItems.HONEY_DROPS.stack(EnumHoneyDrop.HONEY, 1);

		// Honey combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9F, honeyDrop)
				.build(consumer, anonymous());

		// Cocoa combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.COCOA, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.5f, new ItemStack(Items.COCOA_BEANS))
				.build(consumer, anonymous());

		// Simmering combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SIMMERING, 1))
				.product(1.0f, CoreItems.REFRACTORY_WAX.stack())
				.product(0.7f, CoreItems.PHOSPHOR.stack(2))
				.build(consumer, anonymous());

		// Stringy combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.STRINGY, 1))
				.product(1.0f, ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.product(0.4f, honeyDrop)
				.build(consumer, anonymous());

		// Dripping combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.DRIPPING, 1))
				.product(1.0f, ApicultureItems.HONEYDEW.stack())
				.product(0.4f, honeyDrop)
				.build(consumer, anonymous());

		// Frozen combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.FROZEN, 1))
				.product(0.8f, CoreItems.BEESWAX.stack())
				.product(0.7f, honeyDrop)
				.product(0.4f, new ItemStack(Items.SNOWBALL))
				.product(0.2f, ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1))
				.build(consumer, anonymous());

		// Silky combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SILKY, 1))
				.product(1.0f, honeyDrop)
				.product(0.8f, ApicultureItems.PROPOLIS.stack(EnumPropolis.SILKY, 1))
				.build(consumer, anonymous());

		// Parched combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.PARCHED, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9f, honeyDrop)
				.build(consumer, anonymous());

		// Mysterious combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MYSTERIOUS, 1))
				.product(1.0f, ApicultureItems.PROPOLIS.stack(EnumPropolis.PULSATING, 1))
				.product(0.4f, honeyDrop)
				.build(consumer, anonymous());

		// Irradiated combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.IRRADIATED, 1))
				.build(consumer, anonymous());

		// Powdery combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.POWDERY, 1))
				.product(0.2f, honeyDrop)
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.9f, new ItemStack(Items.GUNPOWDER))
				.build(consumer, anonymous());

		// Wheaten Combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.WHEATEN, 1))
				.product(0.2f, honeyDrop)
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.8f, new ItemStack(Items.WHEAT))
				.build(consumer, anonymous());

		// Mossy Combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MOSSY, 1))
				.product(1.0f, CoreItems.BEESWAX.stack())
				.product(0.9f, honeyDrop)
				.build(consumer, anonymous());

		// Mellow Combs
		new CentrifugeRecipeBuilder()
				.setProcessingTime(20)
				.setInput(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MELLOW, 1))
				.product(0.6f, ApicultureItems.HONEYDEW.stack())
				.product(0.2f, CoreItems.BEESWAX.stack())
				.product(0.3f, new ItemStack(Items.QUARTZ))
				.build(consumer, anonymous());

		// Silky Propolis
		new CentrifugeRecipeBuilder()
				.setProcessingTime(5)
				.setInput(ApicultureItems.PROPOLIS.stack(EnumPropolis.SILKY, 1))
				.product(0.6f, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1))
				.product(0.1f, ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL, 1))
				.build(consumer, anonymous());
	}

	private void registerFabricatorSmelting(Consumer<IFinishedRecipe> consumer) {
		FluidStack liquidGlassBucket = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME);
		FluidStack liquidGlassX4 = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME * 4);
		FluidStack liquidGlass375 = ForestryFluids.GLASS.getFluid(375);

		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.GLASS))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(1000)
				.build(consumer, anonymous());
		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.GLASS_PANE))
				.setProduct(liquidGlass375)
				.setMeltingPoint(1000)
				.build(consumer, anonymous());
		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.SAND))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(3000)
				.build(consumer, anonymous());
		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.RED_SAND))
				.setProduct(liquidGlassBucket)
				.setMeltingPoint(3000)
				.build(consumer, anonymous());
		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.SANDSTONE))
				.setProduct(liquidGlassX4)
				.setMeltingPoint(4800)
				.build(consumer, anonymous());
		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.SMOOTH_SANDSTONE))
				.setProduct(liquidGlassX4)
				.setMeltingPoint(4800)
				.build(consumer, anonymous());
		new FabricatorSmeltingRecipeBuilder()
				.setResource(new ItemStack(Blocks.CHISELED_SANDSTONE))
				.setProduct(liquidGlassX4)
				.setMeltingPoint(4800)
				.build(consumer, anonymous());
	}

	private void registerFermenter(Consumer<IFinishedRecipe> consumer) {
		ForestryAPI.activeMode = new GameMode("EASY");

		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.BROWN_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.HONEY.getFluid(1))
				.build(consumer, anonymous());
		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.BROWN_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.JUICE.getFluid(1))
				.build(consumer, anonymous());
		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.RED_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.HONEY.getFluid(1))
				.build(consumer, anonymous());
		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.RED_MUSHROOM))
				.setFermentationValue(ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"))
				.setModifier(1.5f)
				.setOutput(ForestryFluids.BIOMASS.getFluid())
				.setFluidResource(ForestryFluids.JUICE.getFluid(1))
				.build(consumer, anonymous());

		FluidStack shortMead = ForestryFluids.SHORT_MEAD.getFluid(1);
		FluidStack honey = ForestryFluids.HONEY.getFluid(1);

		new FermenterRecipeBuilder()
				.setResource(Ingredient.fromItems(ApicultureItems.HONEYDEW))
				.setFermentationValue(500)
				.setModifier(1.0f)
				.setOutput(shortMead.getFluid())
				.setFluidResource(honey)
				.build(consumer, anonymous());
	}

	private void registerMoistener(Consumer<IFinishedRecipe> consumer) {
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Items.WHEAT_SEEDS))
				.setProduct(new ItemStack(Blocks.MYCELIUM))
				.setTimePerItem(5000)
				.build(consumer, anonymous());
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.COBBLESTONE))
				.setProduct(new ItemStack(Blocks.MOSSY_COBBLESTONE))
				.setTimePerItem(20000)
				.build(consumer, anonymous());
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.STONE_BRICKS))
				.setProduct(new ItemStack(Blocks.MOSSY_STONE_BRICKS))
				.setTimePerItem(20000)
				.build(consumer, anonymous());
		new MoistenerRecipeBuilder()
				.setResource(Ingredient.fromItems(Blocks.SPRUCE_LEAVES))
				.setProduct(new ItemStack(Blocks.PODZOL))
				.setTimePerItem(5000)
				.build(consumer, anonymous());
	}

	private void registerSqueezerContainer(Consumer<IFinishedRecipe> consumer) {
		new SqueezerContainerRecipeBuilder()
				.setProcessingTime(10)
				.setEmptyContainer(FluidsItems.CONTAINERS.stack(EnumContainerType.CAN))
				.setRemnants(CoreItems.INGOT_TIN.stack())
				.setRemnantsChance(0.05f)
				.build(consumer, anonymous());
		new SqueezerContainerRecipeBuilder()
				.setProcessingTime(10)
				.setEmptyContainer(FluidsItems.CONTAINERS.stack(EnumContainerType.CAPSULE))
				.setRemnants(CoreItems.BEESWAX.stack())
				.setRemnantsChance(0.10f)
				.build(consumer, anonymous());
		new SqueezerContainerRecipeBuilder()
				.setProcessingTime(10)
				.setEmptyContainer(FluidsItems.CONTAINERS.stack(EnumContainerType.REFRACTORY))
				.setRemnants(CoreItems.REFRACTORY_WAX.stack())
				.setRemnantsChance(0.10f)
				.build(consumer, anonymous());
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
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(ApicultureItems.HONEYDEW)))
				.setFluidOutput(honeyDropFluid)
				.build(consumer, anonymous());

		ItemStack phosphor = CoreItems.PHOSPHOR.stack(2);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.fromStacks(phosphor));
					ingredients.add(Ingredient.fromItems(Blocks.SAND));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 2000))
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.fromStacks(phosphor));
					ingredients.add(Ingredient.fromItems(Blocks.RED_SAND));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 2000))
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.fromStacks(phosphor));
					ingredients.add(Ingredient.fromItems(Blocks.DIRT));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 1600))
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), (ingredients) -> {
					ingredients.add(Ingredient.fromStacks(phosphor));
					ingredients.add(Ingredient.fromItems(Blocks.COBBLESTONE));
				}))
				.setFluidOutput(new FluidStack(Fluids.LAVA, 1600))
				.build(consumer, anonymous());

		int seedOilAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = ForestryFluids.SEED_OIL.getFluid(seedOilAmount);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.WHEAT_SEEDS)))
				.setFluidOutput(seedOil)
				.build(consumer, anonymous());
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.PUMPKIN_SEEDS)))
				.setFluidOutput(seedOil)
				.build(consumer, anonymous());
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.MELON_SEEDS)))
				.setFluidOutput(seedOil)
				.build(consumer, anonymous());
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.BEETROOT_SEEDS)))
				.setFluidOutput(seedOil)
				.build(consumer, anonymous());

		int appleMulchAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		FluidStack appleJuice = ForestryFluids.JUICE.getFluid(appleJuiceAmount);

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.APPLE)))
				.setFluidOutput(appleJuice)
				.setRemnants(CoreItems.MULCH.stack())
				.setRemnantsChance(appleMulchAmount / 100f)
				.build(consumer, anonymous());
		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Items.CARROT)))
				.setFluidOutput(appleJuice)
				.setRemnants(CoreItems.MULCH.stack())
				.setRemnantsChance(appleMulchAmount / 100f)
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(NonNullList.withSize(1, Ingredient.fromItems(Blocks.CACTUS)))
				.setFluidOutput(new FluidStack(Fluids.WATER, 500))
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(10)
				.setResources(Util.make(NonNullList.create(), ingredients -> {
					ingredients.add(Ingredient.fromItems(Items.SNOWBALL));
					ingredients.add(Ingredient.fromStacks(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.ICE_SHARD, 4)));
				}))
				.setFluidOutput(ForestryFluids.ICE.getFluid(4000))
				.build(consumer, anonymous());

		new SqueezerRecipeBuilder()
				.setProcessingTime(8)
				.setResources(NonNullList.withSize(1, Ingredient.fromStacks(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.CAMOUFLAGED_PANELING))))
				.setFluidOutput(ForestryFluids.BIOMASS.getFluid(150))
				.build(consumer, anonymous());
	}

	private void registerStill(Consumer<IFinishedRecipe> consumer) {
		FluidStack biomass = ForestryFluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT);
		FluidStack ethanol = ForestryFluids.BIO_ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT);

		new StillRecipeBuilder()
				.setTimePerUnit(Constants.STILL_DESTILLATION_DURATION)
				.setInput(biomass)
				.setOutput(ethanol)
				.build(consumer, anonymous());
	}
}
