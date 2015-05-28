/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes.proxy;

import java.lang.reflect.Constructor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.IItemRenderer;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.pipes.PipeItemsPropolis;
import forestry.plugins.PluginPropolisPipe;

import buildcraft.BuildCraftTransport;
import buildcraft.core.BCCreativeTab;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.TransportProxy;

public class ProxyPipes {

	public void registerCustomItemRenderer(Item item, IItemRenderer basemod) {
	}

	public void initPropolisPipe() {
		PluginPropolisPipe.pipeItemsPropolis = createPipe(PipeItemsPropolis.class, BCCreativeTab.get("pipes"));
	}

	@SuppressWarnings("rawtypes")
	public Item createPipe(Class<? extends Pipe> clas, BCCreativeTab creativeTab) {

		return registerPipe(clas, creativeTab);
	}

	/**
	 * Overriding the method in BlockGenericPipe to fix localizations.
	 */
	@SuppressWarnings("rawtypes")
	public static ItemPipe registerPipe(Class<? extends Pipe> clas, BCCreativeTab creativeTab) {
		ItemPipe item = null;
		try {
			Constructor<ItemPipe> ctor = ItemPipe.class.getDeclaredConstructor(BCCreativeTab.class);
			ctor.setAccessible(true);
			item = ctor.newInstance(creativeTab);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert item != null : "The ItemPipe instance must not be null";
		item.setUnlocalizedName(clas.getSimpleName());
		GameRegistry.registerItem(item, item.getUnlocalizedName());

		BlockGenericPipe.pipes.put(item, clas);

		Pipe dummyPipe = BlockGenericPipe.createPipe(item);
		if (dummyPipe != null) {
			item.setPipeIconIndex(dummyPipe.getIconIndexForItem());
			TransportProxy.proxy.setIconProviderFromPipe(item, dummyPipe);
		}

		return item;
	}

	public void registerCraftingPropolis(ItemStack resource) {
		GameRegistry.addRecipe(new ItemStack(PluginPropolisPipe.pipeItemsPropolis), "#X#", '#', resource, 'X', BuildCraftTransport.pipeItemsDiamond);

	}
}
