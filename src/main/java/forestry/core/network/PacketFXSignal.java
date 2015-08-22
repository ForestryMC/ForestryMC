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
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.registry.GameData;

import forestry.core.proxy.Proxies;

public class PacketFXSignal extends ForestryPacket {

	public static enum VisualFXType {
		NONE, BLOCK_DESTROY, SAPLING_PLACE
	}

	public static enum SoundFXType {
		NONE(""), BLOCK_DESTROY(""), BLOCK_PLACE(""), LEAF("step.grass"), LOG("dig.wood"), DIRT("dig.gravel");

		public final String soundFile;
		public final float volume = 1.0f;
		public final float pitch = 1.0f;

		private SoundFXType(String soundFile) {
			this.soundFile = soundFile;
		}
	}

	private VisualFXType visualFX;
	private SoundFXType soundFX;

	private BlockPos pos;
	private Block block;
	private int meta;

	public PacketFXSignal() {
	}

	public PacketFXSignal(VisualFXType type, BlockPos pos, Block block, int meta) {
		this(type, SoundFXType.NONE, pos, block, meta);
	}

	public PacketFXSignal(SoundFXType type, BlockPos pos, Block block, int meta) {
		this(VisualFXType.NONE, type, pos, block, meta);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, BlockPos pos, Block block, int meta) {
		super(PacketIds.FX_SIGNAL);
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.pos = pos;
		this.block = block;
		this.meta = meta;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeShort(visualFX.ordinal());
		data.writeShort(soundFX.ordinal());
		data.writeInt(pos.getX());
		data.writeInt(pos.getY());
		data.writeInt(pos.getZ());
		data.writeUTF((String)GameData.getBlockRegistry().getNameForObject(block));
		data.writeInt(meta);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.visualFX = VisualFXType.values()[data.readShort()];
		this.soundFX = SoundFXType.values()[data.readShort()];
		int xCoord = data.readInt();
		int yCoord = data.readInt();
		int zCoord = data.readInt();
		pos = new BlockPos(xCoord, yCoord, zCoord);
		this.block = GameData.getBlockRegistry().getRaw(data.readUTF());
		this.meta = data.readInt();
	}

	public void executeFX() {
		if (visualFX != VisualFXType.NONE) {
			Proxies.common.addBlockDestroyEffects(Proxies.common.getRenderWorld(), pos, block, meta);
		}
		if (soundFX != SoundFXType.NONE) {
			if (soundFX == SoundFXType.BLOCK_DESTROY) {
				Proxies.common.playBlockBreakSoundFX(Proxies.common.getRenderWorld(), pos, block);
			} else if (soundFX == SoundFXType.BLOCK_PLACE) {
				Proxies.common.playBlockPlaceSoundFX(Proxies.common.getRenderWorld(), pos, block);
			} else {
				Proxies.common.playSoundFX(Proxies.common.getRenderWorld(), pos, soundFX.soundFile, soundFX.volume, soundFX.pitch);
			}
		}
	}

}
