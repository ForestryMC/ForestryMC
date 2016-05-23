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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;

public class PacketFXSignal extends PacketCoordinates implements IForestryPacketClient {

	public enum VisualFXType {
		NONE, BLOCK_BREAK, SAPLING_PLACE
	}

	public enum SoundFXType {
		NONE, BLOCK_BREAK, BLOCK_PLACE;
	}

	private VisualFXType visualFX;
	private SoundFXType soundFX;

	private IBlockState blockState;

	public PacketFXSignal() {
	}

	public PacketFXSignal(VisualFXType type, BlockPos pos, IBlockState blockState) {
		this(type, SoundFXType.NONE, pos, blockState);
	}

	public PacketFXSignal(SoundFXType type, BlockPos pos, IBlockState blockState) {
		this(VisualFXType.NONE, type, pos, blockState);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, BlockPos pos, IBlockState blockState) {
		super(pos);
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.blockState = blockState;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeByte(visualFX.ordinal());
		data.writeByte(soundFX.ordinal());
		Block block = blockState.getBlock();
		data.writeVarInt(Block.getIdFromBlock(block));
		data.writeVarInt(block.getMetaFromState(blockState));
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		this.visualFX = VisualFXType.values()[data.readByte()];
		this.soundFX = SoundFXType.values()[data.readByte()];
		Block block = Block.getBlockById(data.readVarInt());
		this.blockState = block.getStateFromMeta(data.readVarInt());
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		World world = player.getEntityWorld();
		BlockPos pos = getPos();

		if (visualFX == VisualFXType.BLOCK_BREAK) {
			Proxies.common.addBlockDestroyEffects(world, pos, blockState);
		}

		if (soundFX != SoundFXType.NONE) {
			Block block = blockState.getBlock();
			SoundType soundType = block.getSoundType();

			if (soundFX == SoundFXType.BLOCK_BREAK) {
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
			} else if (soundFX == SoundFXType.BLOCK_PLACE) {
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
			}
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.FX_SIGNAL;
	}
}
