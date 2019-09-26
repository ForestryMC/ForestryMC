package forestry.farming.logic;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.ISoil;

class Soil implements ISoil {
	private final ItemStack resource;
	private final BlockState soilState;

	public Soil(ItemStack resource, BlockState soilState) {
		this.resource = resource;
		this.soilState = soilState;
	}

	@Override
	public ItemStack getResource() {
		return resource;
	}

	@Override
	public BlockState getSoilState() {
		return soilState;
	}
}