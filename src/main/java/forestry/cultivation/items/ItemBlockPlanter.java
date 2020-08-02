package forestry.cultivation.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.core.items.ItemBlockForestry;
import forestry.cultivation.blocks.BlockPlanter;

public class ItemBlockPlanter extends ItemBlockForestry<BlockPlanter> {

    public ItemBlockPlanter(BlockPlanter block) {
        super(block);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        String name = getBlock().blockType.getString();
        return new TranslationTextComponent("block.forestry.planter." + (getBlock().getMode().getString()), new TranslationTextComponent("block.forestry." + name));
    }
}
