package forestry.database.blocks;

import forestry.core.blocks.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockDatabase extends BlockBase<BlockTypeDatabase> {

    public BlockDatabase(BlockTypeDatabase blockType) {
        super(blockType, Block.Properties.create(Material.IRON)
                                         .harvestTool(ToolType.PICKAXE)
                                         .harvestLevel(0));
    }

}
