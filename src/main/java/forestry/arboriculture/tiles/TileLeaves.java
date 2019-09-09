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
import java.util.Optional;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.util.Constants;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
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
import forestry.core.utils.RenderUtil;

public class TileLeaves extends TileTreeContainer implements IPollinatable, IFruitBearer, IButterflyNursery, IRipeningPacketReceiver {
	private static final String NBT_RIPENING = "RT";
	private static final String NBT_DAMAGE = "ENC";
	private static final String NBT_FRUIT_LEAF = "FL";
	private static final String NBT_MATURATION = "CATMAT";
	private static final String NBT_CATERPILLAR = "CATER";

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

	private IEffectData[] effectData = new IEffectData[2];

	public TileLeaves() {
		super(ModuleArboriculture.getTiles().leaves);
	}

	/* SAVING & LOADING */
	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);

		ripeningTime = compoundNBT.getShort(NBT_RIPENING);
		damage = compoundNBT.getInt(NBT_DAMAGE);
		isFruitLeaf = compoundNBT.getBoolean(NBT_FRUIT_LEAF);
		checkFruit = !compoundNBT.contains(NBT_FRUIT_LEAF, Constants.NBT.TAG_ANY_NUMERIC);

		if (compoundNBT.contains(NBT_CATERPILLAR)) {
			maturationTime = compoundNBT.getInt(NBT_MATURATION);
			caterpillar = ButterflyManager.butterflyRoot.create(compoundNBT.getCompound(NBT_CATERPILLAR));
		}

		ITree tree = getTree();
		if (tree != null) {
			setTree(tree);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);

		compoundNBT.putInt(NBT_RIPENING, getRipeningTime());
		compoundNBT.putInt(NBT_DAMAGE, damage);
		compoundNBT.putBoolean(NBT_FRUIT_LEAF, isFruitLeaf);

		if (caterpillar != null) {
			compoundNBT.putInt(NBT_MATURATION, maturationTime);

			CompoundNBT caterpillarNbt = new CompoundNBT();
			caterpillar.write(caterpillarNbt);
			compoundNBT.put(NBT_CATERPILLAR, caterpillarNbt);
		}
		return compoundNBT;
	}

	@Override
	public void onBlockTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		ITree tree = getTree();
		if (tree == null) {
			return;
		}

		IGenome genome = tree.getGenome();
		IAlleleTreeSpecies primary = genome.getActiveAllele(TreeChromosomes.SPECIES);

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
			float sappiness = genome.getActiveValue(TreeChromosomes.SAPPINESS) * sappinessModifier;

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

	@Override
	public void setTree(ITree tree) {
		ITree oldTree = getTree();
		super.setTree(tree);

		IGenome genome = tree.getGenome();
		species = genome.getActiveAllele(TreeChromosomes.SPECIES);

		if (oldTree != null && !tree.equals(oldTree)) {
			checkFruit = true;
		}

		if (tree.canBearFruit() && checkFruit && world != null && !world.isRemote) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			if (fruitProvider.isFruitLeaf(genome, world, getPos())) {
				isFruitLeaf = fruitProvider.getFruitChance(genome, world, getPos()) >= world.rand.nextFloat();
			}
		}

		if (isFruitLeaf) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			if (world != null && world.isRemote) {
				fruitSprite = fruitProvider.getSprite(genome, world, getPos(), getRipeningTime());
			}

			ripeningPeriod = (short) fruitProvider.getRipeningPeriod();
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
		return tree != null && !isDestroyed(tree, damage) && tree.getMate().isPresent();
	}

	@OnlyIn(Dist.CLIENT)
	public int getFoliageColour(PlayerEntity player) {
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
			tree = TreeDefinition.Cherry.createIndividual();
		}
		IGenome genome = tree.getGenome();
		IFruitProvider fruit = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
		return fruit.getColour(genome, world, getPos(), getRipeningTime());
	}

	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getLeaveSprite(boolean fancy) {
		final ILeafSpriteProvider leafSpriteProvider = getLeafSpriteProvider();
		return leafSpriteProvider.getSprite(isPollinatedState, fancy);
	}

	@OnlyIn(Dist.CLIENT)
	private ILeafSpriteProvider getLeafSpriteProvider() {
		if (species != null) {
			return species.getLeafSpriteProvider();
		} else {
			IAlleleTreeSpecies oakSpecies = TreeDefinition.Oak.createIndividual().getGenome().getActiveAllele(TreeChromosomes.SPECIES);
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
	public PlantType getPlantType() {
		ITree tree = getTree();
		if (tree == null) {
			return PlantType.Plains;
		}

		return tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES).getPlantType();
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

			tree.mate(individual.getGenome());
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
		return tree.getGenome().getPrimary().getLocalisationKey();
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
			String fruitAlleleUID = getTree().getGenome().getActiveAllele(TreeChromosomes.FRUITS).getRegistryName().toString();
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

		IAllele[] treeTemplate = TreeManager.treeRoot.getTemplates().getTemplate(speciesUID);
		if (treeTemplate != null) {
			if (fruitAlleleUID != null) {
				Optional<IAllele> optionalAllele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(fruitAlleleUID);
				if (optionalAllele.isPresent()) {
					IAllele fruitAllele = optionalAllele.get();
					if (fruitAllele instanceof IAlleleFruit) {
						treeTemplate[TreeChromosomes.FRUITS.ordinal()] = fruitAllele;
					}
				}
			}

			ITree tree = TreeManager.treeRoot.templateAsIndividual(treeTemplate);
			if (isPollinatedState) {
				tree.mate(tree.getGenome());
			}

			setTree(tree);

			RenderUtil.markForUpdate(pos);
			//world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public void fromRipeningPacket(int newColourFruits) {
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;
		RenderUtil.markForUpdate(pos);
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
		return tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).getProvider().getFamily();
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
		return species.getRegistryName().toString();
	}

	/* IBUTTERFLYNURSERY */

	private void matureCaterpillar() {
		if (caterpillar == null) {
			return;
		}
		maturationTime++;

		ITree tree = getTree();
		boolean wasDestroyed = isDestroyed(tree, damage);
		damage += caterpillar.getGenome().getActiveValue(ButterflyChromosomes.METABOLISM);

		IGenome caterpillarGenome = caterpillar.getGenome();
		int caterpillarMatureTime = Math.round((float) caterpillarGenome.getActiveValue(ButterflyChromosomes.LIFESPAN) / (caterpillarGenome.getActiveValue(ButterflyChromosomes.FERTILITY) * 2));

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
		return EnumHumidity.getFromValue(getBiome().getDownfall());
	}


	@Override
	public World getWorldObj() {
		return world;
	}
}
