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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.BreedingTracker;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketGenomeTrackerSync extends ForestryPacket implements IForestryPacketClient {
	private final NBTTagCompound nbt;

	public PacketGenomeTrackerSync(NBTTagCompound nbtTagCompound) {
		this.nbt = nbtTagCompound;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GENOME_TRACKER_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeCompoundTag(nbt);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			NBTTagCompound nbt = data.readCompoundTag();
			if (nbt != null) {
				String type = nbt.getString(BreedingTracker.TYPE_KEY);

				ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(type);
				if (root != null) {
					IBreedingTracker tracker = root.getBreedingTracker(player.getEntityWorld(), player.getGameProfile());
					tracker.decodeFromNBT(nbt);
					MinecraftForge.EVENT_BUS.post(new ForestryEvent.SyncedBreedingTracker(tracker, player));
				}
			}
		}
	}
}
