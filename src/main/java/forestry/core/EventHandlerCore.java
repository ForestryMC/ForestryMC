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
package forestry.core;

import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ErrorStateRegistry;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.render.TextureManager;
import forestry.plugins.PluginManager;

public class EventHandlerCore {

	public EventHandlerCore() {
		for (String name : OreDictionary.getOreNames()) {
			for (ItemStack ore : OreDictionary.getOres(name)) {
				handleOreRegistration(name, ore);
			}
		}
	}

	@SubscribeEvent
	public void handleItemPickup(EntityItemPickupEvent event) {

		if (event.isCanceled()) {
			return;
		}

		for (IPickupHandler handler : PluginManager.pickupHandlers) {
			if (!handler.onItemPickup(event.entityPlayer, event.item)) {
				event.setResult(Result.ALLOW);
				return;
			}
		}
	}

	@SubscribeEvent
	public void handleOreRegistration(OreDictionary.OreRegisterEvent event) {

		if (event.isCanceled()) {
			return;
		}

		handleOreRegistration(event.Name, event.Ore);
	}

	private void handleOreRegistration(String name, ItemStack ore) {
		for (IOreDictionaryHandler handler : PluginManager.dictionaryHandlers) {
			handler.onOreRegistration(name, ore);
		}
	}

	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldLoad(event.world);
		}
	}

	@SubscribeEvent
	public void handleWorldSave(WorldEvent.Save event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldSave(event.world);
		}
	}

	@SubscribeEvent
	public void handleWorldUnload(WorldEvent.Unload event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldUnload(event.world);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			ErrorStateRegistry.initIcons(event.map);
			TextureManager.getInstance().initDefaultIcons(event.map);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTexturePostmap(TextureStitchEvent.Post event) {
	}

}
