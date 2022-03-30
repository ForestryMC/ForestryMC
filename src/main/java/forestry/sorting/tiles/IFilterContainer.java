package forestry.sorting.tiles;

import javax.annotation.Nullable;

import net.minecraft.world.Container;

import forestry.api.core.ILocatable;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.core.tiles.ITitled;

public interface IFilterContainer extends ILocatable, ITitled {

	@Nullable
	Container getBuffer();

	TileGeneticFilter getTileEntity();

	IFilterLogic getLogic();
}
