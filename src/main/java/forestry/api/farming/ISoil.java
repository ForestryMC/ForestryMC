package forestry.api.farming;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * @since Forestry 5.8
 */
public interface ISoil {

	ItemStack getResource();

	BlockState getSoilState();

	boolean hasMetaData();
}
