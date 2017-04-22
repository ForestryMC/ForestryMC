package forestry.apiculture.items;

import forestry.apiculture.blocks.BlockHoneyComb;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockHoneyComb extends ItemBlockForestry<BlockHoneyComb> implements IColoredItem {

	public ItemBlockHoneyComb(Block block) {
		super(block);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		int meta = stack.getMetadata();
		EnumHoneyComb honeyComb = EnumHoneyComb.get(getBlock().minMeta + meta);
		if (tintIndex == 1) {
			return honeyComb.primaryColor;
		} else {
			return honeyComb.secondaryColor;
		}
	}

}
