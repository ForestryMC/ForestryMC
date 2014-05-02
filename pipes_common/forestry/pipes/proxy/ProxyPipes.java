/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes.proxy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import buildcraft.BuildCraftTransport;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.pipes.PipeItemsPropolis;
import forestry.plugins.PluginPropolisPipe;

public class ProxyPipes {

	public void registerCustomItemRenderer(int itemID, IItemRenderer basemod) {
	}

	public void initPropolisPipe() {
		PluginPropolisPipe.pipeItemsPropolis = createPipe(PluginPropolisPipe.propolisPipeItemId, PipeItemsPropolis.class, "Apiarist's Pipe");
	}

	public Item createPipe(int id, Class<? extends Pipe> clas, String description) {

		Item pipe = BlockGenericPipe.registerPipe(id, clas);
		pipe.setUnlocalizedName(clas.getSimpleName());

		return pipe;
	}

	public void registerCraftingPropolis(ItemStack resource) {
		GameRegistry.addRecipe(new ItemStack(PluginPropolisPipe.pipeItemsPropolis),
				new Object[] { "#X#", Character.valueOf('#'), resource, Character.valueOf('X'), BuildCraftTransport.pipeItemsDiamond });

	}

	public void addLocalizations() {
	}
}
