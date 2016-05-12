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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.recipes.IVariableFermentable;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.arboriculture.genetics.pollination.ICheckPollinatable;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.IColoredItem;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Translator;

public class ItemGermlingGE extends ItemGE implements IVariableFermentable, IColoredItem {

	private final EnumGermlingType type;

	public ItemGermlingGE(EnumGermlingType type) {
		super(Tabs.tabArboriculture);
		this.type = type;
	}

	@Override
	public ITree getIndividual(ItemStack itemstack) {
		return new Tree(itemstack.getTagCompound());
	}

	@Override
	protected IAlleleTreeSpecies getSpecies(ItemStack itemStack) {
		return TreeGenome.getSpecies(itemStack);
	}

	private static IAlleleTreeSpecies getSpeciesOrDefault(ItemStack itemstack) {
		IAlleleTreeSpecies treeSpecies = TreeGenome.getSpecies(itemstack);
		if (treeSpecies == null) {
			treeSpecies = TreeDefinition.Oak.getGenome().getPrimary();
		}

		return treeSpecies;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (!itemstack.hasTagCompound()) {
			return "Unknown";
		}
		IAlleleSpecies species = getSpecies(itemstack);

		String customTreeKey = "for.trees.custom." + type.getName() + "." + species.getUnlocalizedName().replace("trees.species.", "");
		if (Translator.canTranslateToLocal(customTreeKey)) {
			return Translator.translateToLocal(customTreeKey);
		}
		String typeString = Translator.translateToLocal("for.trees.grammar." + type.getName() + ".type");
		return Translator.translateToLocal("for.trees.grammar." + type.getName()).replaceAll("%SPECIES", species.getName()).replaceAll("%TYPE", typeString);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		addCreativeItems(itemList, true);
	}

	public void addCreativeItems(List<ItemStack> itemList, boolean hideSecrets) {
		for (IIndividual individual : TreeManager.treeRoot.getIndividualTemplates()) {
			// Don't show secrets unless ordered to.
			if (hideSecrets && individual.isSecret() && !Config.isDebug) {
				continue;
			}

			itemList.add(TreeManager.treeRoot.getMemberStack(individual, type));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack itemstack, int renderPass) {
		return getSpeciesOrDefault(itemstack).getGermlingColour(type, renderPass);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, new GermlingMeshDefinition());
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				((IAlleleTreeSpecies) allele).registerModels(item, manager);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private class GermlingMeshDefinition implements ItemMeshDefinition {
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			IAlleleTreeSpecies treeSpecies = getSpecies(stack);
			if (treeSpecies == null) {
				treeSpecies = TreeDefinition.Oak.getGenome().getPrimary();
			}
			return treeSpecies.getGermlingModel(type);
		}

	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ITree tree = TreeManager.treeRoot.getMember(stack);
		if (tree == null) {
			return EnumActionResult.PASS;
		}

		if (type == EnumGermlingType.SAPLING) {
			// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
			IBlockState hitBlock = worldIn.getBlockState(pos);
			if (!BlockUtil.isReplaceableBlock(hitBlock, hitBlock.getBlock())) {
				if (!worldIn.isAirBlock(pos.up())) {
					return EnumActionResult.FAIL;
				}
				pos = pos.up();
			}

			if (!tree.canStay(worldIn, pos)) {
				return EnumActionResult.FAIL;
			}

			if (TreeManager.treeRoot.plantSapling(worldIn, tree, playerIn.getGameProfile(), pos)) {
				Proxies.common.addBlockPlaceEffects(worldIn, pos, worldIn.getBlockState(pos));
				if (!playerIn.capabilities.isCreativeMode) {
					stack.stackSize--;
				}
				return EnumActionResult.SUCCESS;
			} else {
				return EnumActionResult.FAIL;
			}
		} else if (type == EnumGermlingType.POLLEN) {

			ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(worldIn, pos);

			if (checkPollinatable == null) {
				return EnumActionResult.PASS;
			}

			if (!checkPollinatable.canMateWith(tree)) {
				return EnumActionResult.FAIL;
			}

			IPollinatable pollinatable = GeneticsUtil.getOrCreatePollinatable(playerIn.getGameProfile(), worldIn, pos);

			if (!pollinatable.canMateWith(tree)) {
				return EnumActionResult.FAIL;
			}

			pollinatable.mateWith(tree);
			Proxies.common.sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.LEAF, worldIn, pos,
					worldIn.getBlockState(pos));
			if (!playerIn.capabilities.isCreativeMode) {
				stack.stackSize--;
			}
			return EnumActionResult.SUCCESS;

		} else {
			return EnumActionResult.PASS;
		}
	}

	@Override
	public float getFermentationModifier(ItemStack itemstack) {
		ITree tree = TreeManager.treeRoot.getMember(itemstack);
		if (tree == null) {
			return 1.0f;
		}

		return tree.getGenome().getSappiness() * 10;
	}
}
