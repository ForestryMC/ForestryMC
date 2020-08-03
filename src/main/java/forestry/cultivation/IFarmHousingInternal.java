package forestry.cultivation;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.core.fluids.ITankManager;
import forestry.core.tiles.ILiquidTankTile;
import forestry.farming.FarmTarget;
import forestry.farming.multiblock.IFarmInventoryInternal;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public interface IFarmHousingInternal extends IFarmHousing, ILiquidTankTile {

    ITankManager getTankManager();

    @Override
    IFarmInventoryInternal getFarmInventory();

    void setUpFarmlandTargets(Map<FarmDirection, List<FarmTarget>> targets);

    BlockPos getTopCoord();
}
