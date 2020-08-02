package forestry.farming.logic;

import net.minecraft.block.BlockState;

import forestry.api.farming.IFarmProperties;

public abstract class FarmLogicSoil extends FarmLogic {

    public FarmLogicSoil(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    protected boolean isAcceptedSoil(BlockState blockState) {
        return properties.isAcceptedSoil(blockState);
    }

}
