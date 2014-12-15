package forestry.arboriculture.items;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.ITree;
import forestry.arboriculture.genetics.Tree;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginArboriculture;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
		if(StringUtil.canTranslate(customTreeKey)){
			return StringUtil.localize(customTreeKey);
		}
		String grammar = StringUtil.localize("trees.grammar.leaves");

		return grammar.replaceAll("%SPECIES", tree.getDisplayName()).replaceAll("%TYPE", type);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound())
			return PluginArboriculture.proxy.getFoliageColorBasic();
		ITree tree = getTree(itemstack);
		if (tree == null)
			return PluginArboriculture.proxy.getFoliageColorBasic();
		return tree.getGenome().getPrimary().getLeafColour(tree);
	}

	private ITree getTree(ItemStack itemStack) {
		return new Tree(itemStack.getTagCompound());
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!stack.hasTagCompound())
			return false;
		ITree tree = getTree(stack);
		if (tree == null)
			return false;
		GameProfile owner = player.getGameProfile();
		return PluginArboriculture.treeInterface.setLeaves(world, tree, owner, x, y, z, true);
	}

}
