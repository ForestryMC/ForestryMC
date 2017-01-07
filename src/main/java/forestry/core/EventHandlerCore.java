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

import forestry.core.config.Constants;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.loot.LootTableLoader;
import forestry.core.models.ModelBlockCached;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.packets.PacketGenomeTrackerRequest;
import forestry.core.render.TextureManager;
import forestry.plugins.PluginManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandlerCore {

	public EventHandlerCore() {
	}

	@SubscribeEvent
	public void handleItemPickup(EntityItemPickupEvent event) {

		if (event.isCanceled() || event.getResult() == Result.ALLOW) {
			return;
		}

		for (IPickupHandler handler : PluginManager.pickupHandlers) {
			if (handler.onItemPickup(event.getEntityPlayer(), event.getItem())) {
				event.setResult(Result.ALLOW);
				return;
			}
		}
	}

	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();

		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldLoad(world);
		}

		if (world.isRemote) {
			IForestryPacketServer packet = new PacketGenomeTrackerRequest();
			world.sendPacketToServer(packet.getPacket());
		}
	}

	@SubscribeEvent
	public void handleWorldSave(WorldEvent.Save event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldSave(event.getWorld());
		}
	}

	@SubscribeEvent
	public void handleWorldUnload(WorldEvent.Unload event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldUnload(event.getWorld());
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		ErrorStateRegistry.initSprites();
		TextureManager.initDefaultSprites();
		ModelBlockCached.clear();
	}

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent event) {
		if (!event.getName().getResourceDomain().equals("minecraft")) {
			return;
		}

		ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, event.getName().getResourcePath());
		LootTable forestryChestAdditions = LootTableLoader.loadBuiltinLootTable(resourceLocation);
		if (forestryChestAdditions != null) {
			for (String poolName : PluginManager.getLootPoolNames()) {
				LootPool pool = forestryChestAdditions.getPool(poolName);
				if (pool != null) {
					event.getTable().addPool(pool);
				}
			}
		}
	}
}
