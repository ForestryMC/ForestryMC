package forestry.database.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import net.minecraftforge.common.ToolType;

import forestry.core.blocks.BlockBase;

public class BlockDatabase extends BlockBase<BlockTypeDatabase> {

	public BlockDatabase(BlockTypeDatabase blockType) {
		super(blockType, Block.Properties.of(Material.METAL)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0));
	}

}
