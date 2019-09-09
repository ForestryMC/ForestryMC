package forestry.farming.logic;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.ISoil;

class Soil implements ISoil {
	private final ItemStack resource;
	private final BlockState soilState;
	private final boolean hasMetaData;

	public Soil(ItemStack resource, BlockState soilState, boolean hasMetaData) {
		this.resource = resource;
		this.soilState = soilState;
		this.hasMetaData = hasMetaData;
	}

	public ItemStack getResource() {
		return resource;
	}

	public BlockState getSoilState() {
		return soilState;
	}

	public boolean hasMetaData() {
		return this.hasMetaData;
	}
}