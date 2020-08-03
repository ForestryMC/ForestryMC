package forestry.cultivation.tiles;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TileFarmGourd extends TilePlanter {
    public TileFarmGourd() {
        super(CultivationTiles.GOURD.tileType(), ForestryFarmIdentifier.GOURD);
    }

    @Override
    public NonNullList<ItemStack> createGermlingStacks() {
        return NonNullList.create();
    }

    @Override
    public NonNullList<ItemStack> createResourceStacks() {
        return NonNullList.create();
    }

    @Override
    public NonNullList<ItemStack> createProductionStacks() {
        return createList(
                new ItemStack(Blocks.MELON),
                new ItemStack(Blocks.PUMPKIN),
                new ItemStack(Blocks.PUMPKIN),
                new ItemStack(Blocks.MELON)
        );
    }
}
