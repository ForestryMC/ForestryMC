package forestry.farming.logic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.ISoil;

class Soil implements ISoil {
	private final ItemStack resource;
	private final IBlockState soilState;
	private final boolean hasMetaData;

	public Soil(ItemStack resource, IBlockState soilState, boolean hasMetaData) {
		this.resource = resource;
		this.soilState = soilState;
		this.hasMetaData = hasMetaData;
	}

	public ItemStack getResource() {
		return resource;
	}

	public IBlockState getSoilState() {
		return soilState;
	}

	public boolean hasMetaData() {
		return this.hasMetaData;
	}
}