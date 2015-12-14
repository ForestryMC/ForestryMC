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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

import forestry.core.TickHandlerCoreServer;
import forestry.core.multiblock.MultiblockServerTickHandler;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.worldgen.WorldGenerator;

public class ProxyCommon {

	public String getMinecraftVersion() {
		return Loader.instance().getMinecraftModContainer().getVersion();
	}

	public void registerTickHandlers(WorldGenerator worldGenerator) {
		TickHandlerCoreServer tickHandlerCoreServer = new TickHandlerCoreServer(worldGenerator);
		FMLCommonHandler.instance().bus().register(tickHandlerCoreServer);
		MinecraftForge.EVENT_BUS.register(tickHandlerCoreServer);

		FMLCommonHandler.instance().bus().register(new MultiblockServerTickHandler());
	}

	public String getDisplayName(ItemStack itemstack) {
		return null;
	}

	public File getForestryRoot() {
		return new File(".");
	}

	public boolean isOp(EntityPlayer player) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getConfigurationManager().func_152596_g(player.getGameProfile());
	}

	public double getBlockReachDistance(EntityPlayer entityplayer) {
		return 4f;
	}

	public boolean isShiftDown() {
		return false;
	}

	public void playSoundFX(World world, int x, int y, int z, Block block) {
		Proxies.net.sendNetworkPacket(new PacketFXSignal(PacketFXSignal.SoundFXType.LEAF, x, y, z, block, 0), world);
	}

	public void playSoundFX(World world, int x, int y, int z, String sound, float volume, float pitch) {
	}

	public void addBlockDestroyEffects(World world, int xCoord, int yCoord, int zCoord, Block block, int i) {
		sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.BLOCK_DESTROY, world, xCoord, yCoord, zCoord, block, i);
	}

	public void addBlockPlaceEffects(World world, int xCoord, int yCoord, int zCoord, Block block, int i) {
		sendFXSignal(PacketFXSignal.VisualFXType.NONE, PacketFXSignal.SoundFXType.BLOCK_PLACE, world, xCoord, yCoord, zCoord, block, i);
	}

	public void playBlockBreakSoundFX(World world, int x, int y, int z, Block block) {
	}

	public void playBlockPlaceSoundFX(World world, int x, int y, int z, Block block) {
	}

	public void sendFXSignal(PacketFXSignal.VisualFXType visualFX, PacketFXSignal.SoundFXType soundFX, World world, int xCoord, int yCoord, int zCoord,
			Block block, int i) {
		if (!world.isRemote) {
			Proxies.net.sendNetworkPacket(new PacketFXSignal(visualFX, soundFX, xCoord, yCoord, zCoord, block, i), world);
		}
	}

	public World getRenderWorld() {
		return null;
	}

	public Minecraft getClientInstance() {
		return FMLClientHandler.instance().getClient();
	}

	public EntityPlayer getPlayer() {
		return null;
	}

}
