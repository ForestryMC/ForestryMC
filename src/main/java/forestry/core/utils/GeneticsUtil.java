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
package forestry.core.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.core.IArmorNaturalist;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.arboriculture.genetics.CheckPollinatable;
import forestry.arboriculture.genetics.CheckPollinatableTree;
import forestry.arboriculture.genetics.ICheckPollinatable;
import forestry.core.genetics.ItemGE;
import forestry.plugins.PluginArboriculture;

public class GeneticsUtil {

	private static Set<Material> ersatzSpecimenMaterials;

	private static Set<Material> getErsatzSpecimenMaterials() {
		if (ersatzSpecimenMaterials == null) {
			ersatzSpecimenMaterials = new HashSet<Material>();
			for (ItemStack ersatzSpecimen : AlleleManager.ersatzSpecimen.keySet()) {
				Block ersatzBlock = StackUtils.getBlock(ersatzSpecimen);
				if (ersatzBlock != null) {
					ersatzSpecimenMaterials.add(ersatzBlock.getMaterial());
				}
			}
		}
		return ersatzSpecimenMaterials;
	}

	private static boolean isErsatzMaterial(Block block) {
		return block != null && getErsatzSpecimenMaterials().contains(block.getMaterial());
	}

	public static boolean hasNaturalistEye(EntityPlayer player) {
		ItemStack armorItem = player.inventory.armorInventory[3];
		return armorItem != null && armorItem.getItem() instanceof IArmorNaturalist
				&& ((IArmorNaturalist) armorItem.getItem()).canSeePollination(player, armorItem, true);
	}

	public static boolean canNurse(IButterfly butterfly, World world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof IButterflyNursery) {
			return ((IButterflyNursery) tile).canNurse(butterfly);
		}

		// vanilla leaves can always be converted and then nurse
		return getErsatzPollen(world, pos) != null;
	}

	/**
	 * Returns an ICheckPollinatable that can be checked but not mated.
	 * Used to check for pollination traits without altering the world by changing vanilla leaves to forestry ones.
	 */
	public static ICheckPollinatable getCheckPollinatable(World world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof IPollinatable) {
			return new CheckPollinatable((IPollinatable) tile);
		}

		ITree pollen = getErsatzPollen(world, pos);
		if (pollen != null) {
			return new CheckPollinatableTree(pollen);
		}

		return null;
	}

	public static IPollinatable getOrCreatePollinatable(GameProfile owner, World world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof IPollinatable) {
			return (IPollinatable) tile;
		}

		ITree pollen = getErsatzPollen(world, pos);
		if (pollen != null) {
			PluginArboriculture.treeInterface.setLeaves(world, pollen, owner, pos);
			return (IPollinatable) world.getTileEntity(pos);
		}

		return null;
	}

	public static ITree getErsatzPollen(World world, final BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!isErsatzMaterial(block)) {
			return null;
		}

		int meta = block.getMetaFromState(state);

		if (Blocks.leaves == block || Blocks.leaves2 == block) {
			if ((meta & 4) != 0) {
				// no-decay vanilla leaves. http://minecraft.gamepedia.com/Data_values#Leaves
				// Treat them as decorative and don't pollinate.
				return null;
			}
			meta %= 3;
		}
		ItemStack itemStack = new ItemStack(block, meta);
		IIndividual tree = getGeneticEquivalent(itemStack);
		if (tree instanceof ITree) {
			return (ITree) tree;
		}

		return null;
	}

	public static IIndividual getGeneticEquivalent(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item instanceof ItemGE) {
			return ((ItemGE) item).getIndividual(itemStack);
		}

		for (Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSaplings.entrySet()) {
			if (StackUtils.isIdenticalItem(itemStack, entry.getKey())) {
				return entry.getValue().copy();
			}
		}
		return null;
	}

	public static ItemStack convertSaplingToGeneticEquivalent(ItemStack foreign) {
		IIndividual tree = getGeneticEquivalent(foreign);
		if (!(tree instanceof ITree)) {
			return null;
		}

		ItemStack ersatz = PluginArboriculture.treeInterface.getMemberStack(tree, EnumGermlingType.SAPLING.ordinal());
		ersatz.stackSize = foreign.stackSize;
		return ersatz;
	}
}
