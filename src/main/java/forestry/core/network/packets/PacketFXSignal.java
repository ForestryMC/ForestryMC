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
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.registry.GameData;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;

public class PacketFXSignal extends PacketCoordinates implements IForestryPacketClient {

	public enum VisualFXType {
		NONE, BLOCK_DESTROY, SAPLING_PLACE
	}

	public enum SoundFXType {
		NONE(""), BLOCK_DESTROY(""), BLOCK_PLACE(""), LEAF("step.grass"), LOG("dig.wood"), DIRT("dig.gravel");

		public final String soundFile;
		public final float volume;
		public final float pitch;

		SoundFXType(String soundFile) {
			this.soundFile = soundFile;
			this.volume = 1.0f;
			this.pitch = 1.0f;
		}
	}

	private VisualFXType visualFX;
	private SoundFXType soundFX;

	private Block block;
	private int meta;

	public PacketFXSignal() {
	}

	public PacketFXSignal(VisualFXType type, int xCoord, int yCoord, int zCoord, Block block, int meta) {
		this(type, SoundFXType.NONE, xCoord, yCoord, zCoord, block, meta);
	}

	public PacketFXSignal(SoundFXType type, int xCoord, int yCoord, int zCoord, Block block, int meta) {
		this(VisualFXType.NONE, type, xCoord, yCoord, zCoord, block, meta);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, int xCoord, int yCoord, int zCoord, Block block, int meta) {
		super(xCoord, yCoord, zCoord);
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.block = block;
		this.meta = meta;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeShort(visualFX.ordinal());
		data.writeShort(soundFX.ordinal());
		data.writeUTF(GameData.getBlockRegistry().getNameForObject(block));
		data.writeInt(meta);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		this.visualFX = VisualFXType.values()[data.readShort()];
		this.soundFX = SoundFXType.values()[data.readShort()];
		this.block = GameData.getBlockRegistry().getRaw(data.readUTF());
		this.meta = data.readInt();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		if (visualFX != VisualFXType.NONE) {
			Proxies.common.addBlockDestroyEffects(Proxies.common.getRenderWorld(), getPosX(), getPosY(), getPosZ(), block, meta);
		}
		if (soundFX != SoundFXType.NONE) {
			if (soundFX == SoundFXType.BLOCK_DESTROY) {
				Proxies.common.playBlockBreakSoundFX(Proxies.common.getRenderWorld(), getPosX(), getPosY(), getPosZ(), block);
			} else if (soundFX == SoundFXType.BLOCK_PLACE) {
				Proxies.common.playBlockPlaceSoundFX(Proxies.common.getRenderWorld(), getPosX(), getPosY(), getPosZ(), block);
			} else {
				Proxies.common.playSoundFX(Proxies.common.getRenderWorld(), getPosX(), getPosY(), getPosZ(), soundFX.soundFile, soundFX.volume, soundFX.pitch);
			}
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.FX_SIGNAL;
	}
}
