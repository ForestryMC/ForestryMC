package forestry.cultivation.tiles;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

public class TileFarmEnder extends TilePlanter {
    public TileFarmEnder() {
        super(CultivationTiles.ENDER.tileType(), ForestryFarmIdentifier.ENDER);
    }

    @Override
    public NonNullList<ItemStack> createGermlingStacks() {
        return createList(
                new ItemStack(Blocks.CHORUS_FLOWER),
                new ItemStack(Blocks.CHORUS_FLOWER),
                new ItemStack(Blocks.CHORUS_FLOWER),
                new ItemStack(Blocks.CHORUS_FLOWER)
        );
    }

    @Override
    public NonNullList<ItemStack> createResourceStacks() {
        return createList(
                new ItemStack(Blocks.END_STONE),
                new ItemStack(Blocks.END_STONE),
                new ItemStack(Blocks.END_STONE),
                new ItemStack(Blocks.END_STONE)
        );
    }

    @Override
    public NonNullList<ItemStack> createProductionStacks() {
        return createList(
                new ItemStack(Blocks.CHORUS_FLOWER),
                new ItemStack(Items.CHORUS_FRUIT),
                new ItemStack(Items.CHORUS_FRUIT),
                new ItemStack(Blocks.CHORUS_FLOWER)
        );
    }
}
