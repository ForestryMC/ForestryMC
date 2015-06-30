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
package forestry.arboriculture.gadgets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.core.genetics.alleles.Allele;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketTileStream;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Utils;
import forestry.plugins.PluginArboriculture;

public class TileLeaves extends TileTreeContainer implements IPollinatable, IFruitBearer, IButterflyNursery {

	private int colourLeaves;
	private int colourFruits;

	private short textureIndexFancy = 48;
	private short textureIndexPlain = 64;
	private short textureIndexFruits = -1;

	private boolean isFruitLeaf;
	private boolean isPollinatedState;
	private int ripeningTime;
	private short ripeningPeriod = Short.MAX_VALUE - 1;

	// set true when placed by a player
	private boolean isDecorative = false;

	private int maturationTime;
	private int damage;

	private IEffectData effectData[] = new IEffectData[2];

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		ripeningTime = nbttagcompound.getShort("RT");
		damage = nbttagcompound.getInteger("ENC");

		if (nbttagcompound.hasKey("CATER")) {
			maturationTime = nbttagcompound.getInteger("CATMAT");
			caterpillar = (IButterfly) AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies").getMember(nbttagcompound.getCompoundTag("CATER"));
		}

		if (nbttagcompound.hasKey("Decorative")) { //legacy
			isDecorative = nbttagcompound.getBoolean("Decorative");
		} else {
			isDecorative = nbttagcompound.getBoolean("DEC");
		}

		ITree tree = getTree();
		if (tree != null) {
			setTree(tree);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("RT", getRipeningTime());
		nbttagcompound.setInteger("ENC", damage);

		if (caterpillar != null) {
			nbttagcompound.setInteger("CATMAT", maturationTime);

			NBTTagCompound subcompound = new NBTTagCompound();
			caterpillar.writeToNBT(subcompound);
			nbttagcompound.setTag("CATER", subcompound);
		}
		nbttagcompound.setBoolean("DEC", isDecorative);
	}

