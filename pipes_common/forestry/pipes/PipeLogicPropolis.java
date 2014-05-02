/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.core.network.GuiId;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketNBT;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class PipeLogicPropolis {

	private Pipe pipe;
	
	private EnumFilterType[] typeFilter = new EnumFilterType[6];
	private IAllele[][][] genomeFilter = new IAllele[6][3][2];

	public PipeLogicPropolis(Pipe pipe) {
		this.pipe = pipe;
		for (int i = 0; i < typeFilter.length; i++)
			typeFilter[i] = EnumFilterType.CLOSED;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		for (int i = 0; i < typeFilter.length; i++)
			typeFilter[i] = EnumFilterType.values()[nbttagcompound.getByte("TypeFilter" + i)];

		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 3; j++) {
				if (nbttagcompound.hasKey("GenomeFilterS" + i + "-" + j + "-" + 0))
					genomeFilter[i][j][0] = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString("GenomeFilterS" + i + "-" + j + "-" + 0));
				if (nbttagcompound.hasKey("GenomeFilterS" + i + "-" + j + "-" + 1))
					genomeFilter[i][j][1] = AlleleManager.alleleRegistry.getAllele(nbttagcompound.getString("GenomeFilterS" + i + "-" + j + "-" + 1));
			}
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		for (int i = 0; i < typeFilter.length; i++)
			nbttagcompound.setByte("TypeFilter" + i, (byte) typeFilter[i].ordinal());

		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 3; j++) {
				if (genomeFilter[i][j][0] != null)
					nbttagcompound.setString("GenomeFilterS" + i + "-" + j + "-" + 0, genomeFilter[i][j][0].getUID());
				if (genomeFilter[i][j][1] != null)
					nbttagcompound.setString("GenomeFilterS" + i + "-" + j + "-" + 1, genomeFilter[i][j][1].getUID());
			}
	}
	
	public boolean isClosed(ForgeDirection orientation) {
		return typeFilter[orientation.ordinal()] == EnumFilterType.CLOSED;
	}

	public boolean isIndiscriminate(ForgeDirection orientation) {
		return typeFilter[orientation.ordinal()] == EnumFilterType.ANYTHING;
	}

	public boolean matchType(ForgeDirection orientation, EnumFilterType type, IBee bee) {
		EnumFilterType filter = typeFilter[orientation.ordinal()];
		if (filter == EnumFilterType.BEE)
			return type != EnumFilterType.ITEM && type != EnumFilterType.CLOSED && type != EnumFilterType.CLOSED;

		// Special bee filtering
		if (bee != null) {
			if (filter == EnumFilterType.PURE_BREED)
				return bee.isPureBred(EnumBeeChromosome.SPECIES.ordinal());
			if (filter == EnumFilterType.NOCTURNAL)
				return bee.getGenome().getNocturnal();
			if (filter == EnumFilterType.PURE_NOCTURNAL)
				return bee.getGenome().getNocturnal() && bee.isPureBred(EnumBeeChromosome.NOCTURNAL.ordinal());
			if (filter == EnumFilterType.FLYER)
				return bee.getGenome().getTolerantFlyer();
			if (filter == EnumFilterType.PURE_FLYER)
				return bee.getGenome().getTolerantFlyer() && bee.isPureBred(EnumBeeChromosome.TOLERANT_FLYER.ordinal());
			if (filter == EnumFilterType.CAVE)
				return bee.getGenome().getCaveDwelling();
			if (filter == EnumFilterType.PURE_CAVE)
				return bee.getGenome().getCaveDwelling() && bee.isPureBred(EnumBeeChromosome.CAVE_DWELLING.ordinal());
			if (filter == EnumFilterType.NATURAL)
				return bee.isNatural();
		}

		// Compare the remaining
		return filter == type;
	}

	public boolean matchAllele(IAllele filter, String ident) {
		if (filter == null)
			return true;
		else
			return filter.getUID().equals(ident);
	}

	public ArrayList<IAllele[]> getGenomeFilters(ForgeDirection orientation) {
		ArrayList<IAllele[]> filters = new ArrayList();

		for (int i = 0; i < 3; i++)
			if (genomeFilter[orientation.ordinal()][i] != null
					&& (genomeFilter[orientation.ordinal()][i][0] != null || genomeFilter[orientation.ordinal()][i][1] != null))
				filters.add(genomeFilter[orientation.ordinal()][i]);

		return filters;
	}

	public EnumFilterType getTypeFilter(ForgeDirection orientation) {
		return typeFilter[orientation.ordinal()];
	}

	public void setTypeFilter(ForgeDirection orientation, EnumFilterType type) {
		typeFilter[orientation.ordinal()] = type;
		if (!Proxies.common.isSimulating(pipe.getWorld()))
			sendTypeFilterChange(orientation, type);
	}

	public IAlleleSpecies getSpeciesFilter(ForgeDirection orientation, int pattern, int allele) {

		if (genomeFilter[orientation.ordinal()] == null)
			return null;

		if (genomeFilter[orientation.ordinal()].length <= pattern)
			return null;

		if (genomeFilter[orientation.ordinal()][pattern] == null)
			return null;

		if (genomeFilter[orientation.ordinal()][pattern].length <= allele)
			return null;

		return (IAlleleSpecies) genomeFilter[orientation.ordinal()][pattern][allele];
	}

	public void setSpeciesFilter(ForgeDirection orientation, int pattern, int allele, IAllele species) {
		genomeFilter[orientation.ordinal()][pattern][allele] = species;
		if (!Proxies.common.isSimulating(pipe.getWorld()))
			sendGenomeFilterChange(orientation, pattern, allele, species);
	}

	// Server side
	public void sendFilterSet(EntityPlayer player) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		Proxies.net.sendToPlayer(new PacketNBT(PacketIds.PROP_SEND_FILTER_SET, nbttagcompound), player);
	}

	public void handleTypeFilterChange(PacketPayload payload) {
		typeFilter[payload.intPayload[0]] = EnumFilterType.values()[payload.intPayload[1]];
	}

	public void handleGenomeFilterChange(PacketPayload payload) {
		if (!payload.stringPayload[0].equals("NULL"))
			genomeFilter[payload.intPayload[0]][payload.intPayload[1]][payload.intPayload[2]] = AlleleManager.alleleRegistry
					.getAllele(payload.stringPayload[0]);
		else
			genomeFilter[payload.intPayload[0]][payload.intPayload[1]][payload.intPayload[2]] = null;
	}

	// Client side
	public void requestFilterSet() {
		Proxies.net.sendToServer(new PacketCoordinates(PacketIds.PROP_REQUEST_FILTER_SET, pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord));
	}

	public void sendTypeFilterChange(ForgeDirection orientation, EnumFilterType filter) {
		PacketPayload payload = new PacketPayload(2, 0, 0);
		payload.intPayload[0] = orientation.ordinal();
		payload.intPayload[1] = filter.ordinal();
		Proxies.net.sendToServer(new PacketUpdate(PacketIds.PROP_SEND_FILTER_CHANGE_TYPE, pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord, payload));
	}

	public void sendGenomeFilterChange(ForgeDirection orientation, int pattern, int allele, IAllele species) {
		PacketPayload payload = new PacketPayload(3, 0, 1);
		payload.intPayload[0] = orientation.ordinal();
		payload.intPayload[1] = pattern;
		payload.intPayload[2] = allele;
		if (species != null)
			payload.stringPayload[0] = species.getUID();
		else
			payload.stringPayload[0] = "NULL";

		Proxies.net.sendToServer(new PacketUpdate(PacketIds.PROP_SEND_FILTER_CHANGE_GENOME, pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord, payload));
	}

	public void handleFilterSet(PacketNBT packet) {
		this.readFromNBT(packet.getTagCompound());
	}
}
