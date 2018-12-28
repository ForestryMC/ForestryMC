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
package forestry.arboriculture.tiles;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.Constants;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.apiculture.ModuleApiculture;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.network.IRipeningPacketReceiver;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.utils.ColourUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.NetworkUtil;

public class TileLeaves extends TileTreeContainer implements IPollinatable, IFruitBearer, IButterflyNursery, IRipeningPacketReceiver {

	private int colourFruits;

	@Nullable
	private ResourceLocation fruitSprite;
	@Nullable
	private IAlleleTreeSpecies species;
	@Nullable
	private IButterfly caterpillar;

	private boolean isFruitLeaf;
	private boolean checkFruit = true;
	private boolean isPollinatedState;
	private int ripeningTime;
	private short ripeningPeriod = Short.MAX_VALUE - 1;

	private int maturationTime;
	private int damage;

	private IEffectData effectData[] = new IEffectData[2];

	/**
	 * Worldgen trees used to create TileLeaves, but now they use BlockDefaultLeaves instead.
	 * We add a check to convert the TileLeaves into the new format.
	 * This boolean keeps track of whether this leaf has checked if it should replace itself.
	 */
	private boolean checkedForConversionToDefaultLeaves;

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		ripeningTime = nbttagcompound.getShort("RT");
		damage = nbttagcompound.getInteger("ENC");
		isFruitLeaf = nbttagcompound.getBoolean("FL");
		checkFruit = !nbttagcompound.hasKey("FL", Constants.NBT.TAG_ANY_NUMERIC);

		if (nbttagcompound.hasKey("CATER")) {
			maturationTime = nbttagcompound.getInteger("CATMAT");
			caterpillar = ButterflyManager.butterflyRoot.getMember(nbttagcompound.getCompoundTag("CATER"));
		}

		ITree tree = getTree();
		if (tree != null) {
			setTree(tree);
		}
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound = super.writeToNBT(nbtTagCompound);

		nbtTagCompound.setInteger("RT", getRipeningTime());
		nbtTagCompound.setInteger("ENC", damage);
		nbtTagCompound.setBoolean("FL", isFruitLeaf);

