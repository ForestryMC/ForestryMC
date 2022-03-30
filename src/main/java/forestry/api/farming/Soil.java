package forestry.api.farming;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

public final class Soil {
	private final ItemStack resource;
	private final BlockState soilState;

	public Soil(ItemStack resource, BlockState soilState) {
		this.resource = resource;
		this.soilState = soilState;
	}

	public ItemStack getResource() {
		return resource;
	}

	public BlockState getSoilState() {
		return soilState;
	}
}