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
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesType;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.config.Constants;
import forestry.core.genetics.SpeciesRoot;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;

public class TreeRoot extends SpeciesRoot implements ITreeRoot {

	public static final String UID = "rootTrees";
	private static int treeSpeciesCount = -1;
	private static ITreekeepingMode activeTreekeepingMode;
	public static final ArrayList<ITree> treeTemplates = new ArrayList<>();

	private final ArrayList<ITreekeepingMode> treekeepingModes = new ArrayList<>();

	public TreeRoot() {
		setResearchSuitability(new ItemStack(Blocks.SAPLING, 1, OreDictionary.WILDCARD_VALUE), 1.0f);
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public Class<? extends IIndividual> getMemberClass() {
		return ITree.class;
	}

	@Override
	public int getSpeciesCount() {
		if (treeSpeciesCount < 0) {
			treeSpeciesCount = 0;
			for (Entry<String, IAllele> entry : AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet()) {
				if (entry.getValue() instanceof IAlleleTreeSpecies) {
					if (((IAlleleTreeSpecies) entry.getValue()).isCounted()) {
						treeSpeciesCount++;
					}
				}
			}
		}

		return treeSpeciesCount;
	}

	@Override
	public boolean isMember(ItemStack itemstack) {
		return getType(itemstack) != null;
	}

	@Override
	public boolean isMember(ItemStack stack, ISpeciesType type) {
		return getType(stack) == type;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof ITree;
	}

	/* TREE SPECIFIC */
	@Override
	public EnumGermlingType getType(ItemStack stack) {
		if (stack == null) {
			return null;
		}

		Item item = stack.getItem();

		if (PluginArboriculture.items.sapling == item) {
			return EnumGermlingType.SAPLING;
		} else if (PluginArboriculture.items.pollenFertile == item) {
			return EnumGermlingType.POLLEN;
		}

		return null;
	}

	@Override
	public EnumGermlingType getIconType() {
		return EnumGermlingType.SAPLING;
	}

	@Override
	public ITree getTree(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileSapling)) {
			return null;
		}

