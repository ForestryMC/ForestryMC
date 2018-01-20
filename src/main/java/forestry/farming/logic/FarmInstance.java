package forestry.farming.logic;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmInstance;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISoil;
import forestry.farming.FarmRegistry;

public final class FarmInstance implements IFarmInstance {
	private final Set<ISoil> soils = new HashSet<>();
	private final Set<String> farmablesIdentifiers;
	private final IFarmLogic manualLogic;
	private final IFarmLogic managedLogic;
	private final String identifier;

	@Nullable
	private Collection<IFarmable> farmables;

	public FarmInstance(String identifier, BiFunction<IFarmInstance, Boolean, IFarmLogic> logicFactory, Set<String> farmablesIdentifiers) {
		this.identifier = identifier;
		this.farmablesIdentifiers = farmablesIdentifiers;
		this.manualLogic = logicFactory.apply(this, true);
		this.managedLogic = logicFactory.apply(this, false);
	}

	@Override
	public void registerFarmables(String identifier) {
		farmablesIdentifiers.add(identifier);
	}

	@Override
	public Collection<IFarmable> getFarmables() {
		if(farmables == null){
			farmables = farmablesIdentifiers.stream().map(FarmRegistry.getInstance()::getFarmables).flatMap(Collection::stream).collect(Collectors.toSet());
		}
		return farmables;
	}

	@Override
	public IFarmLogic getLogic(boolean manuel) {
		return manuel ? manualLogic : managedLogic;
	}

	@Override
	public void registerSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData) {
		soils.add(new Soil(resource, soilState, hasMetaData));
	}

	public boolean isAcceptedSoil(IBlockState ground) {
		for (ISoil soil : soils) {
			IBlockState soilState = soil.getSoilState();
			Block soilBlock = soilState.getBlock();
			Block block = ground.getBlock();
			if (soilState.getBlock() == ground.getBlock()) {
				if (!soil.hasMetaData() || block.getMetaFromState(ground) == soilBlock.getMetaFromState(soilState)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		for (ISoil soil : soils) {
			ItemStack resource = soil.getResource();
			if (resource.isItemEqual(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<ISoil> getSoils() {
		return soils;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
}
