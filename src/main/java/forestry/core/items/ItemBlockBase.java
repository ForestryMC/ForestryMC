package forestry.core.items;

import net.minecraft.world.level.block.Block;

import forestry.core.ItemGroupForestry;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.MachinePropertiesTesr;

import net.minecraft.world.item.Item.Properties;

public class ItemBlockBase<B extends Block> extends ItemBlockForestry<B> {

	public ItemBlockBase(B block, Properties builder, IBlockTypeTesr type) {
		super(block, MachinePropertiesTesr.setRenderer(builder, type));
	}

	public ItemBlockBase(B block, IBlockTypeTesr type) {
		super(block, MachinePropertiesTesr.setRenderer(new Properties().tab(ItemGroupForestry.tabForestry), type));
	}

}
