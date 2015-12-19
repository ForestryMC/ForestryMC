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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
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

	private IBlockState state;

	public PacketFXSignal() {
	}

	public PacketFXSignal(VisualFXType type, BlockPos pos, IBlockState state) {
		this(type, SoundFXType.NONE, pos, state);
	}

	public PacketFXSignal(SoundFXType type, BlockPos pos, IBlockState state) {
		this(VisualFXType.NONE, type, pos, state);
	}

	public PacketFXSignal(VisualFXType visualFX, SoundFXType soundFX, BlockPos pos, IBlockState state) {
		super(pos);
		this.visualFX = visualFX;
		this.soundFX = soundFX;
		this.state = state;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeShort(visualFX.ordinal());
		data.writeShort(soundFX.ordinal());
		data.writeUTF(GameData.getBlockRegistry().getNameForObject(state.getBlock()).toString());
		data.writeInt(state.getBlock().getMetaFromState(state));
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		this.visualFX = VisualFXType.values()[data.readShort()];
		this.soundFX = SoundFXType.values()[data.readShort()];
		this.state = GameData.getBlockRegistry().getRaw(GameData.getBlockRegistry().getId(new ResourceLocation(data.readUTF()))).getStateFromMeta(data.readInt());
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		if (visualFX != VisualFXType.NONE) {
			Proxies.common.addBlockDestroyEffects(Proxies.common.getRenderWorld(), getPos(), state);
		}
		if (soundFX != SoundFXType.NONE) {
			if (soundFX == SoundFXType.BLOCK_DESTROY) {
				Proxies.common.playBlockBreakSoundFX(Proxies.common.getRenderWorld(), getPos(), state);
			} else if (soundFX == SoundFXType.BLOCK_PLACE) {
				Proxies.common.playBlockPlaceSoundFX(Proxies.common.getRenderWorld(), getPos(), state);
			} else {
				Proxies.common.playSoundFX(Proxies.common.getRenderWorld(), getPos(), soundFX.soundFile, soundFX.volume, soundFX.pitch);
			}
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.FX_SIGNAL;
	}
}
