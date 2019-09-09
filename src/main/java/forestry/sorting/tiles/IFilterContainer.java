package forestry.sorting.tiles;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;

import forestry.api.core.ILocatable;
import forestry.api.genetics.IFilterLogic;
import forestry.core.tiles.ITitled;

public interface IFilterContainer extends ILocatable, ITitled {

	@Nullable
	IInventory getBuffer();

	TileGeneticFilter getTileEntity();

	IFilterLogic getLogic();
}
