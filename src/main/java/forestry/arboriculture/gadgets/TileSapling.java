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
package forestry.arboriculture.gadgets;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.api.arboriculture.ITree;
import forestry.arboriculture.network.PacketSapling;
import forestry.core.network.ForestryPacket;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginArboriculture;

public class TileSapling extends TileTreeContainer {

	private int timesTicked = 0;

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		timesTicked = nbttagcompound.getInteger("TT");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("TT", timesTicked);
	}

	@Override
	public void onBlockTick() {

		timesTicked++;
		tryGrow(false);
	}

	public int tryGrow(boolean bonemealed) {

		int result = 0;
		if (this.getTree() == null) {
			return result;
		}

		int maturity = (int) (getTree().getRequiredMaturity() * PluginArboriculture.treeInterface.getTreekeepingMode(worldObj).getMaturationModifier(
				getTree().getGenome(), 1f));

		if (bonemealed && timesTicked < maturity) {
			timesTicked++;
			result = 1;
		}

		if (timesTicked < maturity) {
			return result;
		}

		WorldGenerator generator = this.getTree().getTreeGenerator(worldObj, xCoord, yCoord, zCoord, bonemealed);
		if (generator.generate(worldObj, worldObj.rand, xCoord, yCoord, zCoord)) {
			PluginArboriculture.treeInterface.getBreedingTracker(worldObj, getOwnerProfile()).registerBirth(getTree());
			return 2;
		}

		return 3;
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketSapling(this).getPacket();
	}

	@Override
	public void sendNetworkUpdate() {
		PacketSapling saplingPacket = new PacketSapling(this);
		Proxies.net.sendNetworkPacket(saplingPacket, xCoord, yCoord, zCoord);
	}

	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		PacketSapling packet = (PacketSapling) packetRaw;

		ITree tree = getTree();
		if (packet.matchesTree(tree)) {
			return;
		}

		ITree newTree = packet.getTree();
		if (newTree == null) {
			return;
		}

		setTree(newTree);
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

}
