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
package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketFXSignal extends ForestryPacket implements IForestryPacketClient {

	public enum VisualFXType {
		NONE, BLOCK_BREAK, SAPLING_PLACE
	}

	public enum SoundFXType {
		NONE, BLOCK_BREAK, BLOCK_PLACE
	}

	private final BlockPos pos;
	private final VisualFXType visualFX;
	private final SoundFXType soundFX;
	private final IBlockState blockState;

	public PacketFXSignal(VisualFXType type, BlockPos pos, IBlockState blockState) {
		this(type, SoundFXType.NONE, pos, blockState);
	}

	public PacketFXSignal(SoundFXType type, BlockPos pos, IBlockState blockState) {
		this(VisualFXType.NONE, type, pos, blockState);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, BlockPos pos, IBlockState blockState) {
		this.pos = pos;
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.blockState = blockState;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBlockPos(pos);
		data.writeByte(visualFX.ordinal());
		data.writeByte(soundFX.ordinal());
		NBTTagCompound tag = new NBTTagCompound();
		NBTUtil.writeBlockState(tag, blockState);
		data.writeCompoundTag(tag);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.FX_SIGNAL;
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			VisualFXType visualFX = VisualFXType.values()[data.readByte()];
			SoundFXType soundFX = SoundFXType.values()[data.readByte()];
			World world = player.world;
			IBlockState blockState = NBTUtil.readBlockState(data.readCompoundTag());
			Block block = blockState.getBlock();

			if (visualFX == VisualFXType.BLOCK_BREAK) {
				Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, blockState);
			}

			if (soundFX != SoundFXType.NONE) {
				SoundType soundType = block.getSoundType(blockState, world, pos, null);

				if (soundFX == SoundFXType.BLOCK_BREAK) {
					world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
				} else if (soundFX == SoundFXType.BLOCK_PLACE) {
					world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
				}
			}
		}
	}
}
