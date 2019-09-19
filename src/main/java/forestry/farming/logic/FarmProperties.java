package forestry.farming.logic;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.ISoil;
import forestry.farming.FarmRegistry;

public final class FarmProperties implements IFarmProperties {
	private final Set<ISoil> soils = new HashSet<>();
	private final Set<String> farmablesIdentifiers;
	private final IFarmLogic manualLogic;
	private final IFarmLogic managedLogic;
	private final IFarmableInfo defaultInfo;
	@Nullable
	private Collection<IFarmable> farmables;
	@Nullable
	private Collection<IFarmableInfo> farmableInfo;

	public FarmProperties(BiFunction<IFarmProperties, Boolean, IFarmLogic> logicFactory, Set<String> farmablesIdentifiers, String identifier) {
		this.farmablesIdentifiers = farmablesIdentifiers;
		this.manualLogic = logicFactory.apply(this, true);
		this.managedLogic = logicFactory.apply(this, false);
		this.defaultInfo = FarmRegistry.getInstance().getFarmableInfo(identifier);
	}

	@Override
	public void registerFarmables(String identifier) {
		farmablesIdentifiers.add(identifier);
	}

	@Override
	public Collection<IFarmable> getFarmables() {
		if (farmables == null) {
			farmables = farmablesIdentifiers.stream()
					.map(FarmRegistry.getInstance()::getFarmables)
					.flatMap(Collection::stream)
					.collect(Collectors.toSet());
		}
		return farmables;
	}

	@Override
	public Collection<IFarmableInfo> getFarmableInfo() {
		if (farmableInfo == null) {
			farmableInfo = farmablesIdentifiers.stream()
					.map(FarmRegistry.getInstance()::getFarmableInfo)
					.collect(Collectors.toSet());
		}
		return farmableInfo;
	}

	@Override
	public IFarmLogic getLogic(boolean manuel) {
		return manuel ? manualLogic : managedLogic;
	}

	@Override
	public void registerSoil(ItemStack resource, BlockState state) {
		soils.add(new Soil(resource, state));
	}

	@Override
	public void addGermlings(ItemStack... germlings) {
		defaultInfo.addGermlings(germlings);
	}

	@Override
	public void addGermlings(Collection<ItemStack> germlings) {
		defaultInfo.addGermlings(germlings);
	}

	@Override
	public void addProducts(ItemStack... products) {
		defaultInfo.addProducts(products);
	}

	@Override
	public void addProducts(Collection<ItemStack> products) {
		defaultInfo.addProducts(products);
	}

	@Override
	public boolean isAcceptedSoil(BlockState ground) {
		for(ISoil soil : soils) {
			BlockState soilState = soil.getSoilState();
			if(soilState.getBlock() == ground.getBlock()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		for (ISoil soil : soils) {
			//TODO this is deprecated??
			if (soil.getResource().isItemEqual(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<ISoil> getSoils() {
		return soils;
	}
}
