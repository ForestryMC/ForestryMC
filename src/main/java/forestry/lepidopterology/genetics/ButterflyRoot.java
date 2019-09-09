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
package forestry.lepidopterology.genetics;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.api.lepidopterology.genetics.IButterflyRoot;
import forestry.core.genetics.root.BreedingTrackerManager;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.tiles.TileCocoon;

public class ButterflyRoot extends IndividualRoot<IButterfly> implements IButterflyRoot, IBreedingTrackerHandler {

	private static int butterflySpeciesCount = -1;
	public static final String UID = "rootButterflies";
	private static final List<IButterfly> butterflyTemplates = new ArrayList<>();

	public ButterflyRoot(IRootContext<IButterfly> context) {
		super(context);
		BreedingTrackerManager.INSTANCE.registerTracker(UID, this);
	}

	@Override
	public IButterfly create(CompoundNBT compound) {
		return new Butterfly(compound);
	}

	@Override
	public IButterfly create(IGenome genome) {
		return new Butterfly(genome);
	}

	@Override
	public IButterfly create(IGenome genome, IGenome mate) {
		return new Butterfly(genome, mate);
	}

	@Override
	public IGenomeWrapper createWrapper(IGenome genome) {
		return () -> genome;
	}

	@Override
	public Class<? extends IButterfly> getMemberClass() {
		return IButterfly.class;
	}

	@Override
	public int getSpeciesCount() {
		if (butterflySpeciesCount < 0) {
			butterflySpeciesCount = 0;
			for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(ButterflyChromosomes.SPECIES)) {
				if (allele instanceof IAlleleButterflySpecies) {
					if (((IAlleleButterflySpecies) allele).isCounted()) {
						butterflySpeciesCount++;
					}
				}
			}
		}

		return butterflySpeciesCount;
	}

	@Override
	public EnumFlutterType getIconType() {
		return EnumFlutterType.BUTTERFLY;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof IButterfly;
	}

	@Override
	public EntityButterfly spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z) {
		return EntityUtil.spawnEntity(world, EntityButterfly.create(ModuleLepidopterology.BUTTERFLY_ENTITY_TYPE, world, butterfly, new BlockPos(x, y, z)), x, y, z);
	}

	@Override
	public BlockPos plantCocoon(IWorld world, BlockPos coordinates, @Nullable IButterfly caterpillar, GameProfile owner, int age, boolean createNursery) {
		if (caterpillar == null) {
			return BlockPos.ZERO;
		}

		BlockRegistryLepidopterology blocks = ModuleLepidopterology.getBlocks();

		BlockPos pos = getValidCocoonPos(world, coordinates, caterpillar, owner, createNursery);
		if (pos == BlockPos.ZERO) {
			return pos;
		}
		BlockState state = blocks.cocoon.getDefaultState();
		boolean placed = world.setBlockState(pos, state, 18);
		if (!placed) {
			return BlockPos.ZERO;
		}

		Block block = world.getBlockState(pos).getBlock();
		if (blocks.cocoon != block) {
			return BlockPos.ZERO;
		}

		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (cocoon == null) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 18);
			return BlockPos.ZERO;
		}

		cocoon.setCaterpillar(caterpillar);
		cocoon.getOwnerHandler().setOwner(owner);
		cocoon.setAge(age);

		return pos;
	}

	private BlockPos getValidCocoonPos(IWorld world, BlockPos pos, IButterfly caterpillar, GameProfile gameProfile, boolean createNursery) {
		if (isPositionValid(world, pos.down(), caterpillar, gameProfile, createNursery)) {
			return pos.down();
		}
		for (int tries = 0; tries < 3; tries++) {
			for (int y = 1; y < world.getRandom().nextInt(5); y++) {
				BlockPos coordinate = pos.add(world.getRandom().nextInt(6) - 3, -y, world.getRandom().nextInt(6) - 3);
				if (isPositionValid(world, coordinate, caterpillar, gameProfile, createNursery)) {
					return coordinate;
				}
			}
		}

		return BlockPos.ZERO;
	}

	public boolean isPositionValid(IWorld world, BlockPos pos, IButterfly caterpillar, GameProfile gameProfile, boolean createNursery) {
		BlockState blockState = world.getBlockState(pos);
		if (BlockUtil.canReplace(blockState, world, pos)) {
			BlockPos nurseryPos = pos.up();
			IButterflyNursery nursery = GeneticsUtil.getNursery(world, nurseryPos);
			if (isNurseryValid(nursery, caterpillar, gameProfile)) {
				return true;
			} else if (createNursery && GeneticsUtil.canCreateNursery(world, nurseryPos)) {
				nursery = GeneticsUtil.getOrCreateNursery(gameProfile, world, nurseryPos, false);
				return isNurseryValid(nursery, caterpillar, gameProfile);
			}
		}
		return false;
	}

	private boolean isNurseryValid(@Nullable IButterflyNursery nursery, IButterfly caterpillar, GameProfile gameProfile) {
		return nursery != null && nursery.canNurse(caterpillar);
	}

	@Override
	public boolean isMated(ItemStack stack) {
		IButterfly butterfly = getTypes().createIndividual(stack).orElse(null);
		return butterfly != null && !butterfly.getMate().isPresent();
	}

	/* BREEDING TRACKER */
	@Override
	public ILepidopteristTracker getBreedingTracker(IWorld world, @Nullable GameProfile player) {
		return BreedingTrackerManager.INSTANCE.getTracker(getUID(), world, player);
	}

	@Override
	public String getFileName(@Nullable GameProfile profile) {
		return "LepidopteristTracker." + (profile == null ? "common" : profile.getId());
	}

	@Override
	public IBreedingTracker createTracker(String fileName) {
		return new LepidopteristTracker(fileName);
	}

	@Override
	public void populateTracker(IBreedingTracker tracker, @Nullable World world, @Nullable GameProfile profile) {
		if (!(tracker instanceof LepidopteristTracker)) {
			return;
		}
		LepidopteristTracker arboristTracker = (LepidopteristTracker) tracker;
		arboristTracker.setWorld(world);
		arboristTracker.setUsername(profile);
	}

	@Override
	public IAlyzerPlugin getAlyzerPlugin() {
		return FlutterlyzerPlugin.INSTANCE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IDatabasePlugin getSpeciesPlugin() {
		return ButterflyPlugin.INSTANCE;
	}
}
