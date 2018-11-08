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

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.recipes.IVariableFermentable;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.items.IColoredItem;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

public class ItemGermlingGE extends ItemGE implements IVariableFermentable, IColoredItem {

	private final EnumGermlingType type;

	public ItemGermlingGE(EnumGermlingType type) {
		super(Tabs.tabArboriculture);
		this.type = type;
	}

	@Override
	@Nullable
	public ITree getIndividual(ItemStack itemstack) {
		return TreeManager.treeRoot.getMember(itemstack);
	}

	@Override
	protected IAlleleTreeSpecies getSpecies(ItemStack itemStack) {
		return TreeGenome.getSpecies(itemStack);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.getTagCompound() == null) {
			return "Unknown";
		}
		IAlleleSpecies species = getSpecies(itemstack);

		String customTreeKey = "for.trees.custom." + type.getName() + "." + species.getUnlocalizedName().replace("trees.species.", "");
		if (Translator.canTranslateToLocal(customTreeKey)) {
			return Translator.translateToLocal(customTreeKey);
		}
		String typeString = Translator.translateToLocal("for.trees.grammar." + type.getName() + ".type");
		return Translator.translateToLocal("for.trees.grammar." + type.getName()).replaceAll("%SPECIES", species.getAlleleName()).replaceAll("%TYPE", typeString);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			addCreativeItems(subItems, true);
		}
	}

	public void addCreativeItems(NonNullList<ItemStack> subItems, boolean hideSecrets) {
		for (IIndividual individual : TreeManager.treeRoot.getIndividualTemplates()) {
			// Don't show secrets unless ordered to.
			if (hideSecrets && individual.isSecret() && !Config.isDebug) {
				continue;
			}

			subItems.add(TreeManager.treeRoot.getMemberStack(individual, type));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack itemstack, int renderPass) {
		return TreeGenome.getSpecies(itemstack).getGermlingColour(type, renderPass);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, new GermlingMeshDefinition());
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				((IAlleleTreeSpecies) allele).registerModels(item, manager, type);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private class GermlingMeshDefinition implements ItemMeshDefinition {
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			IAlleleTreeSpecies treeSpecies;
			if (!stack.hasTagCompound()) {
				treeSpecies = TreeDefinition.Oak.getGenome().getPrimary();
			} else {
				treeSpecies = getSpecies(stack);
			}
			return treeSpecies.getGermlingModel(type);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

		ItemStack itemStack = playerIn.getHeldItem(handIn);

		if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = raytraceresult.getBlockPos();

			ITree tree = TreeManager.treeRoot.getMember(itemStack);
			if (tree != null) {
				if (type == EnumGermlingType.SAPLING) {
					return onItemRightClickSapling(itemStack, worldIn, playerIn, pos, tree);
				} else if (type == EnumGermlingType.POLLEN) {
					return onItemRightClickPollen(itemStack, worldIn, playerIn, pos, tree);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, itemStack);
	}


	private static ActionResult<ItemStack> onItemRightClickPollen(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, BlockPos pos, ITree tree) {
		ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(worldIn, pos);
		if (checkPollinatable == null || !checkPollinatable.canMateWith(tree)) {
			return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
		}

		IPollinatable pollinatable = GeneticsUtil.getOrCreatePollinatable(playerIn.getGameProfile(), worldIn, pos, true);
		if (pollinatable == null || !pollinatable.canMateWith(tree)) {
			return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
		}

		if (worldIn.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			pollinatable.mateWith(tree);

			IBlockState blockState = worldIn.getBlockState(pos);
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
			NetworkUtil.sendNetworkPacket(packet, pos, worldIn);

			if (!playerIn.capabilities.isCreativeMode) {
				itemStackIn.shrink(1);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		}
	}


	private static ActionResult<ItemStack> onItemRightClickSapling(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, BlockPos pos, ITree tree) {
		// x, y, z are the coordinates of the block "hit", can thus either be the soil or tall grass, etc.
		IBlockState hitBlock = worldIn.getBlockState(pos);
		if (!hitBlock.getBlock().isReplaceable(worldIn, pos)) {
			if (!worldIn.isAirBlock(pos.up())) {
				return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
			}
			pos = pos.up();
		}

		if (tree.canStay(worldIn, pos)) {
			if (TreeManager.treeRoot.plantSapling(worldIn, tree, playerIn.getGameProfile(), pos)) {
				if (!playerIn.capabilities.isCreativeMode) {
					itemStackIn.shrink(1);
				}
				return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
	}

	@Override
	public float getFermentationModifier(ItemStack itemstack) {
		itemstack = GeneticsUtil.convertToGeneticEquivalent(itemstack);
		ITree tree = TreeManager.treeRoot.getMember(itemstack);
		if (tree == null) {
			return 1.0f;
		}
		return tree.getGenome().getSappiness() * 10;
	}

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		return 100;
	}
}
