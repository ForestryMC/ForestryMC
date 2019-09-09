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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import forestry.core.ClimateHandlerClient;
import forestry.core.TickHandlerCoreClient;
import forestry.core.models.ModelManager;
import forestry.core.multiblock.MultiblockClientTickHandler;
import forestry.core.multiblock.MultiblockEventHandlerClient;
import forestry.core.render.TextureManagerForestry;
import forestry.core.worldgen.WorldGenerator;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyClient extends ProxyCommon {

	@Override
	public void registerTickHandlers(WorldGenerator worldGenerator) {
		super.registerTickHandlers(worldGenerator);
		MinecraftForge.EVENT_BUS.register(new TickHandlerCoreClient());
		MinecraftForge.EVENT_BUS.register(new MultiblockClientTickHandler());
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandlerClient());
		MinecraftForge.EVENT_BUS.register(new ClimateHandlerClient());
	}

	@Override
	public void registerBlock(Block block) {
		ModelManager.getInstance().registerBlockClient(block);
		if (Minecraft.getInstance() != null) {
			TextureManagerForestry.getInstance().registerBlock(block);
		}
	}

	@Override
	public void registerItem(Item item) {
		ModelManager.getInstance().registerItemClient(item);
		if (Minecraft.getInstance() != null) {
			TextureManagerForestry.getInstance().registerItem(item);
		}
	}

	@Override
	public File getForestryRoot() {
		return Minecraft.getInstance().gameDir;
	}

	@Override
	public double getBlockReachDistance(PlayerEntity PlayerEntity) {
		if (PlayerEntity instanceof ClientPlayerEntity) {
			return Minecraft.getInstance().playerController.getBlockReachDistance();
		} else {
			return 4f;
		}
	}

}
