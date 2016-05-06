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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import forestry.core.TickHandlerCoreServer;
import forestry.core.multiblock.MultiblockServerTickHandler;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.worldgen.WorldGenerator;

public class ProxyCommon {

	public String getMinecraftVersion() {
		return Loader.instance().getMinecraftModContainer().getVersion();
	}
	
	public void registerItem(Item item){
		
	}
	
	public void registerBlock(Block block){
		
	}

	public void registerTickHandlers(WorldGenerator worldGenerator) {
		TickHandlerCoreServer tickHandlerCoreServer = new TickHandlerCoreServer(worldGenerator);
		MinecraftForge.EVENT_BUS.register(tickHandlerCoreServer);

		MultiblockServerTickHandler multiblockServerTickHandler = new MultiblockServerTickHandler();
		MinecraftForge.EVENT_BUS.register(multiblockServerTickHandler);
	}

	public String getDisplayName(ItemStack itemstack) {
		return null;
	}

	public File getForestryRoot() {
		return new File(".");
	}

	public boolean isOp(EntityPlayer player) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		UserListOpsEntry userlistopsentry = server.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
		return userlistopsentry.getPermissionLevel() >= server.getOpPermissionLevel();
	}

	public double getBlockReachDistance(EntityPlayer entityplayer) {
		return 4f;
	}

	public boolean isShiftDown() {
		return false;
	}

	public void playSoundFX(World world, BlockPos pos, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
	}

	public void addBlockDestroyEffects(World world, BlockPos pos, IBlockState state) {
		sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.BLOCK_DESTROY, world, pos, state);
	}

	public void addBlockPlaceEffects(World world, BlockPos pos, IBlockState state) {
		sendFXSignal(PacketFXSignal.VisualFXType.NONE, PacketFXSignal.SoundFXType.BLOCK_PLACE, world, pos, state);
	}

	public void playBlockBreakSoundFX(World world, BlockPos pos, IBlockState state) {
	}

	public void playBlockPlaceSoundFX(World world, BlockPos pos, IBlockState state) {
	}

	public void sendFXSignal(PacketFXSignal.VisualFXType visualFX, PacketFXSignal.SoundFXType soundFX, World world, BlockPos pos, IBlockState state) {
		Proxies.net.sendNetworkPacket(new PacketFXSignal(visualFX, soundFX, pos, state), world);
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
