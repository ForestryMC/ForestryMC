package forestry.arboriculture.items;

import forestry.api.arboriculture.ITree;
import forestry.arboriculture.genetics.Tree;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.StringUtil;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemLeavesBlock extends ItemForestryBlock {

	public ItemLeavesBlock(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		String type = StringUtil.localize("trees.grammar.leaves.type");
		if (!itemstack.hasTagCompound())
			return type;
		ITree tree = getTree(itemstack);
		String customTreeKey = "trees.custom.leaves." + tree.getGenome().getPrimary().getUnlocalizedName().replace("trees.species.","");
		if(StatCollector.canTranslate("for." + customTreeKey)){
			return StringUtil.localize(customTreeKey);
		}
		String grammar = StringUtil.localize("trees.grammar.leaves");

		return grammar.replaceAll("%SPECIES", tree.getDisplayName()).replaceAll("%TYPE", type);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		ITree tree = getTree(itemstack);
		return tree.getGenome().getPrimary().getLeafColour(tree);
	}

	private ITree getTree(ItemStack itemStack) {
		return new Tree(itemStack.getTagCompound());
	}

}
