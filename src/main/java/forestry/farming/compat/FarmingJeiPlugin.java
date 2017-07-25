package forestry.farming.compat;

import com.google.common.base.Preconditions;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.ForestryAPI;
import forestry.farming.PluginFarming;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.plugins.ForestryPluginUids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class FarmingJeiPlugin implements IModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		if (!ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			return;
		}
		
		BlockRegistryFarming blocks = PluginFarming.getBlocks();
		Item farmBlock = Item.getItemFromBlock(blocks.farm);
		subtypeRegistry.registerSubtypeInterpreter(farmBlock, itemStack -> {
			NBTTagCompound nbt = itemStack.getTagCompound();
			EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(nbt);
			return itemStack.getItemDamage() + "." + texture.getUid();
		});
	}
}
