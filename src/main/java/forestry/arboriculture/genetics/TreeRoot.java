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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IPollinatable;
import forestry.api.genetics.ISpeciesType;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.genetics.SpeciesRoot;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.NetworkUtil;

public class TreeRoot extends SpeciesRoot implements ITreeRoot {
	public static final String UID = "rootTrees";
	private static int treeSpeciesCount = -1;
	@Nullable
	private static ITreekeepingMode activeTreekeepingMode;
	public static final List<ITree> treeTemplates = new ArrayList<>();

	private final Map<IFruitFamily, Collection<IFruitProvider>> providersForFamilies = new HashMap<>();
	private final List<ITreekeepingMode> treekeepingModes = new ArrayList<>();

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
	@Nullable
	public EnumGermlingType getType(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		ItemRegistryArboriculture items = ModuleArboriculture.getItems();

		Item item = stack.getItem();
		if (items.sapling == item) {
			return EnumGermlingType.SAPLING;
		} else if (items.pollenFertile == item) {
			return EnumGermlingType.POLLEN;
		}

		return null;
	}

	@Override
	public EnumGermlingType getIconType() {
		return EnumGermlingType.SAPLING;
	}

	@Override
	public ISpeciesType[] getTypes() {
		return EnumGermlingType.values();
	}

	@Override
	public ITree getTree(World world, BlockPos pos) {
		return TileUtil.getResultFromTile(world, pos, TileSapling.class, TileSapling::getTree);
	}

	@Override
	@Nullable
	public ITree getMember(ItemStack itemstack) {
		if (!isMember(itemstack) || itemstack.getTagCompound() == null) {
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
		Preconditions.checkArgument(tree instanceof ITree, "individual is not a tree");
		Preconditions.checkArgument(type instanceof EnumGermlingType, "type is not an EnumGermlingType");
		ItemRegistryArboriculture items = ModuleArboriculture.getItems();

		Item germlingItem;
		switch ((EnumGermlingType) type) {
			case SAPLING:
				germlingItem = items.sapling;
				break;
			case POLLEN:
				germlingItem = items.pollenFertile;
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
		BlockRegistryArboriculture blocks = ModuleArboriculture.getBlocks();

		IBlockState state = blocks.saplingGE.getDefaultState().withProperty(BlockSapling.TREE, tree.getGenome().getPrimary());
		boolean placed = world.setBlockState(pos, state);
		if (!placed) {
			return false;
		}

		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (blocks.saplingGE != block) {
			return false;
		}

		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null) {
			world.setBlockToAir(pos);
			return false;
		}

		sapling.setTree(tree.copy());
		sapling.getOwnerHandler().setOwner(owner);

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		return true;
	}

	@Override
	public boolean setFruitBlock(World world, ITreeGenome genome, IAlleleFruit allele, float sappiness, BlockPos pos) {
		BlockRegistryArboriculture blocks = ModuleArboriculture.getBlocks();

		EnumFacing facing = BlockUtil.getValidPodFacing(world, pos);
		if (facing != null) {

			BlockFruitPod fruitPod = blocks.getFruitPod(allele);
			if (fruitPod != null) {

				IBlockState state = fruitPod.getDefaultState().withProperty(BlockHorizontal.FACING, facing);
				boolean placed = world.setBlockState(pos, state);
				if (placed) {

					Block block = world.getBlockState(pos).getBlock();
					if (fruitPod == block) {

						TileFruitPod pod = TileUtil.getTile(world, pos, TileFruitPod.class);
						if (pod != null) {
							pod.setProperties(genome, allele, sappiness);
							world.markBlockRangeForRenderUpdate(pos, pos);
							return true;
						} else {
							world.setBlockToAir(pos);
							return false;
						}
					}
				}
			}
		}
		return false;
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
	public IArboristTracker getBreedingTracker(World world, @Nullable GameProfile player) {
		String filename = "ArboristTracker." + (player == null ? "common" : player.getId());
		ArboristTracker tracker = (ArboristTracker) world.loadData(ArboristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new ArboristTracker(filename);
			world.setData(filename, tracker);
		}

		tracker.setUsername(player);
		tracker.setWorld(world);

		return tracker;
	}

	/* BREEDING MODES */

	@Override
	public List<ITreekeepingMode> getTreekeepingModes() {
		return this.treekeepingModes;
	}

	@Override
	public ITreekeepingMode getTreekeepingMode(World world) {
		if (activeTreekeepingMode != null) {
			return activeTreekeepingMode;
		}

		// No Treekeeping mode yet, item it.
		IArboristTracker tracker = getBreedingTracker(world, null);
		String modeName = tracker.getModeName();
		ITreekeepingMode mode = getTreekeepingMode(modeName);
		Preconditions.checkNotNull(mode);
		setTreekeepingMode(world, mode);
		FMLCommonHandler.instance().getFMLLogger().debug("Set Treekeeping mode for a world to " + mode);

		return activeTreekeepingMode;
	}

	@Override
	public void registerTreekeepingMode(ITreekeepingMode mode) {
		treekeepingModes.add(mode);
	}

	@Override
	public void setTreekeepingMode(World world, ITreekeepingMode mode) {
		activeTreekeepingMode = mode;
		getBreedingTracker(world, null).setModeName(mode.getName());
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
	public List<ITree> getIndividualTemplates() {
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
	public IChromosomeType getSpeciesChromosomeType() {
		return EnumTreeChromosome.SPECIES;
	}

	@Override
	public IAlyzerPlugin getAlyzerPlugin() {
		return TreeAlyzerPlugin.INSTANCE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IDatabasePlugin getSpeciesPlugin() {
		return TreePlugin.INSTANCE;
	}

	@Override
	public ICheckPollinatable createPollinatable(IIndividual individual) {
		Preconditions.checkArgument(individual instanceof ITree, "individual must be a tree");
		return new CheckPollinatableTree((ITree) individual);
	}

	@Override
	@Nullable
	public IPollinatable tryConvertToPollinatable(@Nullable GameProfile owner, World world, BlockPos pos, IIndividual individual) {
		Preconditions.checkArgument(individual instanceof ITree, "pollen must be an instance of ITree");
		ITree pollen = (ITree) individual;
		if (pollen.setLeaves(world, owner, pos)) {
			return TileUtil.getTile(world, pos, IPollinatable.class);
		} else {
			return null;
		}
	}

	@Override
	public Collection<IFruitProvider> getFruitProvidersForFruitFamily(IFruitFamily fruitFamily) {
		if (providersForFamilies.isEmpty()) {
			@SuppressWarnings("unchecked")
			Collection<IAlleleFruit> fruitAlleles = (Collection<IAlleleFruit>) (Object) AlleleManager.alleleRegistry.getRegisteredAlleles(EnumTreeChromosome.FRUITS);
			for (IAlleleFruit alleleFruit : fruitAlleles) {
				IFruitProvider fruitProvider = alleleFruit.getProvider();
				Collection<IFruitProvider> fruitProviders = providersForFamilies.computeIfAbsent(fruitProvider.getFamily(), k -> new ArrayList<>());
				fruitProviders.add(fruitProvider);
			}
		}

		return providersForFamilies.computeIfAbsent(fruitFamily, k -> new ArrayList<>());
	}
}
