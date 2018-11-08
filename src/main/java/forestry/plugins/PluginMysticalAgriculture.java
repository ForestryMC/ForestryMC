package forestry.plugins;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Log;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.MAGICAL_AGRICULTURE, name = "Mystical Agriculture", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.mysticalagriculture.description")
public class PluginMysticalAgriculture extends CompatPlugin {
	private static final String MAGICAL_AGRICULTURE = "mysticalagriculture";

	public PluginMysticalAgriculture() {
		super("Mystical Agriculture", MAGICAL_AGRICULTURE);
	}

	@Override
	public void registerRecipes() {
		if (ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			ImmutableList<String> cropNames = getCropNames();
			if (cropNames.isEmpty()) {
				return;
			}
			IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
			int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			FluidStack seedOil = Fluids.SEED_OIL.getFluid(seedAmount);
			for (String cropName : cropNames) {
				ItemStack seeds = getItemStack(cropName + "_seeds");
				Block block = getBlock(cropName + "_crop");
				if (seeds != null && seedOil != null) {
					RecipeManagers.squeezerManager.addRecipe(10, seeds, seedOil);
				}
				if (seeds != null && block != null) {
					farmRegistry.registerFarmables(ForestryFarmIdentifier.CROPS, new FarmableAgingCrop(seeds, block, BlockCrops.AGE, 7, 0));
				}
			}
			for (int i = 1; i <= 5; i++) {
				ItemStack seeds = getItemStack("tier" + i + "_inferium_seeds");
				Block block = getBlock("tier" + i + "_inferium_crop");
				if (seeds != null && seedOil != null) {
					RecipeManagers.squeezerManager.addRecipe(10, seeds, seedOil);
				}
				if (seeds != null && block != null) {
					farmRegistry.registerFarmables(ForestryFarmIdentifier.CROPS, new FarmableAgingCrop(seeds, block, BlockCrops.AGE, 7, 0));
				}
			}
		}
	}

	private ImmutableList<String> getCropNames() {
		try {
			Class<?> typeEnum = Class.forName("com.blakebr0.mysticalagriculture.lib.CropType$Type");
			if (typeEnum.getEnumConstants() == null) {
				return ImmutableList.of();
			}
			ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
			for (Object obj : typeEnum.getEnumConstants()) {
				if (obj instanceof IStringSerializable) {
					builder.add(((IStringSerializable) obj).getName());
				}
			}
			return builder.build();
		} catch (ClassNotFoundException e) {
			Log.error("Failed to find the class ('com.blakebr0.mysticalagriculture.lib.CropType$Type') that contains the crop types of 'Mystical Agriculture'.");
			return ImmutableList.of();
		}
	}
}
