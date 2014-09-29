/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.items;

import java.util.List;

import forestry.core.utils.StringUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.core.Tabs;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.recipes.IVariableFermentable;
import forestry.arboriculture.genetics.Tree;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.network.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.Utils;
import forestry.plugins.PluginArboriculture;

public class ItemGermlingGE extends ItemGE implements IVariableFermentable {

	private final EnumGermlingType type;

	public ItemGermlingGE(EnumGermlingType type) {
		super();
		this.type = type;
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	protected IIndividual getIndividual(ItemStack itemstack) {
		return new Tree(itemstack.getTagCompound());
	}

	private IAlleleTreeSpecies getPrimarySpecies(ItemStack itemstack) {
		ITree tree = PluginArboriculture.treeInterface.getMember(itemstack);
		if (tree == null)
			return (IAlleleTreeSpecies) PluginArboriculture.treeInterface.getDefaultTemplate()[EnumTreeChromosome.SPECIES.ordinal()];
		else
			return tree.getGenome().getPrimary();
	}

	@Override
	protected int getDefaultPrimaryColour() {
		return 0;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (!itemstack.hasTagCompound())
			return "Unknown";
		IIndividual individual = getIndividual(itemstack);
		String customTreeKey = "trees.custom." + type.getName() + "." + individual.getGenome().getPrimary().getUnlocalizedName().replace("trees.species.","");
		if(StringUtil.canTranslate(customTreeKey)){
			return StringUtil.localize(customTreeKey);
		}
		return StringUtil.localize("trees.grammar." + type.getName()).replaceAll("%SPECIES", individual.getDisplayName()).replaceAll("%TYPE", StringUtil.localize("trees.grammar." + type.getName() + ".type"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		addCreativeItems(itemList, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addCreativeItems(List itemList, boolean hideSecrets) {
		for (IIndividual individual : PluginArboriculture.treeInterface.getIndividualTemplates()) {
			// Don't show secrets unless ordered to.
			if (hideSecrets && individual.isSecret() && !Config.isDebug)
				continue;

			itemList.add(PluginArboriculture.treeInterface.getMemberStack(individual, type.ordinal()));
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		return getPrimarySpecies(itemstack).getGermlingColour(type, renderPass);
	}

	/* ICONS */
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 2;//type == EnumGermlingType.SAPLING ? 1 : 2;
	}

	@Override
	public int getSpriteNumber() {
		return type == EnumGermlingType.SAPLING ? SpriteSheet.BLOCKS.getSheetOrdinal() : SpriteSheet.ITEMS.getSheetOrdinal();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack itemstack, int renderPass) {
		IAlleleTreeSpecies species = getPrimarySpecies(itemstack);
		return species.getGermlingIcon(type, renderPass);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float facingX, float facingY, float facingZ) {

		if (!Proxies.common.isSimulating(world))
			return false;

		ITree tree = PluginArboriculture.treeInterface.getMember(itemstack);
		if(tree == null)
			return false;

		if(type == EnumGermlingType.SAPLING) {
			// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
			int yShift;
			if (!Utils.isReplaceableBlock(world, x, y, z)) {
				if (!world.isAirBlock(x, y + 1, z))
					return false;
				yShift = 1;
			} else
				yShift = 0;

			if (!tree.canStay(world, x, y + yShift, z))
				return false;

			if (PluginArboriculture.treeInterface.plantSapling(world, tree, player.getGameProfile(), x, y + yShift, z)) {
				Proxies.common.addBlockPlaceEffects(world, x, y, z, world.getBlock(x, y + yShift, z), 0);
				if (!player.capabilities.isCreativeMode)
					itemstack.stackSize--;
				return true;
			} else
				return false;
		} else if(type == EnumGermlingType.POLLEN) {

			TileEntity target = world.getTileEntity(x, y, z);
			if(!(target instanceof IPollinatable))
				return false;

			IPollinatable pollinatable = (IPollinatable)target;
			if(!pollinatable.canMateWith(tree))
				return false;

			pollinatable.mateWith(tree);
			Proxies.common.sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.LEAF, world, x, y, z,
					world.getBlock(x, y, z), 0);
			if (!player.capabilities.isCreativeMode)
				itemstack.stackSize--;
			return true;

		} else
			return false;
	}

	@Override
	public float getFermentationModifier(ItemStack itemstack) {
		ITree tree = PluginArboriculture.treeInterface.getMember(itemstack);
		if (tree == null)
			return 1.0f;

		return tree.getGenome().getSappiness() * 10;
	}
}
