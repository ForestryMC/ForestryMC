/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;

import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.render.TextureManager;
import forestry.core.utils.LiquidHelper;
import forestry.plugins.PluginManager;

public class EventHandlerCore {

	public EventHandlerCore() {
		for(String name : OreDictionary.getOreNames())
			for(ItemStack ore : OreDictionary.getOres(name))
				handleOreRegistration(name, ore);
	}

	@SubscribeEvent
	public void handleItemPickup(EntityItemPickupEvent event) {

		if (event.isCanceled())
			return;

		for (IPickupHandler handler : PluginManager.pickupHandlers)
			if (!handler.onItemPickup(event.entityPlayer, event.item)) {
				event.setResult(Result.ALLOW);
				return;
			}
	}

	@SubscribeEvent
	public void handleOreRegistration(OreDictionary.OreRegisterEvent event) {

		if (event.isCanceled())
			return;

		handleOreRegistration(event.Name, event.Ore);
	}

	private void handleOreRegistration(String name, ItemStack ore) {
		for (IOreDictionaryHandler handler : PluginManager.dictionaryHandlers)
			handler.onOreRegistration(name, ore);
	}

	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers)
			handler.onWorldLoad(event.world);
	}

	@SubscribeEvent
	public void handleWorldSave(WorldEvent.Save event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers)
			handler.onWorldSave(event.world);
	}

	@SubscribeEvent
	public void handleWorldUnload(WorldEvent.Unload event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers)
			handler.onWorldUnload(event.world);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		if(event.map.getTextureType() == 1) {
			EnumErrorCode.initIcons(event.map);
			TextureManager.getInstance().initDefaultIcons(event.map);
		} else if(event.map.getTextureType() == 0)
			LiquidHelper.resetLiquidIcons(event.map);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTexturePostmap(TextureStitchEvent.Post event) {
	}

}
