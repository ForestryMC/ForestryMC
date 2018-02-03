package forestry.api.farming;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * @since Forestry 5.8
 */
public interface ISoil {

	ItemStack getResource();

	IBlockState getSoilState();

	boolean hasMetaData();
}
