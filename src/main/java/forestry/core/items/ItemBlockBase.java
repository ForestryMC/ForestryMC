package forestry.core.items;

import forestry.core.ItemGroupForestry;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import net.minecraft.block.Block;

public class ItemBlockBase<B extends Block> extends ItemBlockForestry<B> {

    public ItemBlockBase(B block, Properties builder, IBlockTypeTesr type) {
        super(block, MachinePropertiesTesr.setRenderer(builder, type));
    }

    public ItemBlockBase(B block, IBlockTypeTesr type) {
        super(block, MachinePropertiesTesr.setRenderer(new Properties().group(ItemGroupForestry.tabForestry), type));
    }

}
