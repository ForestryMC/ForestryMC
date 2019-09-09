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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IGenomeWrapper;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootContext;
import genetics.api.root.IndividualRoot;

import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IPollinatable;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.genetics.root.BreedingTrackerManager;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.RenderUtil;

public class TreeRoot extends IndividualRoot<ITree> implements ITreeRoot, IBreedingTrackerHandler {
	public static final String UID = "rootTrees";
	private static int treeSpeciesCount = -1;
	@Nullable
	private static ITreekeepingMode activeTreekeepingMode;

	private final Map<IFruitFamily, Collection<IFruitProvider>> providersForFamilies = new HashMap<>();
	private final List<ITreekeepingMode> treekeepingModes = new ArrayList<>();

	public TreeRoot(IRootContext<ITree> context) {
		super(context);
		BreedingTrackerManager.INSTANCE.registerTracker(UID, this);
	}

	@Override
	public Class<? extends ITree> getMemberClass() {
		return ITree.class;
	}

	@Override
	public int getSpeciesCount() {
		if (treeSpeciesCount < 0) {
			treeSpeciesCount = 0;
			for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(TreeChromosomes.SPECIES)) {
				if (allele instanceof IAlleleTreeSpecies) {
					if (((IAlleleTreeSpecies) allele).isCounted()) {
						treeSpeciesCount++;
					}
				}
			}
		}

		return treeSpeciesCount;
	}

	@Override
	public ITree create(CompoundNBT compound) {
		return new Tree(compound);
	}

	@Override
	public ITree create(IGenome genome) {
		return new Tree(genome);
	}

	@Override
	public ITree create(IGenome genome, IGenome mate) {
		return new Tree(genome, mate);
	}

	@Override
	public IGenomeWrapper createWrapper(IGenome genome) {
		return () -> genome;
	}

	@Override
	public ITree getTree(World world, IGenome genome) {
		return create(genome);
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof ITree;
	}

	@Override
	public boolean isMember(ItemStack itemstack) {
		return getTypes().getType(itemstack).isPresent();
	}

	/* TREE SPECIFIC */
	@Override
	public EnumGermlingType getIconType() {
		return EnumGermlingType.SAPLING;
	}

	@Override
	public ITree getTree(World world, BlockPos pos) {
		return TileUtil.getResultFromTile(world, pos, TileSapling.class, TileSapling::getTree);
	}

	@Override
	public boolean plantSapling(World world, ITree tree, GameProfile owner, BlockPos pos) {
		BlockRegistryArboriculture blocks = ModuleArboriculture.getBlocks();
		BlockState state = blocks.saplingGE.getDefaultState().with(BlockSapling.TREE, tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES));
		boolean placed = world.setBlockState(pos, state);
		if (!placed) {
			return false;
		}

		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (blocks.saplingGE != block) {
			return false;
		}

		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return false;
		}

		sapling.setTree(tree.copy());
		sapling.getOwnerHandler().setOwner(owner);

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, blockState);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		return true;
	}

	@Override
	public boolean setFruitBlock(IWorld world, IGenome genome, IAlleleFruit allele, float yield, BlockPos pos) {
		BlockRegistryArboriculture blocks = ModuleArboriculture.getBlocks();

		Direction facing = BlockUtil.getValidPodFacing(world, pos);
		if (facing != null) {

			BlockFruitPod fruitPod = blocks.getFruitPod(allele);
			if (fruitPod != null) {

				BlockState state = fruitPod.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, facing);
				boolean placed = world.setBlockState(pos, state, 18);
				if (placed) {

					Block block = world.getBlockState(pos).getBlock();
					if (fruitPod == block) {

						TileFruitPod pod = TileUtil.getTile(world, pos, TileFruitPod.class);
						if (pod != null) {
							pod.setProperties(genome, allele, yield);
							RenderUtil.markForUpdate(pos);
							return true;
						} else {
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), 18);
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	/* BREEDING TRACKER */
	@Override
	public IArboristTracker getBreedingTracker(IWorld world, @Nullable GameProfile player) {
		return BreedingTrackerManager.INSTANCE.getTracker(getUID(), world, player);
	}

	@Override
	public String getFileName(@Nullable GameProfile profile) {
		return "ArboristTracker." + (profile == null ? "common" : profile.getId());
	}

	@Override
	public IBreedingTracker createTracker(String fileName) {
		return new ArboristTracker(fileName);
	}

	@Override
	public void populateTracker(IBreedingTracker tracker, @Nullable World world, @Nullable GameProfile profile) {
		if (!(tracker instanceof ArboristTracker)) {
			return;
		}
		ArboristTracker arboristTracker = (ArboristTracker) tracker;
		arboristTracker.setWorld(world);
		arboristTracker.setUsername(profile);
	}

	/* BREEDING MODES */

	@Override
	public List<ITreekeepingMode> getTreekeepingModes() {
		return this.treekeepingModes;
	}

	@Override
	public ITreekeepingMode getTreekeepingMode(IWorld world) {
		if (activeTreekeepingMode != null) {
			return activeTreekeepingMode;
		}

		// No Treekeeping mode yet, item it.
		IArboristTracker tracker = getBreedingTracker(world, null);
		String modeName = tracker.getModeName();
		ITreekeepingMode mode = getTreekeepingMode(modeName);
		Preconditions.checkNotNull(mode);
		setTreekeepingMode(world, mode);
		Log.debug("Set Treekeeping mode for a world to " + mode);

		return activeTreekeepingMode;
	}

	@Override
	public void registerTreekeepingMode(ITreekeepingMode mode) {
		treekeepingModes.add(mode);
	}

	@Override
	public void setTreekeepingMode(IWorld world, ITreekeepingMode mode) {
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

		Log.debug("Failed to find a Treekeeping mode called '%s', reverting to fallback.");
		return treekeepingModes.get(0);
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
	public IAlyzerPlugin getAlyzerPlugin() {
		return TreeAlyzerPlugin.INSTANCE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
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
		if (pollen.setLeaves(world, owner, pos, world.rand)) {
			return TileUtil.getTile(world, pos, IPollinatable.class);
		} else {
			return null;
		}
	}

	@Override
	public Collection<IFruitProvider> getFruitProvidersForFruitFamily(IFruitFamily fruitFamily) {
		if (providersForFamilies.isEmpty()) {
			@SuppressWarnings("unchecked")
			Collection<IAlleleFruit> fruitAlleles = (Collection<IAlleleFruit>) (Object) GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(TreeChromosomes.FRUITS);
			for (IAlleleFruit alleleFruit : fruitAlleles) {
				IFruitProvider fruitProvider = alleleFruit.getProvider();
				Collection<IFruitProvider> fruitProviders = providersForFamilies.computeIfAbsent(fruitProvider.getFamily(), k -> new ArrayList<>());
				fruitProviders.add(fruitProvider);
			}
		}

		return providersForFamilies.computeIfAbsent(fruitFamily, k -> new ArrayList<>());
	}
}
