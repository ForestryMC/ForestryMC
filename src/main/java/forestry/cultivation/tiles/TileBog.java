package forestry.cultivation.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileBog extends TilePlanter {
    public TileBog() {
        super(CultivationTiles.BOG.tileType(), ForestryFarmIdentifier.PEAT);
    }

    @Override
    public NonNullList<ItemStack> createGermlingStacks() {
        return NonNullList.create();
    }

    @Override
    public NonNullList<ItemStack> createResourceStacks() {
        return createList(
                CoreBlocks.BOG_EARTH.stack(),
                CoreBlocks.BOG_EARTH.stack(),
                CoreBlocks.BOG_EARTH.stack(),
                CoreBlocks.BOG_EARTH.stack()
        );
    }

    @Override
    public NonNullList<ItemStack> createProductionStacks() {
        return createList(
                CoreItems.PEAT.stack(),
                CoreItems.PEAT.stack(),
                CoreItems.PEAT.stack(),
                CoreItems.PEAT.stack()
        );
    }
}
