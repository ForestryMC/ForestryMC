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
package forestry.apiculture.items;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBee;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import forestry.core.vect.Vect;
import forestry.plugins.PluginApiculture;

public class ItemHabitatLocator extends ItemInventoried {

	public static class HabitatLocatorInventory extends ItemInventory implements IErrorSource, IHintSource {

		private static final short SLOT_ENERGY = 2;
		private static final short SLOT_SPECIMEN = 0;
		private static final short SLOT_ANALYZED = 1;

		public Set<BiomeGenBase> biomesToSearch = new HashSet<BiomeGenBase>();
		private ItemHabitatLocator habitatLocator;

		public HabitatLocatorInventory(ItemStack itemstack) {
			super(ItemHabitatLocator.class, 3, itemstack);
			this.habitatLocator = (ItemHabitatLocator) itemstack.getItem();
		}

		private boolean isEnergy(ItemStack itemstack) {
			if (itemstack == null || itemstack.stackSize <= 0) {
				return false;
			}

			return ForestryItem.honeyDrop.isItemEqual(itemstack) || ForestryItem.honeydew.isItemEqual(itemstack);
		}

		public void tryAnalyze() {

			if (getStackInSlot(SLOT_SPECIMEN) != null) {
				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
					return;
				}

				// Decrease energy
				decrStackSize(SLOT_ENERGY, 1);

				setInventorySlotContents(SLOT_ANALYZED, getStackInSlot(SLOT_SPECIMEN));
				setInventorySlotContents(SLOT_SPECIMEN, null);
			}

			IBee bee = PluginApiculture.beeInterface.getMember(getStackInSlot(SLOT_ANALYZED));

			// No bee, abort
			if (bee == null) {
				return;
			}

			biomesToSearch = new HashSet<BiomeGenBase>(bee.getSuitableBiomes());
			habitatLocator.startBiomeSearch(biomesToSearch);
		}

		@Override
		public void markDirty() {
			tryAnalyze();
		}

		// / IHINTSOURCE
		@Override
		public boolean hasHints() {
			return Config.hints.get("habitatlocator") != null && Config.hints.get("habitatlocator").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("habitatlocator");
		}

		// / IERRORSOURCE
		@Override
		public boolean throwsErrors() {
			return true;
		}

