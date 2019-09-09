package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

import forestry.api.core.ILocatable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRuleType;
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
	public CompoundNBT write(CompoundNBT data) {
		for (int i = 0; i < filterRules.length; i++) {
			data.putString("TypeFilter" + i, filterRules[i].getUID());
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					continue;
				}
				if (filter.activeAllele != null) {
					data.putString("GenomeFilterS" + i + "-" + j + "-" + 0, filter.activeAllele.getRegistryName().toString());
				}
				if (filter.inactiveAllele != null) {
					data.putString("GenomeFilterS" + i + "-" + j + "-" + 1, filter.inactiveAllele.getRegistryName().toString());
				}
			}
		}
		return data;
	}

	@Override
	public void read(CompoundNBT data) {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRuleOrDefault(data.getString("TypeFilter" + i));
		}

		IAlleleRegistry alleleRegistry = GeneticsAPI.apiInstance.getAlleleRegistry();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.contains("GenomeFilterS" + i + "-" + j + "-" + 0)) {
					filter.activeAllele = alleleRegistry.getAllele(data.getString("GenomeFilterS" + i + "-" + j + "-" + 0)).orElse(null);
				}
				if (data.contains("GenomeFilterS" + i + "-" + j + "-" + 1)) {
					filter.inactiveAllele = alleleRegistry.getAllele(data.getString("GenomeFilterS" + i + "-" + j + "-" + 1)).orElse(null);
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
					data.writeString(filter.activeAllele.getRegistryName().toString());
				} else {
					data.writeBoolean(false);
				}
				if (filter.inactiveAllele != null) {
					data.writeBoolean(true);
					data.writeString(filter.inactiveAllele.getRegistryName().toString());
				} else {
					data.writeBoolean(false);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void readGuiData(PacketBuffer data) {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRule(data.readShort());
		}

		IAlleleRegistry alleleRegistry = GeneticsAPI.apiInstance.getAlleleRegistry();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.readBoolean()) {
					filter.activeAllele = alleleRegistry.getAllele(data.readString(1024)).orElse(null);
				}
				if (data.readBoolean()) {
					filter.inactiveAllele = alleleRegistry.getAllele(data.readString(1024)).orElse(null);
				}
				genomeFilter[i][j] = filter;
			}
		}
	}

	public Collection<Direction> getValidDirections(ItemStack itemStack, Direction from) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(itemStack);
		IIndividual individual = null;
		IOrganismType type = null;
		if (definition.isRootPresent()) {
			IIndividualRoot<IIndividual> root = definition.get();
			individual = root.create(itemStack).orElse(null);
			type = root.getTypes().getType(itemStack).orElse(null);
		}
		IFilterData filterData = new FilterData(definition, individual, type);
		List<Direction> validFacings = new LinkedList<>();
		for (Direction facing : Direction.VALUES) {
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
	public boolean isValid(ItemStack itemStack, Direction facing) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(itemStack);
		IIndividual individual = null;
		IOrganismType type = null;
		if (definition.isRootPresent()) {
			IIndividualRoot<IIndividual> root = definition.get();
			individual = root.create(itemStack).orElse(null);
			type = root.getTypes().getType(itemStack).orElse(null);
		}
		return isValid(facing, itemStack, new FilterData(definition, individual, type));
	}

	public boolean isValid(Direction facing, ItemStack itemStack, IFilterData filterData) {
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
				return isValidAllelePair(facing, active.getRegistryName().toString(), inactive.getRegistryName().toString());
			}
			return true;
		}
		return false;
	}

	public boolean isValidAllelePair(Direction orientation, String activeUID, String inactiveUID) {
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

	public IFilterRuleType getRule(Direction facing) {
		return filterRules[facing.ordinal()];
	}

	public boolean setRule(Direction facing, IFilterRuleType rule) {
		if (filterRules[facing.ordinal()] != rule) {
			filterRules[facing.ordinal()] = rule;
			return true;
		}
		return false;
	}

	@Nullable
	public AlleleFilter getGenomeFilter(Direction facing, int index) {
		return genomeFilter[facing.ordinal()][index];
	}

	@Nullable
	public IAllele getGenomeFilter(Direction facing, int index, boolean active) {
		AlleleFilter filter = getGenomeFilter(facing, index);
		if (filter == null) {
			return null;
		}
		return active ? filter.activeAllele : filter.inactiveAllele;
	}

	public boolean setGenomeFilter(Direction facing, int index, boolean active, @Nullable IAllele allele) {
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
	public void sendToServer(Direction facing, int index, boolean active, @Nullable IAllele allele) {
		NetworkUtil.sendToServer(new PacketFilterChangeGenome(locatable.getCoordinates(), facing, (short) index, active, allele));
	}

	@Override
	public void sendToServer(Direction facing, IFilterRuleType rule) {
		NetworkUtil.sendToServer(new PacketFilterChangeRule(locatable.getCoordinates(), facing, rule));
	}
}