		if (caterpillar != null) {
			nbtTagCompound.setInteger("CATMAT", maturationTime);

			NBTTagCompound caterpillarNbt = new NBTTagCompound();
			caterpillar.writeToNBT(caterpillarNbt);
			nbtTagCompound.setTag("CATER", caterpillarNbt);
		}
		return nbtTagCompound;
	}

	@Override
	public void onBlockTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		ITree tree = getTree();
		if (tree == null) {
			return;
		}

		ITreeGenome genome = tree.getGenome();
		IAlleleTreeSpecies primary = genome.getPrimary();

		if (!checkedForConversionToDefaultLeaves) {
			if (shouldConvertToDefaultLeaves()) {
				IBlockState defaultLeaves = null;
				if (isFruitLeaf) {
					defaultLeaves = ModuleArboriculture.getBlocks().getDefaultLeavesFruit(primary.getUID());
				}
				if(defaultLeaves == null) {
					defaultLeaves = ModuleArboriculture.getBlocks().getDefaultLeaves(primary.getUID());
				}
				worldIn.setBlockState(getPos(), defaultLeaves);
				return;
			}
			checkedForConversionToDefaultLeaves = true;
		}

		boolean isDestroyed = isDestroyed(tree, damage);
		for (ILeafTickHandler tickHandler : primary.getRoot().getLeafTickHandlers()) {
			if (tickHandler.onRandomLeafTick(tree, world, rand, getPos(), isDestroyed)) {
				return;
			}
		}

		if (isDestroyed) {
			return;
		}

		if (damage > 0) {
			damage--;
		}

		if (hasFruit() && getRipeningTime() < ripeningPeriod) {
			ITreekeepingMode treekeepingMode = TreeManager.treeRoot.getTreekeepingMode(world);
			float sappinessModifier = treekeepingMode.getSappinessModifier(genome, 1f);
			float sappiness = genome.getSappiness() * sappinessModifier;

			if (rand.nextFloat() < sappiness) {
				ripeningTime++;
				sendNetworkUpdateRipening();
			}
		}

		if (caterpillar != null) {
			matureCaterpillar();
		}

		effectData = tree.doEffect(effectData, world, getPos());
	}

	/**
	 * Worldgen trees with default genomes should be converted to leaves without tile entities.
	 */
	private boolean shouldConvertToDefaultLeaves() {
		if (getOwnerHandler().getOwner() == null) { // null owner likely means it's a worldgen tree
			ITree tree = getTree();
			return tree != null && tree.getGenome().matchesTemplateGenome();
		}
		return false;
	}

	@Override
	public void setTree(ITree tree) {
		ITree oldTree = getTree();
		super.setTree(tree);

		ITreeGenome genome = tree.getGenome();
		species = genome.getPrimary();

		if (oldTree != null && !tree.equals(oldTree)) {
			checkFruit = true;
		}

		if (tree.canBearFruit() && checkFruit && world != null && !world.isRemote) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			if (fruitProvider.isFruitLeaf(genome, world, getPos())) {
				isFruitLeaf = fruitProvider.getFruitChance(genome, world, getPos()) >= world.rand.nextFloat();
			}
		}

		if (isFruitLeaf) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			if (world != null && world.isRemote) {
				fruitSprite = fruitProvider.getSprite(genome, world, getPos(), getRipeningTime());
			}

			ripeningPeriod = (short) tree.getGenome().getFruitProvider().getRipeningPeriod();
		} else if (world != null && world.isRemote) {
			fruitSprite = null;
		}

		markDirty();
	}

	/* INFORMATION */
	private static boolean isDestroyed(@Nullable ITree tree, int damage) {
		return tree != null && damage > tree.getResilience();
	}

	@Override
	public boolean isPollinated() {
		ITree tree = getTree();
		return tree != null && !isDestroyed(tree, damage) && tree.getMate() != null;
	}

	@SideOnly(Side.CLIENT)
	public int getFoliageColour(EntityPlayer player) {
		final boolean showPollinated = isPollinatedState && GeneticsUtil.hasNaturalistEye(player);
		final int baseColor = getLeafSpriteProvider().getColor(showPollinated);

		ITree tree = getTree();
		if (isDestroyed(tree, damage)) {
			return ColourUtil.addRGBComponents(baseColor, 92, 61, 0);
		} else if (caterpillar != null) {
			return ColourUtil.multiplyRGBComponents(baseColor, 1.5f);
		} else {
			return baseColor;
		}
	}

	public int getFruitColour() {
		if (colourFruits == 0 && hasFruit()) {
			colourFruits = determineFruitColour();
		}
		return colourFruits;
	}

	private int determineFruitColour() {
		ITree tree = getTree();
		if (tree == null) {
			tree = TreeDefinition.Cherry.getIndividual();
		}
		ITreeGenome genome = tree.getGenome();
		IFruitProvider fruit = genome.getFruitProvider();
		return fruit.getColour(genome, world, getPos(), getRipeningTime());
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getLeaveSprite(boolean fancy) {
		final ILeafSpriteProvider leafSpriteProvider = getLeafSpriteProvider();
		return leafSpriteProvider.getSprite(isPollinatedState, fancy);
	}

	@SideOnly(Side.CLIENT)
	private ILeafSpriteProvider getLeafSpriteProvider() {
		if (species != null) {
			return species.getLeafSpriteProvider();
		} else {
			IAlleleTreeSpecies oakSpecies = TreeDefinition.Oak.getIndividual().getGenome().getPrimary();
			return oakSpecies.getLeafSpriteProvider();
		}
	}

	@Nullable
	public ResourceLocation getFruitSprite() {
		return fruitSprite;
	}

	public int getRipeningTime() {
		return ripeningTime;
	}

	/* IPOLLINATABLE */
	@Override
	public EnumPlantType getPlantType() {
		ITree tree = getTree();
		if (tree == null) {
			return EnumPlantType.Plains;
		}

		return tree.getGenome().getPrimary().getPlantType();
	}

	@Override
	public boolean canMateWith(IIndividual individual) {
		if (individual instanceof ITree) {
			ITree tree = getTree();
			return tree != null &&
					tree.getMate() == null &&
					(ModuleApiculture.doSelfPollination || !tree.isGeneticEqual(individual));
		}
		return false;
	}

	@Override
	public void mateWith(IIndividual individual) {
		if (individual instanceof ITree) {
			ITree tree = getTree();
			if (tree == null || world == null) {
				return;
			}

			tree.mate((ITree) individual);
			if (!world.isRemote) {
				sendNetworkUpdate();
			}
		}
	}

	@Override
	public ITree getPollen() {
		return getTree();
	}

	public String getUnlocalizedName() {
		ITree tree = getTree();
		if (tree == null) {
			return "for.leaves.corrupted";
		}
		return tree.getGenome().getPrimary().getUnlocalizedName();
	}

	/* NETWORK */
	private void sendNetworkUpdate() {
		NetworkUtil.sendNetworkPacket(new PacketTileStream(this), pos, world);
	}

	private void sendNetworkUpdateRipening() {
		int newColourFruits = determineFruitColour();
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;

		PacketRipeningUpdate ripeningUpdate = new PacketRipeningUpdate(this);
		NetworkUtil.sendNetworkPacket(ripeningUpdate, pos, world);
	}

	private static final short hasFruitFlag = 1;
	private static final short isPollinatedFlag = 1 << 1;

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);

		byte leafState = 0;
		boolean hasFruit = hasFruit();

		if (isPollinated()) {
			leafState |= isPollinatedFlag;
		}

		if (hasFruit) {
			leafState |= hasFruitFlag;
		}

		data.writeByte(leafState);

		if (hasFruit) {
			String fruitAlleleUID = getTree().getGenome().getActiveAllele(EnumTreeChromosome.FRUITS).getUID();
			int colourFruits = getFruitColour();

			data.writeString(fruitAlleleUID);
			data.writeInt(colourFruits);
		}
	}

	@Override
	public void readData(PacketBufferForestry data) {

		String speciesUID = data.readString(); // this is called instead of super.readData, be careful!

		byte leafState = data.readByte();
		isFruitLeaf = (leafState & hasFruitFlag) > 0;
		isPollinatedState = (leafState & isPollinatedFlag) > 0;
		String fruitAlleleUID = null;

		if (isFruitLeaf) {
			fruitAlleleUID = data.readString();
			colourFruits = data.readInt();
		}

		IAllele[] treeTemplate = TreeManager.treeRoot.getTemplate(speciesUID);
		if (treeTemplate != null) {
			if (fruitAlleleUID != null) {
				IAllele fruitAllele = AlleleManager.alleleRegistry.getAllele(fruitAlleleUID);
				if (fruitAllele instanceof IAlleleFruit) {
					treeTemplate[EnumTreeChromosome.FRUITS.ordinal()] = fruitAllele;
				}
			}

			ITree tree = TreeManager.treeRoot.templateAsIndividual(treeTemplate);
			if (isPollinatedState) {
				tree.mate(tree);
			}

			setTree(tree);

			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public void fromRipeningPacket(int newColourFruits) {
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;
		world.markBlockRangeForRenderUpdate(getPos(), getPos());
	}

	/* IFRUITBEARER */
	@Override
	public NonNullList<ItemStack> pickFruit(ItemStack tool) {
		ITree tree = getTree();
		if (tree == null || !hasFruit()) {
			return NonNullList.create();
		}

		NonNullList<ItemStack> produceStacks = tree.produceStacks(world, getPos(), getRipeningTime());
		ripeningTime = 0;
		sendNetworkUpdateRipening();
		return produceStacks;
	}

	@Override
	public IFruitFamily getFruitFamily() {
		ITree tree = getTree();
		if (tree == null) {
			return EnumFruitFamily.NONE;
		}
		return tree.getGenome().getFruitProvider().getFamily();
	}

	@Override
	public float getRipeness() {
		if (ripeningPeriod == 0) {
			return 1.0f;
		}
		if (getTree() == null) {
			return 0f;
		}
		return (float) getRipeningTime() / ripeningPeriod;
	}

	@Override
	public boolean hasFruit() {
		return isFruitLeaf && !isDestroyed(getTree(), damage);
	}

	@Override
	public void addRipeness(float add) {
		if (getTree() == null || !isFruitLeaf || getRipeningTime() >= ripeningPeriod) {
			return;
		}
		ripeningTime += ripeningPeriod * add;
		sendNetworkUpdateRipening();
	}

	@Nullable
	public String getSpeciesUID() {
		if (species == null) {
			return null;
		}
		return species.getUID();
	}

	/* IBUTTERFLYNURSERY */

	private void matureCaterpillar() {
		if (caterpillar == null) {
			return;
		}
		maturationTime++;

		ITree tree = getTree();
		boolean wasDestroyed = isDestroyed(tree, damage);
		damage += caterpillar.getGenome().getMetabolism();

		IButterflyGenome caterpillarGenome = caterpillar.getGenome();
		int caterpillarMatureTime = Math.round((float) caterpillarGenome.getLifespan() / (caterpillarGenome.getFertility() * 2));

		if (maturationTime >= caterpillarMatureTime) {
			ButterflyManager.butterflyRoot.plantCocoon(world, pos.down(), getCaterpillar(), getOwnerHandler().getOwner(), 0, false);
			setCaterpillar(null);
		} else if (!wasDestroyed && isDestroyed(tree, damage)) {
			sendNetworkUpdate();
		}
	}

	@Override
	public BlockPos getCoordinates() {
		return getPos();
	}

	@Override
	@Nullable
	public IButterfly getCaterpillar() {
		return caterpillar;
	}

	@Override
	public IIndividual getNanny() {
		return getTree();
	}

	@Override
	public void setCaterpillar(@Nullable IButterfly caterpillar) {
		maturationTime = 0;
		this.caterpillar = caterpillar;
		sendNetworkUpdate();
	}

	@Override
	public boolean canNurse(IButterfly caterpillar) {
		ITree tree = getTree();
		return !isDestroyed(tree, damage) && this.caterpillar == null;
	}

	@Override
	public Biome getBiome() {
		return world.getBiome(pos);
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), pos);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().getRainfall());
	}


	@Override
	public World getWorldObj() {
		return world;
	}
}
