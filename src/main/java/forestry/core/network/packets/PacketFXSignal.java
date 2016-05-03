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
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;

public class PacketFXSignal extends PacketCoordinates implements IForestryPacketClient {

	public enum VisualFXType {
		NONE, BLOCK_DESTROY, SAPLING_PLACE
	}

	public enum SoundFXType {
		NONE(null),
		BLOCK_DESTROY(null),
		BLOCK_PLACE(null),
		LEAF(Blocks.LEAVES.getSoundType().getStepSound()),
		LOG(Blocks.LOG.getSoundType().getBreakSound()),
		DIRT(Blocks.DIRT.getSoundType().getBreakSound());

		public final SoundEvent soundEvent;
		public final float volume;
		public final float pitch;

		SoundFXType(SoundEvent soundEvent) {
			this.soundEvent = soundEvent;
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
		data.writeInt(state.getBlock().getMetaFromState(state));
		data.writeUTF(ItemStackUtil.getBlockNameFromRegistryAsSting(state.getBlock()));
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		this.visualFX = VisualFXType.values()[data.readShort()];
		this.soundFX = SoundFXType.values()[data.readShort()];
		int meta = data.readInt();
		this.state = ItemStackUtil.getBlockFromRegistry(data.readUTF()).getStateFromMeta(meta);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		World renderWorld = Proxies.common.getRenderWorld();
		if (visualFX != VisualFXType.NONE) {
			Proxies.common.addBlockDestroyEffects(renderWorld, getPos(), state);
		}
		if (soundFX != SoundFXType.NONE) {
			if (soundFX == SoundFXType.BLOCK_DESTROY) {
				Proxies.common.playBlockBreakSoundFX(renderWorld, getPos(), state);
			} else if (soundFX == SoundFXType.BLOCK_PLACE) {
				Proxies.common.playBlockPlaceSoundFX(renderWorld, getPos(), state);
			} else {
				Proxies.common.playSoundFX(renderWorld, getPos(), soundFX.soundEvent, SoundCategory.BLOCKS, soundFX.volume, soundFX.pitch);
			}
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.FX_SIGNAL;
	}
}
