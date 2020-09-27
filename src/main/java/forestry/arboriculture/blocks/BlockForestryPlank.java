package forestry.arboriculture.blocks;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockForestryPlank extends Block implements IWoodTyped {

    public static Properties createWoodProperties(IWoodType woodType) {
        return Block.Properties.create(Material.WOOD)
                               .hardnessAndResistance(woodType.getHardness(), woodType.getHardness() * 1.5F)
                               .sound(SoundType.WOOD)
                               .harvestTool(ToolType.AXE)
                               .harvestLevel(0);
    }

    private final boolean fireproof;
    private final IWoodType woodType;

    public BlockForestryPlank(boolean fireproof, IWoodType woodType) {
        super(createWoodProperties(woodType));
        this.fireproof = fireproof;
        this.woodType = woodType;
    }

    public IWoodType getWoodType() {
        return woodType;
    }

    @Override
    public boolean isFireproof() {
        return fireproof;
    }

    @Override
    public WoodBlockKind getBlockKind() {
        return WoodBlockKind.PLANKS;
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
