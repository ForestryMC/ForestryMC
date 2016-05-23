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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import forestry.core.TickHandlerCoreClient;
import forestry.core.models.ModelManager;
import forestry.core.multiblock.MultiblockClientTickHandler;
import forestry.core.render.TextureManager;
import forestry.core.worldgen.WorldGenerator;

public class ProxyClient extends ProxyCommon {

	@Override
	public void registerTickHandlers(WorldGenerator worldGenerator) {
		super.registerTickHandlers(worldGenerator);

		MinecraftForge.EVENT_BUS.register(new TickHandlerCoreClient());
		MinecraftForge.EVENT_BUS.register(new MultiblockClientTickHandler());
	}
	
	@Override
	public void registerBlock(Block block) {
		ModelManager.getInstance().registerBlockClient(block);
		TextureManager.getInstance().registerBlock(block);
	}
	
	@Override
	public void registerItem(Item item) {
		ModelManager.getInstance().registerItemClient(item);
		TextureManager.getInstance().registerItem(item);
	}

	@Override
	public File getForestryRoot() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public World getRenderWorld() {
		return getClientInstance().theWorld;
	}

	@Override
	public boolean isOp(EntityPlayer player) {
		return false;
	}

	@Override
	public double getBlockReachDistance(EntityPlayer entityplayer) {
		if (entityplayer instanceof EntityPlayerSP) {
			return getClientInstance().playerController.getBlockReachDistance();
		} else {
			return 4f;
		}
	}

	@Override
	public boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@Override
	public String getDisplayName(ItemStack itemstack) {
		return itemstack.getItem().getItemStackDisplayName(itemstack);
	}

	@Override
	public void addBlockDestroyEffects(World world, BlockPos pos, IBlockState blockState) {
		Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, blockState);
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}
