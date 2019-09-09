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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.genetics.BreedingTracker;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketGenomeTrackerSync extends ForestryPacket implements IForestryPacketClient {
	private final CompoundNBT nbt;

	public PacketGenomeTrackerSync(CompoundNBT CompoundNBT) {
		this.nbt = CompoundNBT;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GENOME_TRACKER_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeCompoundTag(nbt);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
			CompoundNBT nbt = data.readCompoundTag();
			if (nbt != null) {
				String type = nbt.getString(BreedingTracker.TYPE_KEY);

				IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(type);

				if (definition.isRootPresent()) {
					IBreedingTracker tracker = definition.get().getBreedingTracker(player.getEntityWorld(), player.getGameProfile());
					tracker.decodeFromNBT(nbt);
					MinecraftForge.EVENT_BUS.post(new ForestryEvent.SyncedBreedingTracker(tracker, player));
				}
			}
		}
	}
}
