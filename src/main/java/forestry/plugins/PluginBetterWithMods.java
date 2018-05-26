package forestry.plugins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableDoubleCrop;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BETTER_WITH_MODS, name = "Better With Mods", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.betterwithmods.description")
public class PluginBetterWithMods extends CompatPlugin {
	public PluginBetterWithMods() {
		super("Better With Mods", "betterwithmods");
	}

	@Override
	public void registerRecipes() {
		IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
		int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		PropertyBool TOP = PropertyBool.create("top");
		PropertyInteger AGE = BlockCrops.AGE;
		ItemStack hempSeed = getItemStack("hemp");
		Block hempCrop = getBlock("hemp");

		if (hempSeed != null && hempCrop != null) {
			IBlockState defaultState = hempCrop.getDefaultState();
			IBlockState planted = defaultState.withProperty(AGE, 0).withProperty(TOP, false);
			IBlockState mature = defaultState.withProperty(AGE, 7).withProperty(TOP, false);
			IBlockState topMature = defaultState.withProperty(AGE, 7).withProperty(TOP, true);

			farmRegistry.registerFarmables(ForestryFarmIdentifier.CROPS, new FarmableDoubleCrop(hempSeed, planted, mature, topMature, true));

			RecipeManagers.squeezerManager.addRecipe(10, hempSeed, Fluids.SEED_OIL.getFluid(seedAmount));
		}
	}
}
