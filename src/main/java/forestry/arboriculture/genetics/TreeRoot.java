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

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeChromosome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.config.Constants;
import forestry.core.genetics.SpeciesRoot;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.plugins.PluginArboriculture;

public class TreeRoot extends SpeciesRoot<TreeChromosome> implements ITreeRoot {

	public static final String UID = "rootTrees";
	private static int treeSpeciesCount = -1;
	private static ITreekeepingMode activeTreekeepingMode;
	public static final ArrayList<ITree> treeTemplates = new ArrayList<>();

	@Nonnull
	private final ArrayList<ITreekeepingMode> treekeepingModes = new ArrayList<>();

	public TreeRoot() {
		super(TreeChromosome.class);
		setResearchSuitability(new ItemStack(Blocks.sapling, 1, OreDictionary.WILDCARD_VALUE), 1.0f);
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public Class<ITree> getMemberClass() {
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
		return getType(itemstack) != EnumGermlingType.NONE;
	}

	@Override
	public boolean isMember(ItemStack stack, int type) {
		return getType(stack).ordinal() == type;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof ITree;
	}

	/* TREE SPECIFIC */
	@Override
	public EnumGermlingType getType(ItemStack stack) {
		if (stack == null) {
			return EnumGermlingType.NONE;
		}

		Item item = stack.getItem();

		if (PluginArboriculture.items.sapling == item) {
			return EnumGermlingType.SAPLING;
		} else if (PluginArboriculture.items.pollenFertile == item) {
			return EnumGermlingType.POLLEN;
		}

		return EnumGermlingType.NONE;
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
	public ItemStack getMemberStack(IIndividual tree, int type) {
		if (!isMember(tree)) {
			return null;
		}

		Item germlingItem;
		switch (EnumGermlingType.VALUES[type]) {
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

		boolean placed = world.setBlockState(pos, PluginArboriculture.blocks.saplingGE.getStateFromMeta(0), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
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
	public boolean setFruitBlock(World world, IAlleleFruit allele, float sappiness, short[] indices, BlockPos pos) {

		int direction = BlockUtil.getDirectionalMetadata(world, pos);
		if (direction < 0) {
			return false;
		}

		boolean placed = world.setBlockState(pos, PluginArboriculture.blocks.pods.getStateFromMeta(direction), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		if (!placed) {
			return false;
		}

		Block block = BlockUtil.getBlock(world, pos);
		if (PluginArboriculture.blocks.pods != block) {
			return false;
		}

		TileFruitPod pod = BlockFruitPod.getPodTile(world, pos);
		if (pod == null) {
			world.setBlockToAir(pos);
			return false;
		}
		pod.setFruit(allele, sappiness, indices);
		world.markBlockForUpdate(pos);
		return true;
	}

	/* GENOME CONVERSIONS */
	@Override
	public ITreeGenome templateAsGenome(ImmutableMap<TreeChromosome, IAllele> template) {
		return new TreeGenome(templateAsChromosomes(template));
	}

	@Override
	public ITreeGenome templateAsGenome(ImmutableMap<TreeChromosome, IAllele> templateActive, ImmutableMap<TreeChromosome, IAllele> templateInactive) {
		return new TreeGenome(templateAsChromosomes(templateActive, templateInactive));
	}

	@Nonnull
	@Override
	public ITreeGenome chromosomesAsGenome(ImmutableMap<TreeChromosome, IChromosome> chromosomes) {
		return new TreeGenome(chromosomes);
	}

	@Nonnull
	@Override
	public ITree templateAsIndividual(ImmutableMap<TreeChromosome, IAllele> template) {
		return new Tree(templateAsGenome(template));
	}

	@Nonnull
	@Override
	public ITree templateAsIndividual(ImmutableMap<TreeChromosome, IAllele> templateActive, ImmutableMap<TreeChromosome, IAllele> templateInactive) {
		return new Tree(templateAsGenome(templateActive, templateInactive));
	}

	/* BREEDING TRACKER */
	@Nonnull
	@Override
	public IArboristTracker getBreedingTracker(@Nonnull World world, @Nonnull GameProfile player) {
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

	@Nonnull
	@Override
	public List<ITreekeepingMode> getModes() {
		return this.treekeepingModes;
	}

	@Override
	public void resetMode() {
		activeTreekeepingMode = null;
	}

	@Nonnull
	@Override
	public ITreekeepingMode getMode(@Nonnull World world) {
		if (activeTreekeepingMode != null) {
			return activeTreekeepingMode;
		}

		// No Treekeeping mode yet, item it.
		IArboristTracker tracker = getBreedingTracker(world, null);
		String mode = tracker.getModeName();
		if (mode == null || mode.isEmpty()) {
			mode = PluginArboriculture.treekeepingMode;
		}

		setMode(world, mode);
		FMLCommonHandler.instance().getFMLLogger().debug("Set Treekeeping mode for a world to " + mode);

		return activeTreekeepingMode;
	}

	@Override
	public void registerMode(@Nonnull ITreekeepingMode mode) {
		treekeepingModes.add(mode);
	}

	@Override
	public void setMode(@Nonnull World world, @Nonnull String name) {
		activeTreekeepingMode = getMode(name);
		getBreedingTracker(world, null).setModeName(name);
	}

	@Nonnull
	@Override
	public ITreekeepingMode getMode(@Nonnull String name) {
		for (ITreekeepingMode mode : treekeepingModes) {
			if (mode.getName().equals(name) || mode.getName().equals(name.toLowerCase(Locale.ENGLISH))) {
				return mode;
			}
		}

		Log.error("Failed to find a Treekeeping mode called '%s', reverting to fallback.");
		return treekeepingModes.get(0);
	}

	/* TEMPLATES */

	@Override
	public ArrayList<ITree> getIndividualTemplates() {
		return treeTemplates;
	}

	@Override
	public void registerTemplate(String identifier, ImmutableMap<TreeChromosome, IAllele> template) {
		treeTemplates.add(new Tree(TreeManager.treeRoot.templateAsGenome(template)));
		speciesTemplates.put(identifier, template);
	}

	@Override
	public ImmutableMap<TreeChromosome, IAllele> getDefaultTemplate() {
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
	public void registerMutation(IMutation<TreeChromosome> mutation) {
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getResultTemplate().get(TreeChromosome.SPECIES).getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getSpecies0().getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getSpecies1().getUID())) {
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

	@Nonnull
	@Override
	public TreeChromosome[] getKaryotype() {
		return TreeChromosome.values();
	}

	@Nonnull
	@Override
	public TreeChromosome getKaryotypeKey() {
		return TreeChromosome.SPECIES;
	}

}
