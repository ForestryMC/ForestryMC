package forestry.farming.compat;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import forestry.core.ModuleCore;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.farming.ModuleFarming;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class FarmingJeiPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			return;
		}

		registry.addRecipes(FarmingInfoRecipeMaker.getRecipes(), FarmingInfoRecipeCategory.UID);

		registry.addRecipeCatalyst(ModuleCore.getItems().circuitboards.get(EnumCircuitBoardType.INTRICATE), FarmingInfoRecipeCategory.UID);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			return;
		}

		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new FarmingInfoRecipeCategory(guiHelper));
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			return;
		}

		BlockRegistryFarming blocks = ModuleFarming.getBlocks();
		Item farmBlock = Item.getItemFromBlock(blocks.farm);
		subtypeRegistry.registerSubtypeInterpreter(farmBlock, itemStack -> {
			NBTTagCompound nbt = itemStack.getTagCompound();
			EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(nbt);
			return itemStack.getItemDamage() + "." + texture.getUid();
		});
	}
}
