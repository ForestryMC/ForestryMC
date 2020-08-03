package forestry.sorting.tiles;

import forestry.api.core.ILocatable;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.core.tiles.ITitled;
import net.minecraft.inventory.IInventory;

import javax.annotation.Nullable;

public interface IFilterContainer extends ILocatable, ITitled {

    @Nullable
    IInventory getBuffer();

    TileGeneticFilter getTileEntity();

    IFilterLogic getLogic();
}
