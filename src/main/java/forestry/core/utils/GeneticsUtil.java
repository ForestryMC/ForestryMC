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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IArmorNaturalist;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.arboriculture.genetics.pollination.CheckPollinatableTree;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;

public class GeneticsUtil {

	private static Set<Block> ersatzSpecimenBlocks;

	private static Set<Block> getErsatzBlocks() {
		if (ersatzSpecimenBlocks == null) {
			ersatzSpecimenBlocks = new HashSet<>();
			for (ItemStack ersatzSpecimen : AlleleManager.ersatzSpecimen.keySet()) {
				Block ersatzBlock = ItemStackUtil.getBlock(ersatzSpecimen);
				if (ersatzBlock != null) {
					ersatzSpecimenBlocks.add(ersatzBlock);
				}
			}
		}
		return ersatzSpecimenBlocks;
	}

	private static boolean isErsatzBlock(Block block) {
		return block != null && getErsatzBlocks().contains(block);
	}

	public static boolean hasNaturalistEye(EntityPlayer player) {
		if (player == null) {
			return false;
		}

		ItemStack armorItemStack = player.inventory.armorInventory[3];
		if (armorItemStack == null) {
			return false;
		}

		Item armorItem = armorItemStack.getItem();
		if (!(armorItem instanceof IArmorNaturalist)) {
			return false;
		}

		IArmorNaturalist armorNaturalist = (IArmorNaturalist) armorItem;
		return armorNaturalist.canSeePollination(player, armorItemStack, true);
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
			return (IPollinatable) tile;
		}

		ITree pollen = getErsatzPollen(world, pos);
		if (pollen != null) {
			return new CheckPollinatableTree(pollen);
		}

		return null;
	}

	/**
	 * Returns an IPollinatable that can be mated. This will convert vanilla leaves to Forestry leaves.
	 */
	public static IPollinatable getOrCreatePollinatable(GameProfile owner, World world, final BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof IPollinatable) {
			return (IPollinatable) tile;
		}

		if (Config.pollinateVanillaTrees) {
			ITree pollen = getErsatzPollen(world, pos);
			if (pollen != null) {
				pollen.setLeaves(world, owner, pos);
				return (IPollinatable) world.getTileEntity(pos);
			}
		}

		return null;
	}

	public static ITree getErsatzPollen(World world, final BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (!isErsatzBlock(block)) {
			return null;
		}

		int meta = block.getMetaFromState(blockState);

		if (Blocks.LEAVES == block || Blocks.LEAVES2 == block) {
			if ((meta & 4) != 0) {
				// no-decay vanilla leaves. http://minecraft.gamepedia.com/Data_values#Leaves
				// Treat them as decorative and don't pollinate.
				return null;
			}
			if (block == Blocks.LEAVES2) {
				meta = meta + 4; //Dark Oak and Acacia are their own leaf block, but added on the end of sapling
			}
			block = Blocks.SAPLING;
		}
		ItemStack itemStack = new ItemStack(block, 1, meta);
		IIndividual tree = getGeneticEquivalent(itemStack);
		if (tree instanceof ITree) {
			return (ITree) tree;
		}

		return null;
	}

	public static IIndividual getGeneticEquivalent(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		Item item = itemStack.getItem();
		if (item instanceof ItemGE) {
			return ((ItemGE) item).getIndividual(itemStack);
		}

		for (Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSaplings.entrySet()) {
			if (ItemStackUtil.isIdenticalItem(itemStack, entry.getKey())) {
				return entry.getValue().copy();
			}
		}
		return null;
	}

	public static ItemStack convertSaplingToGeneticEquivalent(ItemStack foreign) {
		if (foreign == null) {
			return null;
		}

		if (TreeManager.treeRoot.isMember(foreign)) {
			return foreign;
		}

		IIndividual tree = getGeneticEquivalent(foreign);
		if (!(tree instanceof ITree)) {
			return null;
		}

		ItemStack ersatz = TreeManager.treeRoot.getMemberStack(tree, EnumGermlingType.SAPLING);
		ersatz.stackSize = foreign.stackSize;
		return ersatz;
	}

	public static int getResearchComplexity(IAlleleSpecies species, IChromosomeType speciesChromosome) {
		return 1 + getGeneticAdvancement(species, new HashSet<>(), speciesChromosome);
	}

	private static int getGeneticAdvancement(IAlleleSpecies species, Set<IAlleleSpecies> exclude, IChromosomeType speciesChromosome) {
		int highest = 0;
		exclude.add(species);

		for (IMutation mutation : species.getRoot().getPaths(species, speciesChromosome)) {
			highest = getHighestAdvancement(mutation.getAllele0(), highest, exclude, speciesChromosome);
			highest = getHighestAdvancement(mutation.getAllele1(), highest, exclude, speciesChromosome);
		}

		return 1 + highest;
	}

	private static int getHighestAdvancement(IAlleleSpecies mutationSpecies, int highest, Set<IAlleleSpecies> exclude, IChromosomeType speciesChromosome) {
		if (exclude.contains(mutationSpecies) || AlleleManager.alleleRegistry.isBlacklisted(mutationSpecies.getUID())) {
			return highest;
		}

		int otherAdvance = getGeneticAdvancement(mutationSpecies, exclude, speciesChromosome);
		return otherAdvance > highest ? otherAdvance : highest;
	}
}