	@Override
	public void onBlockTick() {
		ITree tree = getTree();
		if (isDecorative || tree == null) {
			return;
		}

		ITreeGenome genome = tree.getGenome();

		boolean isDestroyed = isDestroyed(tree, damage);
		for (ILeafTickHandler tickHandler : genome.getPrimary().getRoot().getLeafTickHandlers()) {
			if (tickHandler.onRandomLeafTick(tree, worldObj, xCoord, yCoord, zCoord, isDestroyed)) {
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
			ITreekeepingMode treekeepingMode = PluginArboriculture.treeInterface.getTreekeepingMode(worldObj);
			float sappinessModifier = treekeepingMode.getSappinessModifier(genome, 1f);
			float sappiness = genome.getSappiness() * sappinessModifier;

			if (worldObj.rand.nextFloat() < sappiness) {
				ripeningTime++;
				sendNetworkUpdateRipening();
			}
		}

		if (caterpillar != null) {
			matureCaterpillar();
		}

		effectData = tree.doEffect(effectData, worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void setTree(ITree tree) {
		super.setTree(tree);

		ITreeGenome genome = tree.getGenome();
		IAlleleTreeSpecies species = genome.getPrimary();

		if (tree.canBearFruit()) {
			IFruitProvider fruitProvider = genome.getFruitProvider();

			isFruitLeaf = fruitProvider.markAsFruitLeaf(genome, worldObj, xCoord, yCoord, zCoord);
			if (isFruitLeaf) {
				// Hardcoded because vanilla oak trees don't show fruits.
				if ((species == Allele.treeOak) && (fruitProvider == Allele.fruitApple.getProvider())) {
					textureIndexFruits = -1;
				} else {
					textureIndexFruits = fruitProvider.getIconIndex(genome, worldObj, xCoord, yCoord, zCoord, getRipeningTime(), true);
				}

				ripeningPeriod = (short) tree.getGenome().getFruitProvider().getRipeningPeriod();
			}
		} else {
			isFruitLeaf = false;
			textureIndexFruits = -1;
		}

		textureIndexFancy = species.getLeafIconIndex(tree, true);
		textureIndexPlain = species.getLeafIconIndex(tree, false);

		colourLeaves = species.getLeafColour(tree);
		if (isDestroyed(tree, damage)) {
			colourLeaves = Utils.addRGBComponents(colourLeaves, 92, 61, 0);
		} else if (caterpillar != null) {
			colourLeaves = Utils.multiplyRGBComponents(colourLeaves, 1.5f);
		}
	}

	/* INFORMATION */
	private static boolean isDestroyed(ITree tree, int damage) {
		if (tree == null) {
			return false;
		}
		return damage > tree.getResilience();
	}

	public boolean isPollinated() {
		ITree tree = getTree();
		return tree != null && !isDestroyed(tree, damage) && tree.getMate() != null;
	}

	public int getFoliageColour(EntityPlayer player) {
		return (isPollinatedState && GeneticsUtil.hasNaturalistEye(player)) ? 0xffffff : colourLeaves;
	}

	public int getFruitColour() {
		if (colourFruits == 0 && hasFruit()) {
			colourFruits = determineFruitColour();
		}
		return colourFruits;
	}

	private int determineFruitColour() {
		ITree tree = getTree();
		ITreeGenome genome = tree.getGenome();
		IFruitProvider fruit = genome.getFruitProvider();
		return fruit.getColour(genome, worldObj, xCoord, yCoord, zCoord, getRipeningTime());
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(boolean fancy) {
		if (fancy) {
			return TextureManager.getInstance().getIcon(textureIndexFancy);
		} else {
			return TextureManager.getInstance().getIcon(textureIndexPlain);
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getFruitTexture() {
		if (textureIndexFruits >= 0) {
			return TextureManager.getInstance().getIcon(textureIndexFruits);
		} else {
			return null;
		}
	}

	public int getRipeningTime() {
		return isDecorative ? ripeningPeriod : ripeningTime;
	}

	public void setDecorative() {
		isDecorative = true;
	}

	public boolean isDecorative() {
		return isDecorative;
	}

	/* IPOLLINATABLE */
	@Override
	public EnumSet<EnumPlantType> getPlantType() {
		if (getTree() == null) {
			return EnumSet.noneOf(EnumPlantType.class);
		}

		return getTree().getPlantTypes();
	}

	@Override
	public boolean canMateWith(IIndividual individual) {
		if (getTree() == null || isDecorative) {
			return false;
		}
		if (getTree().getMate() != null) {
			return false;
		}
		if (!(individual instanceof ITree)) {
			return false;
		}

		return !getTree().isGeneticEqual(individual);
	}

	@Override
	public void mateWith(IIndividual individual) {
		if (getTree() == null || isDecorative) {
			return;
		}

		getTree().mate((ITree) individual);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public IIndividual getPollen() {
		if (isDecorative) {
			return null;
		}
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
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileStream(this).getPacket();
	}

	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketTileStream(this));
	}

	private void sendNetworkUpdateRipening() {
		int newColourFruits = determineFruitColour();
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;

		PacketRipeningUpdate ripeningUpdate = new PacketRipeningUpdate(this);
		Proxies.net.sendNetworkPacket(ripeningUpdate);
	}

	private static final short hasFruitFlag = 1;
	private static final short isPollinatedFlag = 1 << 1;

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
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

			data.writeUTF(fruitAlleleUID);
			data.writeInt(colourFruits);
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {

		String speciesUID = data.readUTF(); // this is called instead of super.readData, be careful!

		byte leafState = data.readByte();
		isFruitLeaf = (leafState & hasFruitFlag) > 0;
		isPollinatedState = (leafState & isPollinatedFlag) > 0;
		String fruitAlleleUID = null;

		if (isFruitLeaf) {
			fruitAlleleUID = data.readUTF();
			colourFruits = data.readInt();
		}

		IAllele[] treeTemplate = PluginArboriculture.treeInterface.getTemplate(speciesUID);

		if (fruitAlleleUID != null) {
			IAllele fruitAllele = AlleleManager.alleleRegistry.getAllele(fruitAlleleUID);
			if (fruitAllele != null) {
				treeTemplate[EnumTreeChromosome.FRUITS.ordinal()] = fruitAllele;
			}
		}

		if (treeTemplate != null) {
			ITree tree = PluginArboriculture.treeInterface.templateAsIndividual(treeTemplate);
			if (isPollinatedState) {
				tree.mate(tree);
			}

			setTree(tree);

			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	public void fromRipeningPacket(int newColourFruits) {
		if (newColourFruits == colourFruits) {
			return;
		}
		colourFruits = newColourFruits;
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

	/**
	 * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
	 * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
	 */
	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return !Block.isEqualTo(oldBlock, newBlock);
	}

	/* IFRUITBEARER */
	@Override
	public Collection<ItemStack> pickFruit(ItemStack tool) {
		ITree tree = getTree();
		if (tree == null || !hasFruit() || isDecorative) {
			return Collections.emptyList();
		}

		ItemStack[] produceStacks = tree.produceStacks(worldObj, xCoord, yCoord, zCoord, getRipeningTime());
		ripeningTime = 0;
		sendNetworkUpdateRipening();
		return Arrays.asList(produceStacks);
	}

	@Override
	public IFruitFamily getFruitFamily() {
		ITree tree = getTree();
		if (tree == null) {
			return null;
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
		if (getTree() == null || !isFruitLeaf || getRipeningTime() >= ripeningPeriod || isDecorative) {
			return;
		}
		ripeningTime += ripeningPeriod * add;
		sendNetworkUpdateRipening();
	}

	/* IBUTTERFLYNURSERY */
	private IButterfly caterpillar;

	private void matureCaterpillar() {
		maturationTime++;

		ITree tree = getTree();
		boolean wasDestroyed = isDestroyed(tree, damage);
		damage += caterpillar.getGenome().getMetabolism();

		IButterflyGenome caterpillarGenome = caterpillar.getGenome();
		int caterpillarMatureTime = Math.round((float) caterpillarGenome.getLifespan() / (caterpillarGenome.getFertility() * 2));

		if (maturationTime >= caterpillarMatureTime && caterpillar.canTakeFlight(worldObj, xCoord, yCoord, zCoord)) {
			if (worldObj.isAirBlock(xCoord - 1, yCoord, zCoord)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord - 1, yCoord, zCoord);
			} else if (worldObj.isAirBlock(xCoord + 1, yCoord, zCoord)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord + 1, yCoord, zCoord);
			} else if (worldObj.isAirBlock(xCoord, yCoord, zCoord - 1)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord, yCoord, zCoord - 1);
			} else if (worldObj.isAirBlock(xCoord, yCoord, zCoord + 1)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord, yCoord, zCoord + 1);
			}
			setCaterpillar(null);
		} else if (!wasDestroyed && isDestroyed(tree, damage)) {
			sendNetworkUpdate();
		}
	}

	private static void attemptButterflySpawn(World world, IButterfly butterfly, double x, double y, double z) {
		IButterflyRoot butterflyRoot = (IButterflyRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies");
		if (butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(), x, y + 0.1f, z) != null) {
			Proxies.log.finest("A caterpillar '%s' hatched at %s/%s/%s.", butterfly.getDisplayName(), x, y, z);
		}
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(xCoord, yCoord, zCoord);
	}

	@Override
	public IButterfly getCaterpillar() {
		return caterpillar;
	}

	@Override
	public IIndividual getNanny() {
		return getTree();
	}

	@Override
	public void setCaterpillar(IButterfly butterfly) {
		if (isDecorative) {
			return;
		}
		maturationTime = 0;
		caterpillar = butterfly;
		sendNetworkUpdate();
	}

	@Override
	public boolean canNurse(IButterfly butterfly) {
		ITree tree = getTree();
		return !isDecorative && !isDestroyed(tree, damage) && caterpillar == null;
	}

}
