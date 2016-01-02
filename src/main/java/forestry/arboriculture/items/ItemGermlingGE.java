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

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.StringUtil;

public class ItemGermlingGE extends ItemGE implements IVariableFermentable {

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
	protected int getDefaultPrimaryColour() {
		return 0;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (!itemstack.hasTagCompound()) {
			return "Unknown";
		}
		IAlleleSpecies species = getSpecies(itemstack);

		String customTreeKey = "trees.custom." + type.getName() + "." + species.getUnlocalizedName().replace("trees.species.", "");
		if (StringUtil.canTranslate(customTreeKey)) {
			return StringUtil.localize(customTreeKey);
		}
		return StringUtil.localize("trees.grammar." + type.getName()).replaceAll("%SPECIES", species.getName()).replaceAll("%TYPE", StringUtil.localize("trees.grammar." + type.getName() + ".type"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		addCreativeItems(itemList, true);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addCreativeItems(List itemList, boolean hideSecrets) {
		for (IIndividual individual : TreeManager.treeRoot.getIndividualTemplates()) {
			// Don't show secrets unless ordered to.
			if (hideSecrets && individual.isSecret() && !Config.isDebug) {
				continue;
			}

			itemList.add(TreeManager.treeRoot.getMemberStack(individual, type.ordinal()));
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		return getSpeciesOrDefault(itemstack).getGermlingColour(type, renderPass);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, new GermlingMeshDefinition());
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				((IAlleleTreeSpecies) allele).registerModels(manager);
			}
		}
	}

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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		ITree tree = TreeManager.treeRoot.getMember(stack);
		if (tree == null) {
			return false;
		}

		if (type == EnumGermlingType.SAPLING) {
			// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
			int yShift;
			if (!BlockUtil.isReplaceableBlock(world, pos)) {
				if (!world.isAirBlock(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()))) {
					return false;
				}
				yShift = 1;
			} else {
				yShift = 0;
			}
			BlockPos posS = pos.add(0, yShift, 0);

			if (!tree.canStay(world, posS)) {
				return false;
			}

			if (TreeManager.treeRoot.plantSapling(world, tree, player.getGameProfile(), posS)) {
				Proxies.common.addBlockPlaceEffects(world, pos, world.getBlockState(posS));
				if (!player.capabilities.isCreativeMode) {
					stack.stackSize--;
				}
				return true;
			} else {
				return false;
			}
		} else if (type == EnumGermlingType.POLLEN) {

			ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(world, pos);

			if (checkPollinatable == null) {
				return false;
			}

			if (!checkPollinatable.canMateWith(tree)) {
				return false;
			}

			IPollinatable pollinatable = GeneticsUtil.getOrCreatePollinatable(player.getGameProfile(), world, pos);

			if (!pollinatable.canMateWith(tree)) {
				return false;
			}

			pollinatable.mateWith(tree);
			Proxies.common.sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.LEAF, world, pos,
					world.getBlockState(pos));
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
			}
			return true;

		} else {
			return false;
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
