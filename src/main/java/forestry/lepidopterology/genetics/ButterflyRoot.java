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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesType;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyMutation;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.core.genetics.SpeciesRoot;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemRegistryLepidopterology;
import forestry.lepidopterology.tiles.TileCocoon;

public class ButterflyRoot extends SpeciesRoot implements IButterflyRoot {

	private static int butterflySpeciesCount = -1;
	public static final String UID = "rootButterflies";
	private static final List<IButterfly> butterflyTemplates = new ArrayList<>();


	@Override
	public String getUID() {
		return UID;
	}


	@Override
	public Class<? extends IIndividual> getMemberClass() {
		return IButterfly.class;
	}

	@Override
	public int getSpeciesCount() {
		if (butterflySpeciesCount < 0) {
			butterflySpeciesCount = 0;
			for (Entry<String, IAllele> entry : AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet()) {
				if (entry.getValue() instanceof IAlleleButterflySpecies) {
					if (((IAlleleButterflySpecies) entry.getValue()).isCounted()) {
						butterflySpeciesCount++;
					}
				}
			}
		}

		return butterflySpeciesCount;
	}

	@Override
	public boolean isMember(ItemStack stack) {
		return getType(stack) != null;
	}

	@Nullable
	@Override
	public EnumFlutterType getType(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		ItemRegistryLepidopterology butterflyItems = ModuleLepidopterology.getItems();
		Preconditions.checkState(butterflyItems != null);

		Item item = stack.getItem();
		if (butterflyItems.butterflyGE == item) {
			return EnumFlutterType.BUTTERFLY;
		} else if (butterflyItems.serumGE == item) {
			return EnumFlutterType.SERUM;
		} else if (butterflyItems.caterpillarGE == item) {
			return EnumFlutterType.CATERPILLAR;
		} else if (butterflyItems.cocoonGE == item) {
			return EnumFlutterType.COCOON;
		} else {
			return null;
		}
	}

	@Override
	public EnumFlutterType getIconType() {
		return EnumFlutterType.BUTTERFLY;
	}

	@Override
	public ISpeciesType[] getTypes() {
		return EnumFlutterType.values();
	}

	@Override
	public boolean isMember(ItemStack stack, ISpeciesType type) {
		return getType(stack) == type;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof IButterfly;
	}

	@Override
	public IButterfly getMember(ItemStack stack) {
		if (!isMember(stack) || stack.getTagCompound() == null) {
			return null;
		}

		return new Butterfly(stack.getTagCompound());
	}

	@Override
	public IButterfly getMember(NBTTagCompound compound) {
		return new Butterfly(compound);
	}

	@Override
	public ItemStack getMemberStack(IIndividual butterfly, ISpeciesType type) {
		Preconditions.checkArgument(type instanceof EnumFlutterType);
		ItemRegistryLepidopterology items = ModuleLepidopterology.getItems();
		Preconditions.checkState(items != null);

		Item butterflyItem;
		switch ((EnumFlutterType) type) {
			case SERUM:
				butterflyItem = items.serumGE;
				break;
			case CATERPILLAR:
				butterflyItem = items.caterpillarGE;
				break;
			case COCOON:
				butterflyItem = items.cocoonGE;
				break;
			case BUTTERFLY:
			default:
				butterflyItem = items.butterflyGE;
				break;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		butterfly.writeToNBT(nbttagcompound);
		ItemStack stack = new ItemStack(butterflyItem);
		stack.setTagCompound(nbttagcompound);
		if(type == EnumFlutterType.CATERPILLAR){
			ItemButterflyGE.setAge(stack, 0);
		}
		return stack;

	}

	@Override
	public EntityButterfly spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z) {
		return EntityUtil.spawnEntity(world, new EntityButterfly(world, butterfly, new BlockPos(x, y, z)), x, y, z);
	}

	@Override
	public BlockPos plantCocoon(World world, BlockPos coordinates, IButterfly caterpillar, GameProfile owner, int age, boolean createNursery) {
		if (caterpillar == null) {
			return BlockPos.ORIGIN;
		}

		BlockRegistryLepidopterology blocks = ModuleLepidopterology.getBlocks();

		BlockPos pos = getValidCocoonPos(world, coordinates, caterpillar, owner, createNursery);
		if(pos == BlockPos.ORIGIN){
			return pos;
		}
		IBlockState state = blocks.cocoon.getDefaultState();
		boolean placed = world.setBlockState(pos, state);
		if (!placed) {
			return BlockPos.ORIGIN;
		}

		Block block = world.getBlockState(pos).getBlock();
		if (blocks.cocoon != block) {
			return BlockPos.ORIGIN;
		}

		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (cocoon == null) {
			world.setBlockToAir(pos);
			return BlockPos.ORIGIN;
		}

		cocoon.setCaterpillar(caterpillar);
		cocoon.getOwnerHandler().setOwner(owner);
		cocoon.setAge(age);

		return pos;
	}

