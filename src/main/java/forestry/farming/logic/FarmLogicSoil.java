package forestry.farming.logic;

import forestry.api.farming.IFarmProperties;
import net.minecraft.block.BlockState;

public abstract class FarmLogicSoil extends FarmLogic {

    public FarmLogicSoil(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    protected boolean isAcceptedSoil(BlockState blockState) {
        return properties.isAcceptedSoil(blockState);
    }

}
