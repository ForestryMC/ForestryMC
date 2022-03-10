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
import java.util.Objects;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.PlantType;

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
import forestry.arboriculture.features.ArboricultureTiles;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.network.IRipeningPacketReceiver;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.utils.ColourUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.RenderUtil;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.utils.AlleleUtils;

public class TileLeaves extends TileTreeContainer implements IPollinatable, IFruitBearer, IButterflyNursery, IRipeningPacketReceiver {
	private static final String NBT_RIPENING = "RT";
	private static final String NBT_DAMAGE = "ENC";
	private static final String NBT_FRUIT_LEAF = "FL";
	private static final String NBT_MATURATION = "CATMAT";
	private static final String NBT_CATERPILLAR = "CATER";

	public static final ModelProperty<ILeafSpriteProvider> SPRITE_PROVIDER = new ModelProperty<>();
	public static final ModelProperty<Boolean> POLLINATED = new ModelProperty<>();
	public static final ModelProperty<ResourceLocation> FRUIT_TEXTURE = new ModelProperty<>();

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

	public TileLeaves(BlockPos pos, BlockState state) {
		super(ArboricultureTiles.LEAVES.tileType(), pos, state);
	}

	/* SAVING & LOADING */
	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);

		ripeningTime = compoundNBT.getShort(NBT_RIPENING);
		damage = compoundNBT.getInt(NBT_DAMAGE);
		isFruitLeaf = compoundNBT.getBoolean(NBT_FRUIT_LEAF);
		checkFruit = !compoundNBT.contains(NBT_FRUIT_LEAF, Tag.TAG_ANY_NUMERIC);

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
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);

		compoundNBT.putInt(NBT_RIPENING, getRipeningTime());
		compoundNBT.putInt(NBT_DAMAGE, damage);
		compoundNBT.putBoolean(NBT_FRUIT_LEAF, isFruitLeaf);

		if (caterpillar != null) {
			compoundNBT.putInt(NBT_MATURATION, maturationTime);

			CompoundTag caterpillarNbt = new CompoundTag();
			caterpillar.write(caterpillarNbt);
			compoundNBT.put(NBT_CATERPILLAR, caterpillarNbt);
		}
	}

	@Override
	public void onBlockTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
		ITree tree = getTree();
		if (tree == null) {
			return;
		}

		IGenome genome = tree.getGenome();
		IAlleleTreeSpecies primary = genome.getActiveAllele(TreeChromosomes.SPECIES);

		boolean isDestroyed = isDestroyed(tree, damage);
		for (ILeafTickHandler tickHandler : primary.getRoot().getLeafTickHandlers()) {
			if (tickHandler.onRandomLeafTick(tree, level, rand, getBlockPos(), isDestroyed)) {
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
			ITreekeepingMode treekeepingMode = TreeManager.treeRoot.getTreekeepingMode(level);
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

		effectData = tree.doEffect(effectData, level, getBlockPos());
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

		if (tree.canBearFruit() && checkFruit && level != null && !level.isClientSide) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			if (fruitProvider.isFruitLeaf(genome, level, getBlockPos())) {
				isFruitLeaf = fruitProvider.getFruitChance(genome, level, getBlockPos()) >= level.random.nextFloat();
			}
		}

		if (isFruitLeaf) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			if (level != null && level.isClientSide) {
				fruitSprite = fruitProvider.getSprite(genome, level, getBlockPos(), getRipeningTime());
			}

			ripeningPeriod = (short) fruitProvider.getRipeningPeriod();
		} else if (level != null && level.isClientSide) {
			fruitSprite = null;
		}
		requestModelDataUpdate();

		setChanged();
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
	public int getFoliageColour(Player player) {
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
		return fruit.getColour(genome, level, getBlockPos(), getRipeningTime());
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

	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getLeaveSprite(IModelData data, boolean fancy) {
		final ILeafSpriteProvider leafSpriteProvider = getLeafSpriteProvider(data);
		final Boolean pollinated = data.getData(POLLINATED);
		return leafSpriteProvider.getSprite(pollinated != null && pollinated, fancy);
	}

	@OnlyIn(Dist.CLIENT)
	private static ILeafSpriteProvider getLeafSpriteProvider(IModelData data) {
		final ILeafSpriteProvider leafSpriteProvider = data.getData(SPRITE_PROVIDER);
		if (leafSpriteProvider != null) {
			return leafSpriteProvider;
		} else {
			IAlleleTreeSpecies oakSpecies = TreeDefinition.Oak.createIndividual().getGenome().getActiveAllele(TreeChromosomes.SPECIES);
			return oakSpecies.getLeafSpriteProvider();
		}
	}

	@Nullable
	public static ResourceLocation getFruitSprite(IModelData data) {
		return data.getData(FRUIT_TEXTURE);
	}

	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		builder.withInitial(SPRITE_PROVIDER, getLeafSpriteProvider());
		builder.withInitial(POLLINATED, isPollinatedState);
		builder.withInitial(FRUIT_TEXTURE, fruitSprite);
		return builder.build();
	}

	public int getRipeningTime() {
		return ripeningTime;
	}

	/* IPOLLINATABLE */
	@Override
	public PlantType getPlantType() {
		ITree tree = getTree();
		if (tree == null) {
			return PlantType.PLAINS;
		}

		return tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES).getPlantType();
	}

	@Override
	public boolean canMateWith(IIndividual individual) {
		if (individual instanceof ITree) {
			ITree tree = getTree();
			return tree != null &&
					!tree.getMate().isPresent() &&
					(ModuleApiculture.doSelfPollination || !tree.isGeneticEqual(individual));
		}
		return false;
	}

	@Override
	public void mateWith(IIndividual individual) {
		if (individual instanceof ITree) {
			ITree tree = getTree();
			if (tree == null || level == null) {
				return;
			}

			tree.mate(individual.getGenome());
			if (!level.isClientSide) {
				sendNetworkUpdate();
			}
		}
	}

	@Override
	public ITree getPollen() {
		return getTree();
	}

	/* NETWORK */
	private void sendNetworkUpdate() {
		NetworkUtil.sendNetworkPacket(new PacketTileStream(this), worldPosition, level);
	}

	private void sendNetworkUpdateRipening() {
		if (isRemoved()) {
			return;
		}
		int newColourFruits = determineFruitColour();
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;

		PacketRipeningUpdate ripeningUpdate = new PacketRipeningUpdate(this);
		NetworkUtil.sendNetworkPacket(ripeningUpdate, worldPosition, level);
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

			data.writeUtf(fruitAlleleUID);
			data.writeInt(colourFruits);
		}
	}

	@Override
	public void readData(PacketBufferForestry data) {

		String speciesUID = data.readUtf(); // this is called instead of super.readData, be careful!

		byte leafState = data.readByte();
		isFruitLeaf = (leafState & hasFruitFlag) > 0;
		isPollinatedState = (leafState & isPollinatedFlag) > 0;
		String fruitAlleleUID = null;

		if (isFruitLeaf) {
			fruitAlleleUID = data.readUtf();
			colourFruits = data.readInt();
		}

		IAllele[] treeTemplate = TreeManager.treeRoot.getTemplates().getTemplate(speciesUID);
		if (treeTemplate != null) {
			if (fruitAlleleUID != null) {
				AlleleUtils.actOn(new ResourceLocation(fruitAlleleUID), IAlleleFruit.class, fruitAllele -> treeTemplate[TreeChromosomes.FRUITS.getIndex()] = fruitAllele);
			}

			ITree tree = TreeManager.treeRoot.templateAsIndividual(treeTemplate);
			if (isPollinatedState) {
				tree.mate(tree.getGenome());
			}

			setTree(tree);

			RenderUtil.markForUpdate(worldPosition);
			//world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public void fromRipeningPacket(int newColourFruits) {
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;
		RenderUtil.markForUpdate(worldPosition);
	}

	/* IFRUITBEARER */
	@Override
	public NonNullList<ItemStack> pickFruit(ItemStack tool) {
		ITree tree = getTree();
		if (tree == null || !hasFruit()) {
			return NonNullList.create();
		}

		NonNullList<ItemStack> produceStacks = tree.produceStacks(level, getBlockPos(), getRipeningTime());
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
			ButterflyManager.butterflyRoot.plantCocoon(level, worldPosition.below(), getCaterpillar(), getOwnerHandler().getOwner(), 0, false);
			setCaterpillar(null);
		} else if (!wasDestroyed && isDestroyed(tree, damage)) {
			sendNetworkUpdate();
		}
	}

	@Override
	public BlockPos getCoordinates() {
		return getBlockPos();
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
		Level level = Objects.requireNonNull(this.level);
		return level.getBiome(worldPosition).value();
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), worldPosition);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().getDownfall());
	}

	@Override
	public Level getWorldObj() {
		return level;
	}
}