	private BlockPos getValidCocoonPos(World world, BlockPos pos, IButterfly caterpillar, GameProfile gameProfile, boolean createNursery) {
		if(isPositionValid(world, pos.down(), caterpillar, gameProfile, createNursery)){
			return pos.down();
		}
		for(int tries = 0;tries < 3;tries++){
			for(int y = 1;y < world.rand.nextInt(5);y++){
				BlockPos coordinate = pos.add(world.rand.nextInt(6)-3, -y, world.rand.nextInt(6)-3);
				if(isPositionValid(world, coordinate, caterpillar, gameProfile, createNursery)){
					return coordinate;
				}
			}
		}

		return BlockPos.ORIGIN;
	}
	
	public boolean isPositionValid(World world, BlockPos pos, IButterfly caterpillar, GameProfile gameProfile, boolean createNursery){
		IBlockState blockState = world.getBlockState(pos);
		if(BlockUtil.canReplace(blockState, world, pos)){
			BlockPos nurseryPos = pos.up();
			IButterflyNursery nursery = GeneticsUtil.getNursery(world, nurseryPos);
			if(isNurseryValid(nursery, caterpillar, gameProfile)){
				return true;
			}else if(createNursery && GeneticsUtil.canCreateNursery(world, nurseryPos)){
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
		IButterfly butterfly = getMember(stack);
		return butterfly != null && butterfly.getMate() != null;
	}

	/* GENOME CONVERSIONS */
	@Override
	public IButterfly templateAsIndividual(IAllele[] template) {
		return new Butterfly(templateAsGenome(template));
	}

	@Override
	public IButterfly templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive) {
		return new Butterfly(templateAsGenome(templateActive, templateInactive));
	}

	@Override
	public IButterflyGenome templateAsGenome(IAllele[] template) {
		return new ButterflyGenome(templateAsChromosomes(template));
	}

	@Override
	public IButterflyGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new ButterflyGenome(templateAsChromosomes(templateActive, templateInactive));
	}

	/* TEMPLATES */

	@Override
	public List<IButterfly> getIndividualTemplates() {
		return butterflyTemplates;
	}


	@Override
	public IAllele[] getDefaultTemplate() {
		return MothDefinition.Brimstone.getTemplate();
	}

	@Override
	public void registerTemplate(String identifier, IAllele[] template) {
		butterflyTemplates.add(ButterflyManager.butterflyRoot.templateAsIndividual(template));
		speciesTemplates.put(identifier, template);
	}

	/* MUTATIONS */
	private static final List<IButterflyMutation> butterflyMutations = new ArrayList<>();

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

		butterflyMutations.add((IButterflyMutation) mutation);
	}

	@Override
	public List<IButterflyMutation> getMutations(boolean shuffle) {
		if (shuffle) {
			Collections.shuffle(butterflyMutations);
		}
		return butterflyMutations;
	}

	/* BREEDING TRACKER */
	@Override
	public ILepidopteristTracker getBreedingTracker(World world, @Nullable GameProfile player) {
		String filename = "LepidopteristTracker." + (player == null ? "common" : player.getId());
		LepidopteristTracker tracker = (LepidopteristTracker) world.loadData(LepidopteristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new LepidopteristTracker(filename);
			world.setData(filename, tracker);
		}

		tracker.setUsername(player);
		tracker.setWorld(world);

		return tracker;
	}

	@Override
	public IChromosomeType[] getKaryotype() {
		return EnumButterflyChromosome.values();
	}

	@Override
	public IChromosomeType getSpeciesChromosomeType() {
		return EnumButterflyChromosome.SPECIES;
	}

	@Override
	public IAlyzerPlugin getAlyzerPlugin() {
		return FlutterlyzerPlugin.INSTANCE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IDatabasePlugin getSpeciesPlugin() {
		return ButterflyPlugin.INSTANCE;
	}
}
