package forestry.arboriculture.blocks;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockForestryFence extends FenceBlock implements IWoodTyped {

    private final boolean fireproof;
    private final IWoodType woodType;

    public BlockForestryFence(boolean fireproof, IWoodType woodType) {
        super(BlockForestryPlank.createWoodProperties(woodType));
        this.fireproof = fireproof;
        this.woodType = woodType;
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

    @Override
    public WoodBlockKind getBlockKind() {
        return WoodBlockKind.FENCE;
    }
}
