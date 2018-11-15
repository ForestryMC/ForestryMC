package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ILocatable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRuleType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.core.utils.NetworkUtil;
import forestry.sorting.network.packets.PacketFilterChangeGenome;
import forestry.sorting.network.packets.PacketFilterChangeRule;

public class FilterLogic implements IFilterLogic {
	private final ILocatable locatable;
	private final INetworkHandler networkHandler;
	private IFilterRuleType[] filterRules = new IFilterRuleType[6];
	private AlleleFilter[][] genomeFilter = new AlleleFilter[6][3];

	public FilterLogic(ILocatable locatable, INetworkHandler networkHandler) {
		this.locatable = locatable;
		this.networkHandler = networkHandler;
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getDefaultRule();
		}
	}

	@Override
	public INetworkHandler getNetworkHandler() {
		return networkHandler;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		for (int i = 0; i < filterRules.length; i++) {
			data.setString("TypeFilter" + i, filterRules[i].getUID());
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					continue;
				}
				if (filter.activeAllele != null) {
					data.setString("GenomeFilterS" + i + "-" + j + "-" + 0, filter.activeAllele.getUID());
				}
				if (filter.inactiveAllele != null) {
					data.setString("GenomeFilterS" + i + "-" + j + "-" + 1, filter.inactiveAllele.getUID());
				}
			}
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRuleOrDefault(data.getString("TypeFilter" + i));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.hasKey("GenomeFilterS" + i + "-" + j + "-" + 0)) {
					filter.activeAllele = AlleleManager.alleleRegistry.getAllele(data.getString("GenomeFilterS" + i + "-" + j + "-" + 0));
				}
				if (data.hasKey("GenomeFilterS" + i + "-" + j + "-" + 1)) {
					filter.inactiveAllele = AlleleManager.alleleRegistry.getAllele(data.getString("GenomeFilterS" + i + "-" + j + "-" + 1));
				}
				genomeFilter[i][j] = filter;
			}
		}
	}

	@Override
	public void writeGuiData(PacketBuffer data) {
		for (IFilterRuleType filterRule : filterRules) {
			data.writeShort(AlleleManager.filterRegistry.getId(filterRule));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					data.writeBoolean(false);
					data.writeBoolean(false);
					continue;
				}
				if (filter.activeAllele != null) {
					data.writeBoolean(true);
					data.writeString(filter.activeAllele.getUID());
				} else {
					data.writeBoolean(false);
				}
				if (filter.inactiveAllele != null) {
					data.writeBoolean(true);
					data.writeString(filter.inactiveAllele.getUID());
				} else {
					data.writeBoolean(false);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void readGuiData(PacketBuffer data) {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRule(data.readShort());
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.readBoolean()) {
					filter.activeAllele = AlleleManager.alleleRegistry.getAllele(data.readString(1024));
				}
				if (data.readBoolean()) {
					filter.inactiveAllele = AlleleManager.alleleRegistry.getAllele(data.readString(1024));
				}
				genomeFilter[i][j] = filter;
			}
		}
	}

	public Collection<EnumFacing> getValidDirections(ItemStack itemStack, EnumFacing from) {
		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		IIndividual individual = null;
		ISpeciesType type = null;
		if (root != null) {
			individual = root.getMember(itemStack);
			type = root.getType(itemStack);
		}
		IFilterData filterData = new FilterData(root, individual, type);
		List<EnumFacing> validFacings = new LinkedList<>();
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (facing == from) {
				continue;
			}
			if (isValid(facing, itemStack, filterData)) {
				validFacings.add(facing);
			}
		}
		return validFacings;
	}

	@Override
	public boolean isValid(ItemStack itemStack, EnumFacing facing) {
		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		IIndividual individual = null;
		ISpeciesType type = null;
		if (root != null) {
			individual = root.getMember(itemStack);
			type = root.getType(itemStack);
		}
		return isValid(facing, itemStack, new FilterData(root, individual, type));
	}

	public boolean isValid(EnumFacing facing, ItemStack itemStack, IFilterData filterData) {
		IFilterRuleType rule = getRule(facing);
		if (rule == DefaultFilterRuleType.CLOSED) {
			return false;
		}
		if (rule == DefaultFilterRuleType.ITEM && !filterData.isPresent()) {
			return true;
		}
		String requiredRoot = rule.getRootUID();
		if (requiredRoot != null && (!filterData.isPresent() || !filterData.getRoot().getUID().equals(requiredRoot))) {
			return false;
		}
		if (rule == DefaultFilterRuleType.ANYTHING || rule.isValid(itemStack, filterData)) {
			if (filterData.isPresent()) {
				IIndividual ind = filterData.getIndividual();
				IGenome genome = ind.getGenome();
				IAllele active = genome.getPrimary();
				IAllele inactive = genome.getSecondary();
				return isValidAllelePair(facing, active.getUID(), inactive.getUID());
			}
			return true;
		}
		return false;
	}

	public boolean isValidAllelePair(EnumFacing orientation, String activeUID, String inactiveUID) {
		AlleleFilter[] directionFilters = genomeFilter[orientation.ordinal()];

		if (directionFilters == null) {
			return true;
		}

		boolean foundFilter = false;
		for (int i = 0; i < 3; i++) {
			AlleleFilter filter = directionFilters[i];
			if (filter != null && !filter.isEmpty()) {
				foundFilter = true;
				if (!filter.isEmpty() && filter.isValid(activeUID, inactiveUID)) {
					return true;
				}
			}
		}
		return !foundFilter;
	}

	public IFilterRuleType getRule(EnumFacing facing) {
		return filterRules[facing.ordinal()];
	}

	public boolean setRule(EnumFacing facing, IFilterRuleType rule) {
		if (filterRules[facing.ordinal()] != rule) {
			filterRules[facing.ordinal()] = rule;
			return true;
		}
		return false;
	}

	@Nullable
	public AlleleFilter getGenomeFilter(EnumFacing facing, int index) {
		return genomeFilter[facing.ordinal()][index];
	}

	@Nullable
	public IAllele getGenomeFilter(EnumFacing facing, int index, boolean active) {
		AlleleFilter filter = getGenomeFilter(facing, index);
		if (filter == null) {
			return null;
		}
		return active ? filter.activeAllele : filter.inactiveAllele;
	}

	public boolean setGenomeFilter(EnumFacing facing, int index, boolean active, @Nullable IAllele allele) {
		AlleleFilter filter = genomeFilter[facing.ordinal()][index];
		if (filter == null) {
			filter = genomeFilter[facing.ordinal()][index] = new AlleleFilter();
		}
		boolean set;
		if (active) {
			set = filter.activeAllele != allele;
			filter.activeAllele = allele;
		} else {
			set = filter.inactiveAllele != allele;
			filter.inactiveAllele = allele;
		}
		return set;
	}

	@Override
	public void sendToServer(EnumFacing facing, int index, boolean active, @Nullable IAllele allele) {
		NetworkUtil.sendToServer(new PacketFilterChangeGenome(locatable.getCoordinates(), facing, (short) index, active, allele));
	}

	@Override
	public void sendToServer(EnumFacing facing, IFilterRuleType rule) {
		NetworkUtil.sendToServer(new PacketFilterChangeRule(locatable.getCoordinates(), facing, rule));
	}
}
