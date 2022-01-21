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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	private final BlockState blockState;

	public PacketFXSignal(VisualFXType type, BlockPos pos, BlockState blockState) {
		this(type, SoundFXType.NONE, pos, blockState);
	}

	public PacketFXSignal(SoundFXType type, BlockPos pos, BlockState blockState) {
		this(VisualFXType.NONE, type, pos, blockState);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, BlockPos pos, BlockState blockState) {
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
		CompoundTag tag = NbtUtils.writeBlockState(blockState);
		data.writeNbt(tag);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.FX_SIGNAL;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, Player player) throws IOException {
			BlockPos pos = data.readBlockPos();
			VisualFXType visualFX = VisualFXType.values()[data.readByte()];
			SoundFXType soundFX = SoundFXType.values()[data.readByte()];
			Level world = player.level;
			BlockState blockState = NbtUtils.readBlockState(data.readNbt());
			Block block = blockState.getBlock();

			if (visualFX == VisualFXType.BLOCK_BREAK) {
				Minecraft.getInstance().particleEngine.destroy(pos, blockState);
			}

			if (soundFX != SoundFXType.NONE) {
				SoundType soundType = block.getSoundType(blockState, world, pos, null);

				if (soundFX == SoundFXType.BLOCK_BREAK) {
					world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
				} else if (soundFX == SoundFXType.BLOCK_PLACE) {
					world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
				}
			}
		}
	}
}
