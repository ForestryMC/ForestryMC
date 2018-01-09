package forestry.farming.logic;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public abstract class FarmLogicSoil extends FarmLogic {
	protected final Set<Soil> soils = new HashSet<>();

	public FarmLogicSoil() {
	}

	@Override
	public void addSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData) {
		soils.add(new Soil(resource,soilState,hasMetaData));
	}

}
