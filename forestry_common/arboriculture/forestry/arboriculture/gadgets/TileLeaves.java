/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.gadgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITree;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.arboriculture.network.PacketLeafUpdate;
import forestry.core.genetics.Allele;
import forestry.core.network.ForestryPacket;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
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

	private int maturationTime;
	private int encumbrance;

	private int biomeId = -1;

	private IEffectData effectData[] = new IEffectData[2];

	private void updateBiome() {
		if(worldObj == null)
			return;
		BiomeGenBase biome = Utils.getBiomeAt(worldObj, xCoord, zCoord);
		if (biome != null) {
			this.biomeId = biome.biomeID;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		isFruitLeaf = nbttagcompound.getBoolean("FL");
		ripeningTime = nbttagcompound.getInteger("RT");

		encumbrance = nbttagcompound.getInteger("ENC");

		if (nbttagcompound.hasKey("CATER")) {
			maturationTime = nbttagcompound.getInteger("CATMAT");
			caterpillar = (IButterfly)AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies").getMember(nbttagcompound.getCompoundTag("CATER"));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("FL", isFruitLeaf);
		nbttagcompound.setInteger("RT", ripeningTime);
		nbttagcompound.setInteger("ENC", encumbrance);

		if (caterpillar != null) {
			nbttagcompound.setInteger("CATMAT", maturationTime);

			NBTTagCompound subcompound = new NBTTagCompound();
			caterpillar.writeToNBT(subcompound);
			nbttagcompound.setTag("CATER", subcompound);
		}

	}

	@Override
	public void onBlockTick() {
		if(biomeId < 0)
			updateBiome();

		if(getTree() == null)
			return;

		boolean isDestroyed = isDestroyed();
		for(ILeafTickHandler tickHandler : getTree().getGenome().getPrimary().getRoot().getLeafTickHandlers())
			if(tickHandler.onRandomLeafTick(getTree(), worldObj, biomeId, xCoord, yCoord, zCoord, isDestroyed))
				return;

		if(isDestroyed)
			return;

		if(encumbrance > 0)
			encumbrance--;

		if (hasFruit() && ripeningTime < Short.MAX_VALUE - 1) {
			float sappiness = getTree().getGenome().getSappiness()
					* PluginArboriculture.treeInterface.getTreekeepingMode(worldObj).getSappinessModifier(this.getTree().getGenome(), 1f);

			if (worldObj.rand.nextFloat() < sappiness) {
				ripeningTime++;
				sendNetworkUpdateRipening();
			}
		}

		if(caterpillar != null)
			matureCaterpillar();

		effectData = getTree().doEffect(effectData, worldObj, biomeId, xCoord, yCoord, zCoord);
	}

	@Override
	public void setTree(ITree tree) {
		if (tree.canBearFruit())
			isFruitLeaf = tree.getGenome().getFruitProvider().markAsFruitLeaf(tree.getGenome(), worldObj, xCoord, yCoord, zCoord);
		super.setTree(tree);
	}

	/* INFORMATION */
	private boolean isDestroyed() {
		if(getTree() == null)
			return false;
		return encumbrance > getTree().getResilience();
	}

	public boolean isPollinated() {
		return !isDestroyed() && getTree() != null && getTree().getMate() != null;
	}

	public int getFoliageColour(EntityPlayer player) {
		return isPollinatedState & Utils.hasNaturalistEye(player) ? 0xffffff : colourLeaves;
	}

	public int getFruitColour() {
		return colourFruits;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(boolean fancy) {
		if (fancy)
			return TextureManager.getInstance().getIcon(textureIndexFancy);
		else
			return TextureManager.getInstance().getIcon(textureIndexPlain);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getFruitTexture() {
		if (textureIndexFruits >= 0)
			return TextureManager.getInstance().getIcon(textureIndexFruits);
		else
			return null;
	}

	public int getRipeningTime() {
		return ripeningTime;
	}

	/* IPOLLINATABLE */
	@Override
	public EnumSet<EnumPlantType> getPlantType() {
		if (getTree() == null)
			return EnumSet.noneOf(EnumPlantType.class);

		return getTree().getPlantTypes();
	}

	@Override
	public boolean canMateWith(IIndividual individual) {
		if (getTree() == null)
			return false;
		if (getTree().getMate() != null)
			return false;

		return !getTree().isGeneticEqual(individual);
	}

	@Override
	public void mateWith(IIndividual individual) {
		if (getTree() == null)
			return;

		getTree().mate((ITree) individual);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public IIndividual getPollen() {
		return getTree();
	}

	public int determineFoliageColour() {

		if (getTree() == null)
			return PluginArboriculture.proxy.getFoliageColorBasic();

		int colour = getTree().getGenome().getPrimary().getLeafColour(getTree());

		if(isDestroyed())
			return Utils.addRGBComponents(colour, 92, 61, 0);
		else if(caterpillar != null)
			return Utils.multiplyRGBComponents(colour, 1.5f);
		return colour;
	}

	public int determineFruitColour() {
		if (getTree() == null)
			return 0xffffff;

		IFruitProvider fruit = getTree().getGenome().getFruitProvider();
		return fruit.getColour(getTree().getGenome(), worldObj, xCoord, yCoord, zCoord, getRipeningTime());
	}

	public short determineTextureIndex(boolean fancy) {
		if (getTree() != null)
			return getTree().getGenome().getPrimary().getLeafIconIndex(getTree(), fancy);

		return 0;
	}

	public short determineOverlayIndex() {
		if (getTree() == null)
			return -1;
		if (!hasFruit())
			return -1;

		IFruitProvider fruit = getTree().getGenome().getFruitProvider();

		// Hardcoded because vanilla oak trees don't show fruits.
		if (getTree().getGenome().getPrimary() == Allele.treeOak && fruit == ((IAlleleFruit) Allele.fruitApple).getProvider())
			return -1;
		else
			return fruit.getIconIndex(getTree().getGenome(), worldObj, xCoord, yCoord, zCoord, getRipeningTime(), true);

	}

	/* NETWORK */
	@Override
	public Packet getDescriptionPacket() {
		return new PacketLeafUpdate(xCoord, yCoord, zCoord, this).getPacket();
	}

	@Override
	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketLeafUpdate(xCoord, yCoord, zCoord, this), xCoord, yCoord, zCoord);
	}

	private void sendNetworkUpdateRipening() {
		Proxies.net.sendNetworkPacket(new PacketLeafUpdate(xCoord, yCoord, zCoord, determineFruitColour()), xCoord, yCoord, zCoord);
	}

	@Override
	public void fromPacket(ForestryPacket packetRaw) {

		PacketLeafUpdate packet = (PacketLeafUpdate)packetRaw;
		if(packet.isRipeningUpdate()) {
			colourFruits = packet.colourFruits;
		} else {
			isFruitLeaf = packet.isFruitLeaf();
			isPollinatedState = packet.isPollinated();
			textureIndexFancy = packet.textureIndexFancy;
			textureIndexPlain = packet.textureIndexPlain;
			textureIndexFruits = packet.textureIndexFruit;
			colourLeaves = packet.colourLeaves;
			colourFruits = packet.colourFruits;
		}

		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

	/* IFRUITBEARER */
	@Override
	public Collection<ItemStack> pickFruit(ItemStack tool) {
		if (!hasFruit())
			return new ArrayList<ItemStack>();
		if (getTree() == null)
			return new ArrayList<ItemStack>();

		ArrayList<ItemStack> picked = new ArrayList<ItemStack>(Arrays.asList(getTree().produceStacks(worldObj, xCoord, yCoord, zCoord, getRipeningTime())));
		ripeningTime = 0;
		sendNetworkUpdateRipening();
		return picked;
	}

	@Override
	public IFruitFamily getFruitFamily() {
		if (getTree() == null)
			return null;
		return getTree().getGenome().getFruitProvider().getFamily();
	}

	@Override
	public float getRipeness() {
		if (getTree() == null)
			return 0f;
		int ripeningPeriod = getTree().getGenome().getFruitProvider().getRipeningPeriod();
		if (ripeningPeriod == 0)
			return 1.0f;
		return (float) ripeningTime / ripeningPeriod;
	}

	@Override
	public boolean hasFruit() {
		return isFruitLeaf && !isDestroyed();
	}

	@Override
	public void addRipeness(float add) {
		if (getTree() == null)
			return;
		ripeningTime += getTree().getGenome().getFruitProvider().getRipeningPeriod() * add;
		sendNetworkUpdateRipening();
	}

	/* IBUTTERFLYNURSERY */
	private IButterfly caterpillar;

	private void matureCaterpillar() {
		maturationTime++;

		boolean wasDestroyed = isDestroyed();
		encumbrance += caterpillar.getGenome().getMetabolism();
		wasDestroyed = !wasDestroyed && isDestroyed() ? true : false;

		if(maturationTime >= (float)caterpillar.getGenome().getLifespan() / (caterpillar.getGenome().getFertility()*2)
				&& caterpillar.canTakeFlight(worldObj, xCoord, yCoord, zCoord)) {
			if(worldObj.isAirBlock(xCoord - 1, yCoord, zCoord)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord - 1, yCoord, zCoord);
			} else if(worldObj.isAirBlock(xCoord + 1, yCoord, zCoord)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord + 1, yCoord, zCoord);
			} else if(worldObj.isAirBlock(xCoord, yCoord, zCoord - 1)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord, yCoord, zCoord - 1);
			} else if(worldObj.isAirBlock(xCoord, yCoord, zCoord + 1)) {
				attemptButterflySpawn(worldObj, caterpillar, xCoord, yCoord, zCoord + 1);
			}
			setCaterpillar(null);
		} else if(wasDestroyed) {
			sendNetworkUpdate();
		}
	}

	private void attemptButterflySpawn(World world, IButterfly butterfly, double x, double y, double z) {
		if(((IButterflyRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies")).spawnButterflyInWorld(world, butterfly.copy(), x, y + 0.1f, z) != null)
			Proxies.log.finest("A caterpillar '%s' hatched at %s/%s/%s.", butterfly.getDisplayName(), x, y, z);
	}

	@Override public World getWorld() { return worldObj; }
	@Override public int getXCoord() { return xCoord; }
	@Override public int getYCoord() { return yCoord; }
	@Override public int getZCoord() { return zCoord; }

	@Override
	public int getBiomeId() {
		return 0;
	}

	@Override
	public EnumTemperature getTemperature() {
		return null;
	}

	@Override
	public EnumHumidity getHumidity() {
		return null;
	}

	@Override public void setErrorState(int state) {}
	@Override public int getErrorOrdinal() { return 0; }
	@Override
	public boolean addProduct(ItemStack product, boolean all) {
		return false;
	}

	@Override public IButterfly getCaterpillar() { return caterpillar; }
	@Override public IIndividual getNanny() { return getTree(); }

	@Override
	public void setCaterpillar(IButterfly butterfly) {
		maturationTime = 0;
		caterpillar = butterfly;
		sendNetworkUpdate();
	}

	@Override
	public boolean canNurse(IButterfly butterfly) {
		return !isDestroyed() && caterpillar == null;
	}

}
