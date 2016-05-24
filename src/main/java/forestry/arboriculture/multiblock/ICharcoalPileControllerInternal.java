package forestry.arboriculture.multiblock;

import forestry.api.multiblock.ICharcoalPileController;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.tiles.IActivatable;
import net.minecraft.util.math.BlockPos;

public interface ICharcoalPileControllerInternal extends IMultiblockControllerInternal, ICharcoalPileController, IActivatable {
	
	BlockPos getMinimumCoord();
	
	BlockPos getMaximumCoord();

}
