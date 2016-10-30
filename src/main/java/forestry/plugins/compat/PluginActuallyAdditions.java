package forestry.plugins.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@ForestryPlugin(pluginID = ForestryPluginUids.ACT_ADD, name = "Actually Additions", author = "Ellpeck", url = "http://ellpeck.de/actadd", unlocalizedDescription = "for.plugin.actuallyadditions.description")
public class PluginActuallyAdditions extends BlankForestryPlugin {

	private static final String ACT_ADD = "actuallyadditions";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(ACT_ADD);
	}

	@Override
	public String getFailMessage() {
		return "Actually Additions not found!";
	}

	@Override
	public void registerRecipes() {
		Item canolaSeed = getItem("itemCanolaSeed");
		Item flaxSeed = getItem("itemFlaxSeed");
		Item riceSeed = getItem("itemRiceSeed");
		Item coffeeSeed = getItem("itemCoffeeSeed");

		//add farm seed planting
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			registerSeedPlant(canolaSeed, "blockCanola");
			registerSeedPlant(flaxSeed, "blockFlax");
			registerSeedPlant(riceSeed, "blockRice");
			registerSeedPlant(coffeeSeed, "blockCoffee");
		}

		//add seed squeezing
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedFluid = Fluids.SEED_OIL.getFluid(amount);
		for (Item seed : Arrays.asList(canolaSeed, flaxSeed, riceSeed, coffeeSeed)) {
			if (seed != null) {
				ItemStack[] resources = {new ItemStack(seed)};
				RecipeManagers.squeezerManager.addRecipe(10, resources, seedFluid);
			}
		}

		Fluid canolaOil = getFluid("canolaoil");
		if (canolaOil != null) {
			//add canola squeezing to canola oil
			Item misc = getItem("itemMisc");
			if (misc != null) {
				ItemStack canola = new ItemStack(misc, 1, 13);
				RecipeManagers.squeezerManager.addRecipe(15, new ItemStack[]{canola}, new FluidStack(canolaOil, 80));
			}

			//add canola oil fermenting in still
			Fluid oil = getFluid("oil");
			if (oil != null) {
				RecipeManagers.stillManager.addRecipe(200, new FluidStack(canolaOil, 5), new FluidStack(oil, 5));
			}
		}
	}

	private static void registerSeedPlant(@Nullable Item seedItem, @Nonnull String blockName) {
		if (seedItem != null) {
			Block plantBlock = getBlock(blockName);
			if (plantBlock != null) {
				FarmableAgingCrop crop = new FarmableAgingCrop(new ItemStack(seedItem), plantBlock, BlockCrops.AGE, 7);
				Farmables.farmables.get("farmWheat").add(crop);
			}
		}
	}

	@Nullable
	private static Fluid getFluid(@Nonnull String fluidName) {
		Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid == null) {
			Log.error("Could not find fluid {}", fluidName);
		}
		return fluid;
	}

	@Nullable
	private static Block getBlock(@Nonnull String blockName) {
		ResourceLocation key = new ResourceLocation(ACT_ADD, blockName);
		if (ForgeRegistries.BLOCKS.containsKey(key)) {
			return ForgeRegistries.BLOCKS.getValue(key);
		} else {
			Log.error("Could not find {}", key);
			return null;
		}
	}

	@Nullable
	private static Item getItem(@Nonnull String itemName) {
		ResourceLocation key = new ResourceLocation(ACT_ADD, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return ForgeRegistries.ITEMS.getValue(key);
		} else {
			Log.error("Could not find {}", key);
			return null;
		}
	}
}
