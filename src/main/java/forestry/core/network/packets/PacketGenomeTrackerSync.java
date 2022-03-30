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

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.genetics.BreedingTracker;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;

public class PacketGenomeTrackerSync extends ForestryPacket implements IForestryPacketClient {
	private final CompoundTag nbt;

	public PacketGenomeTrackerSync(CompoundTag CompoundNBT) {
		this.nbt = CompoundNBT;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GENOME_TRACKER_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeNbt(nbt);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, Player player) throws IOException {
			CompoundTag nbt = data.readNbt();
			if (nbt != null) {
				String type = nbt.getString(BreedingTracker.TYPE_KEY);

				IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRoot(type);
				definition.ifPresent(root -> {
					IBreedingTracker tracker = root.getBreedingTracker(player.getCommandSenderWorld(), player.getGameProfile());
					tracker.decodeFromNBT(nbt);
					MinecraftForge.EVENT_BUS.post(new ForestryEvent.SyncedBreedingTracker(tracker, player));
				});
			}
		}
	}
}
