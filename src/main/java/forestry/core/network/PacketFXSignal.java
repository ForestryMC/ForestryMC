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

import cpw.mods.fml.common.registry.GameData;
import forestry.core.proxy.Proxies;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.block.Block;

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

	private int xCoord;
	private int yCoord;
	private int zCoord;
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
		super(PacketIds.FX_SIGNAL);
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.block = block;
		this.meta = meta;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeShort(visualFX.ordinal());
		data.writeShort(soundFX.ordinal());
		data.writeInt(xCoord);
		data.writeInt(yCoord);
		data.writeInt(zCoord);
		data.writeUTF(GameData.getBlockRegistry().getNameForObject(block));
		data.writeInt(meta);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		this.visualFX = VisualFXType.values()[data.readShort()];
		this.soundFX = SoundFXType.values()[data.readShort()];
		this.xCoord = data.readInt();
		this.yCoord = data.readInt();
		this.zCoord = data.readInt();
		this.block = GameData.getBlockRegistry().getRaw(data.readUTF());
		this.meta = data.readInt();
	}

	public void executeFX() {
		if (visualFX != VisualFXType.NONE)
			Proxies.common.addBlockDestroyEffects(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, block, meta);
		if (soundFX != SoundFXType.NONE)
			if (soundFX == SoundFXType.BLOCK_DESTROY)
				Proxies.common.playBlockBreakSoundFX(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, block);
			else if (soundFX == SoundFXType.BLOCK_PLACE)
				Proxies.common.playBlockPlaceSoundFX(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, block);
			else
				Proxies.common.playSoundFX(Proxies.common.getRenderWorld(), xCoord, yCoord, zCoord, soundFX.soundFile, soundFX.volume, soundFX.pitch);
	}

}
