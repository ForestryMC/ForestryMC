package forestry.cultivation.tiles;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileArboretum extends TilePlanter {
    public TileArboretum() {
        super(CultivationTiles.ARBORETUM.tileType(), ForestryFarmIdentifier.ARBOREAL);
    }

    @Override
    public NonNullList<ItemStack> createGermlingStacks() {
        return createList(
                new ItemStack(Blocks.OAK_SAPLING),
                new ItemStack(Blocks.BIRCH_SAPLING),
                new ItemStack(Blocks.BIRCH_SAPLING),
                new ItemStack(Blocks.OAK_SAPLING)
        );
    }

    @Override
    public NonNullList<ItemStack> createResourceStacks() {
        return createList(
                new ItemStack(Blocks.DIRT),
                new ItemStack(Blocks.DIRT),
                new ItemStack(Blocks.DIRT),
                new ItemStack(Blocks.DIRT)
        );
    }

    @Override
    public NonNullList<ItemStack> createProductionStacks() {
        return createList(
                new ItemStack(Blocks.OAK_LOG),
                new ItemStack(Items.APPLE),
                new ItemStack(Items.APPLE),
                new ItemStack(Blocks.OAK_LOG)
        );
    }
}
