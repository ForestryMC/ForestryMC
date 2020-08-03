package forestry.arboriculture.blocks;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockForestryStairs extends StairsBlock implements IWoodTyped {
    private final boolean fireproof;
    private final IWoodType woodType;

    public BlockForestryStairs(BlockForestryPlank plank) {
        super(plank.getDefaultState(), Block.Properties.from(plank));
        this.fireproof = plank.isFireproof();
        this.woodType = plank.getWoodType();
    }

    @Override
    public boolean isFireproof() {
        return fireproof;
    }

    @Override
    public IWoodType getWoodType() {
        return woodType;
    }

    @Override
    public WoodBlockKind getBlockKind() {
        return WoodBlockKind.STAIRS;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        if (fireproof) {
            return 0;
        }
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        if (fireproof) {
            return 0;
        }
        return 5;
    }
}
