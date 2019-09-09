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
package forestry.core.proxy;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import net.minecraftforge.common.MinecraftForge;

import forestry.core.TickHandlerCoreServer;
import forestry.core.multiblock.MultiblockServerTickHandler;
import forestry.core.worldgen.WorldGenerator;

public class ProxyCommon {
	public void registerItem(Item item) {

	}

	public void registerBlock(Block block) {

	}

	public void registerTickHandlers(WorldGenerator worldGenerator) {
		TickHandlerCoreServer tickHandlerCoreServer = new TickHandlerCoreServer(worldGenerator);
		MinecraftForge.EVENT_BUS.register(tickHandlerCoreServer);

		MultiblockServerTickHandler multiblockServerTickHandler = new MultiblockServerTickHandler();
		MinecraftForge.EVENT_BUS.register(multiblockServerTickHandler);
	}

	public void registerEventHandlers() {
	}

	public File getForestryRoot() {
		return new File(".");
	}

	public double getBlockReachDistance(PlayerEntity PlayerEntity) {
		return 4f;
	}

}
