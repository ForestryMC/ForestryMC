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
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import org.lwjgl.input.Keyboard;

import forestry.core.TickHandlerCoreClient;
import forestry.core.multiblock.MultiblockClientTickHandler;
import forestry.core.worldgen.WorldGenerator;

public class ProxyCommonClient extends ProxyCommon {

	@Override
	public void registerTickHandlers(WorldGenerator worldGenerator) {
		super.registerTickHandlers(worldGenerator);

		FMLCommonHandler.instance().bus().register(new TickHandlerCoreClient());
		FMLCommonHandler.instance().bus().register(new MultiblockClientTickHandler());
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
	public void playSoundFX(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if (!world.isRemote) {
			super.playSoundFX(world, pos, state);
		} else {
			playSoundFX(world, pos, block.stepSound.getStepSound(), block.stepSound.getVolume(), block.stepSound.getFrequency());
		}
	}

	@Override
	public void playBlockBreakSoundFX(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if (!world.isRemote) {
			super.playSoundFX(world, pos, state);
		} else {
			playSoundFX(world, pos, block.stepSound.getBreakSound(), block.stepSound.getVolume() / 4, block.stepSound.getFrequency());
		}
	}

	@Override
	public void playBlockPlaceSoundFX(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if (!world.isRemote) {
			super.playSoundFX(world, pos, state);
		} else {
			playSoundFX(world, pos, block.stepSound.getStepSound(), block.stepSound.getVolume() / 4, block.stepSound.getFrequency());
		}
	}

	@Override
	public void playSoundFX(World world, BlockPos pos, String sound, float volume, float pitch) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		world.playSound(x + 0.5, y + 0.5, z + 0.5, sound, volume, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f, false);
	}

	@Override
	public void addBlockDestroyEffects(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote) {
			getClientInstance().effectRenderer.addBlockDestroyEffects(pos, state);
		} else {
			super.addBlockDestroyEffects(world, pos, state);
		}
	}

	@Override
	public void addBlockPlaceEffects(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote) {
			playBlockPlaceSoundFX(world, pos, state);
		} else {
			super.addBlockPlaceEffects(world, pos, state);
		}
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}