		return ((TileSapling) tile).getTree();
	}

	@Override
	public ITree getMember(ItemStack itemstack) {
		if (!isMember(itemstack)) {
			return null;
		}
		if (itemstack.getTagCompound() == null) {
			return null;
		}

		return new Tree(itemstack.getTagCompound());
	}

	@Override
	public ITree getMember(NBTTagCompound compound) {
		return new Tree(compound);
	}

	@Override
	public ITree getTree(World world, ITreeGenome genome) {
		return new Tree(genome);
	}

	@Override
	public ItemStack getMemberStack(IIndividual tree, ISpeciesType type) {
		if (!isMember(tree)) {
			return null;
		}
		if (!(type instanceof EnumGermlingType)) {
			return null;
		}

		Item germlingItem;
		switch ((EnumGermlingType) type) {
			case SAPLING:
				germlingItem = PluginArboriculture.items.sapling;
				break;
			case POLLEN:
				germlingItem = PluginArboriculture.items.pollenFertile;
				break;
			default:
				throw new RuntimeException("Cannot instantiate a tree of type " + type);
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		tree.writeToNBT(nbttagcompound);

		ItemStack treeStack = new ItemStack(germlingItem);
		treeStack.setTagCompound(nbttagcompound);

		return treeStack;

	}

	@Override
	public boolean plantSapling(World world, ITree tree, GameProfile owner, BlockPos pos) {

		IBlockState state = PluginArboriculture.blocks.saplingGE.getDefaultState().withProperty(BlockSapling.TREE, tree.getGenome().getPrimary());
		boolean placed = world.setBlockState(pos, state, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		if (!placed) {
			return false;
		}

		Block block = BlockUtil.getBlock(world, pos);
		if (PluginArboriculture.blocks.saplingGE != block) {
			return false;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileSapling)) {
			world.setBlockToAir(pos);
			return false;
		}

		TileSapling sapling = (TileSapling) tile;
		sapling.setTree(tree.copy());
		sapling.setOwner(owner);

		return true;
	}

	@Override
	public boolean setFruitBlock(World world, IAlleleFruit allele, float sappiness, BlockPos pos) {

		EnumFacing facing = BlockUtil.getValidPodFacing(world, pos);
		if (facing == null) {
			return false;
		}

		BlockFruitPod fruitPod = PluginArboriculture.blocks.getFruitPod(allele);
		if (fruitPod == null) {
			return false;
		}

		IBlockState state = fruitPod.getDefaultState().withProperty(BlockDirectional.FACING, facing);
		boolean placed = world.setBlockState(pos, state);
		if (!placed) {
			return false;
		}

		Block block = BlockUtil.getBlock(world, pos);
		if (fruitPod != block) {
			return false;
		}

		TileFruitPod pod = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (pod == null) {
			world.setBlockToAir(pos);
			return false;
		}
		pod.setProperties(allele, sappiness);
		world.markBlockRangeForRenderUpdate(pos, pos);
		return true;
	}

	/* GENOME CONVERSIONS */
	@Override
	public ITreeGenome templateAsGenome(IAllele[] template) {
		return new TreeGenome(templateAsChromosomes(template));
	}

	@Override
	public ITreeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new TreeGenome(templateAsChromosomes(templateActive, templateInactive));
	}

	@Override
	public ITree templateAsIndividual(IAllele[] template) {
		return new Tree(templateAsGenome(template));
	}

	@Override
	public ITree templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive) {
		return new Tree(templateAsGenome(templateActive, templateInactive));
	}

	/* BREEDING TRACKER */
	@Override
	public IArboristTracker getBreedingTracker(World world, GameProfile player) {
		String filename = "ArboristTracker." + (player == null ? "common" : player.getId());
		ArboristTracker tracker = (ArboristTracker) world.loadItemData(ArboristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new ArboristTracker(filename);
			world.setItemData(filename, tracker);
		}

		tracker.setUsername(player);
		tracker.setWorld(world);

		return tracker;
	}

	/* BREEDING MODES */

	@Override
	public ArrayList<ITreekeepingMode> getTreekeepingModes() {
		return this.treekeepingModes;
	}

	@Override
	public ITreekeepingMode getTreekeepingMode(World world) {
		if (activeTreekeepingMode != null) {
			return activeTreekeepingMode;
		}

		// No Treekeeping mode yet, item it.
		IArboristTracker tracker = getBreedingTracker(world, null);
		String mode = tracker.getModeName();
		if (mode == null || mode.isEmpty()) {
			mode = PluginArboriculture.treekeepingMode;
		}

		setTreekeepingMode(world, mode);
		FMLCommonHandler.instance().getFMLLogger().debug("Set Treekeeping mode for a world to " + mode);

		return activeTreekeepingMode;
	}

	@Override
	public void registerTreekeepingMode(ITreekeepingMode mode) {
		treekeepingModes.add(mode);
	}

	@Override
	public void setTreekeepingMode(World world, String name) {
		activeTreekeepingMode = getTreekeepingMode(name);
		getBreedingTracker(world, null).setModeName(name);
	}

	@Override
	public ITreekeepingMode getTreekeepingMode(String name) {
		for (ITreekeepingMode mode : treekeepingModes) {
			if (mode.getName().equals(name) || mode.getName().equals(name.toLowerCase(Locale.ENGLISH))) {
				return mode;
			}
		}

		FMLCommonHandler.instance().getFMLLogger().debug("Failed to find a Treekeeping mode called '%s', reverting to fallback.");
		return treekeepingModes.get(0);
	}

	/* TEMPLATES */

	@Override
	public ArrayList<ITree> getIndividualTemplates() {
		return treeTemplates;
	}

	@Override
	public void registerTemplate(String identifier, IAllele[] template) {
		treeTemplates.add(new Tree(TreeManager.treeRoot.templateAsGenome(template)));
		speciesTemplates.put(identifier, template);
	}

	@Override
	public IAllele[] getDefaultTemplate() {
		return TreeDefinition.Oak.getTemplate();
	}

	/* MUTATIONS */
	private static final List<ITreeMutation> treeMutations = new ArrayList<>();

	@Override
	public List<ITreeMutation> getMutations(boolean shuffle) {
		if (shuffle) {
			Collections.shuffle(treeMutations);
		}
		return treeMutations;
	}

	@Override
	public void registerMutation(IMutation mutation) {
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getTemplate()[0].getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getAllele0().getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getAllele1().getUID())) {
			return;
		}

		treeMutations.add((ITreeMutation) mutation);
	}

	/* ILEAFTICKHANDLER */
	private final LinkedList<ILeafTickHandler> leafTickHandlers = new LinkedList<>();

	@Override
	public void registerLeafTickHandler(ILeafTickHandler handler) {
		leafTickHandlers.add(handler);
	}

	@Override
	public Collection<ILeafTickHandler> getLeafTickHandlers() {
		return leafTickHandlers;
	}

	@Override
	public IChromosomeType[] getKaryotype() {
		return EnumTreeChromosome.values();
	}

	@Override
	public IChromosomeType getKaryotypeKey() {
		return EnumTreeChromosome.SPECIES;
	}

}
