package forestry.farming.compat;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import forestry.farming.ModuleFarming;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class FarmingJeiPlugin implements IModPlugin {
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