		@Override
		public EnumErrorCode getErrorState() {
			if (PluginApiculture.beeInterface.isMember(inventoryStacks[SLOT_SPECIMEN]) && !isEnergy(getStackInSlot(SLOT_ENERGY))) {
				return EnumErrorCode.NOHONEY;
			}

			return EnumErrorCode.OK;
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_ENERGY) {
				Item item = itemStack.getItem();
				return item == ForestryItem.honeydew.item() || item == ForestryItem.honeyDrop.item();
			} else if (slotIndex == SLOT_SPECIMEN) {
				return PluginApiculture.beeInterface.isMember(itemStack);
			}
			return false;
		}

	}

	private Set<BiomeGenBase> biomesToSearch = new HashSet<BiomeGenBase>();
	private boolean biomeFound = false;
	private int searchRadiusIteration = 0;
	private int searchAngleIteration = 0;
	private Vect searchCenter;
	public static TextureAtlasSprite icon;

	public ItemHabitatLocator() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.HabitatLocatorGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY, (int) entityplayer.posZ);
		}

		return itemstack;
	}

	/* TEXTURES */
	@SideOnly(Side.CLIENT)
	public static void registerIcon(TextureMap map) {
			TextureAtlasSprite texture = new TextureHabitatLocator();
			map.setTextureEntry("forestry:biomefinder", texture);
			icon = texture;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		BiomeGenBase currentBiome = player.worldObj.getBiomeGenForCoords(player.getPosition());

		float temperatureValue = currentBiome.getFloatTemperature(player.getPosition());
		EnumTemperature temperature = EnumTemperature.getFromValue(temperatureValue);
		EnumHumidity humidity = EnumHumidity.getFromValue(currentBiome.rainfall);

		list.add(StringUtil.localize("gui.currentBiome") + ": " + currentBiome.biomeName);
		list.add(StringUtil.localize("gui.temperature") + ": " + AlleleManager.climateHelper.toDisplay(temperature));
		list.add(StringUtil.localize("gui.humidity") + ": " + AlleleManager.climateHelper.toDisplay(humidity));
	}

	public void startBiomeSearch(Set<BiomeGenBase> biomesToSearch) {

		this.biomesToSearch = biomesToSearch;
		this.searchAngleIteration = 0;
		this.searchRadiusIteration = 0;
		this.biomeFound = false;
		this.searchCenter = null;

		// reset the locator coordinates
		Proxies.common.setHabitatLocatorCoordinates(null, null);
	}

	@Override
	public void onUpdate(ItemStack p_77663_1_, World world, Entity player, int p_77663_4_, boolean p_77663_5_) {

		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		if (this.searchCenter == null) {
			this.searchCenter = new Vect((int) player.posX, (int) player.posY, (int) player.posZ);

			BiomeGenBase currentBiome = world.getBiomeGenForCoords(searchCenter.toBlockPos());
			removeInvalidBiomes(currentBiome, biomesToSearch);
		}

		if (biomesToSearch.isEmpty()) {
			return;
		}

		// once we've found the biome, slow down to conserve cpu and network data
		if (biomeFound && world.getTotalWorldTime() % 20 != 0) {
			return;
		}

		BlockPos target = findNearestBiome(player, biomesToSearch);

		// send an update if we find the biome
		if (target != null) {
			Proxies.common.setHabitatLocatorCoordinates(player, target);
			biomeFound = true;
		}
	}

	private BlockPos findNearestBiome(Entity player, Collection<BiomeGenBase> biomesToSearch) {

		final int maxChecksPerTick = 100;
		final int maxSearchRadiusIterations = 500;
		final int spacing = 20;

		Vect playerPos = new Vect((int) player.posX, (int) player.posY, (int) player.posZ);

		// If we are in a valid spot, we point to ourselves.
		BlockPos coordinates = getChunkCoordinates(playerPos, player.worldObj, biomesToSearch);
		if (coordinates != null) {
			searchAngleIteration = 0;
			searchRadiusIteration = 0;
			return playerPos.toBlockPos();
		}

		// check in a circular pattern, starting at the center and increasing radius each step
		final int radius = spacing * (searchRadiusIteration + 1);

		double angleSpacing = 2.0f * Math.asin(spacing / (2.0 * radius));

		// round to nearest divisible angle, for an even distribution
		angleSpacing = 2.0 * Math.PI / Math.round(2.0 * Math.PI / angleSpacing);

		// do a limited number of checks per tick
		for (int i = 0; i < maxChecksPerTick; i++) {

			double angle = angleSpacing * searchAngleIteration;
			if (angle > 2.0 * Math.PI) {
				searchAngleIteration = 0;
				searchRadiusIteration++;
				if (searchRadiusIteration > maxSearchRadiusIterations) {
					searchAngleIteration = 0;
					searchRadiusIteration = 0;
					searchCenter = playerPos;
				}
				return null;
			} else {
				searchAngleIteration++;
			}

			int xOffset = Math.round((float) (radius * Math.cos(angle)));
			int zOffset = Math.round((float) (radius * Math.sin(angle)));
			Vect pos = searchCenter.add(xOffset, 0, zOffset);

			coordinates = getChunkCoordinates(pos, player.worldObj, biomesToSearch);
			if (coordinates != null) {
				searchAngleIteration = 0;
				searchRadiusIteration = 0;
				return coordinates;
			}
		}

		return null;
	}

	private static BlockPos getChunkCoordinates(Vect pos, World world, Collection<BiomeGenBase> biomesToSearch) {

		// to avoid reporting very tiny patches, check around the point want
		final int minBiomeRadius = 8;

		BiomeGenBase biome;

		biome = world.getBiomeGenForCoords(pos.toBlockPos());
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(new BlockPos(pos.x - minBiomeRadius, pos.y, pos.z));
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(new BlockPos(pos.x + minBiomeRadius, pos.y, pos.z));
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(new BlockPos(pos.x, pos.y, pos.z - minBiomeRadius));
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(new BlockPos(pos.x, pos.y, pos.z + minBiomeRadius));
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		return new BlockPos(pos.x, pos.y, pos.z);
	}

	private static final Set<BiomeGenBase> waterBiomes = new HashSet<BiomeGenBase>();
	private static final Set<BiomeGenBase> netherBiomes = new HashSet<BiomeGenBase>();
	private static final Set<BiomeGenBase> endBiomes = new HashSet<BiomeGenBase>();

	static {
		Collections.addAll(waterBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.BEACH));
		Collections.addAll(waterBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.OCEAN));
		Collections.addAll(waterBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.RIVER));

		Collections.addAll(netherBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.NETHER));

		Collections.addAll(endBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.END));
	}

	private static void removeInvalidBiomes(BiomeGenBase currentBiome, Set<BiomeGenBase> biomesToSearch) {

		biomesToSearch.removeAll(waterBiomes);

		if (BiomeDictionary.isBiomeOfType(currentBiome, BiomeDictionary.Type.NETHER)) {
			biomesToSearch.retainAll(netherBiomes);
		} else {
			biomesToSearch.removeAll(netherBiomes);
		}

		if (BiomeDictionary.isBiomeOfType(currentBiome, BiomeDictionary.Type.END)) {
			biomesToSearch.retainAll(endBiomes);
		} else {
			biomesToSearch.removeAll(endBiomes);
		}
	}
}
