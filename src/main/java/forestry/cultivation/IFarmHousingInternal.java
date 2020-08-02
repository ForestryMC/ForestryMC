package forestry.cultivation;

import java.util.List;
import java.util.Map;

import net.minecraft.util.math.BlockPos;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.core.fluids.ITankManager;
import forestry.core.tiles.ILiquidTankTile;
import forestry.farming.FarmTarget;
import forestry.farming.multiblock.IFarmInventoryInternal;

public interface IFarmHousingInternal extends IFarmHousing, ILiquidTankTile {

    ITankManager getTankManager();

    @Override
    IFarmInventoryInternal getFarmInventory();

    void setUpFarmlandTargets(Map<FarmDirection, List<FarmTarget>> targets);

    BlockPos getTopCoord();
}
