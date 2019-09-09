package forestry.database.blocks;

import forestry.core.blocks.BlockBase;

public class BlockDatabase extends BlockBase<BlockTypeDatabase> {

	public BlockDatabase(BlockTypeDatabase blockType) {
		super(blockType);
		//TODO harvest level
		//		setHarvestLevel("pickaxe", 0);
	}

}
