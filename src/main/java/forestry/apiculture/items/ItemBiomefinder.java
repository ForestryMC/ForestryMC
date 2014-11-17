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

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.apiculture.render.TextureBiomefinder;
import forestry.api.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;
import forestry.core.utils.Vect;
import forestry.plugins.PluginApiculture;

public class ItemBiomefinder extends ItemInventoried {

	public static class BiomefinderInventory extends ItemInventory implements IErrorSource, IHintSource {

		public ArrayList<Integer> biomesToSearch = new ArrayList<Integer>();

		private final short energySlot = 2;
		private final short specimenSlot = 0;
		private final short analyzeSlot = 1;

		public BiomefinderInventory() {
			super(ItemBiomefinder.class, 3);
		}

		public BiomefinderInventory(ItemStack itemstack) {
			super(ItemBiomefinder.class, 3, itemstack);
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {

			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 2; i < inventoryStacks.length; i++)
				if (inventoryStacks[i] != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte) i);
					inventoryStacks[i].writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			nbttagcompound.setTag("Items", nbttaglist);

		}

		private boolean isEnergy(ItemStack itemstack) {
			if (itemstack == null || itemstack.stackSize <= 0)
				return false;

			return ForestryItem.honeyDrop.isItemEqual(itemstack) || ForestryItem.honeydew.isItemEqual(itemstack);
		}

		private void tryAnalyze() {

			// Analyzed slot occupied, abort
			if (inventoryStacks[analyzeSlot] != null)
				return;

			// Source slot to analyze empty
			if (getStackInSlot(specimenSlot) == null)
				return;

			IBee bee = PluginApiculture.beeInterface.getMember(getStackInSlot(specimenSlot));
			// No bee, abort
			if (bee == null)
				return;

			// Requires energy
			if (!isEnergy(getStackInSlot(energySlot)))
				return;

			biomesToSearch = bee.getSuitableBiomeIds();

			// Decrease energy
			decrStackSize(energySlot, 1);

			setInventorySlotContents(analyzeSlot, getStackInSlot(specimenSlot));
			setInventorySlotContents(specimenSlot, null);
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
			if (PluginApiculture.beeInterface.isMember(inventoryStacks[specimenSlot]) && !isEnergy(getStackInSlot(energySlot)))
				return EnumErrorCode.NOHONEY;

			return EnumErrorCode.OK;
		}

	}

	public ItemBiomefinder() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(world))
			entityplayer.openGui(ForestryAPI.instance, GuiId.HabitatLocatorGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);

		return itemstack;
	}

	public void startBiomeSearch(World world, EntityPlayer player, ArrayList<Integer> biomesToSearch) {

		ArrayList<Integer> excludedBiomes = new ArrayList<Integer>();
		excludedBiomes.add(BiomeGenBase.ocean.biomeID);
		excludedBiomes.add(BiomeGenBase.frozenOcean.biomeID);
		excludedBiomes.add(BiomeGenBase.beach.biomeID);
		biomesToSearch.removeAll(excludedBiomes);

		// If we are in a valid biome, we point to ourself.
		BiomeGenBase biome = world.getBiomeGenForCoords((int) player.posX, (int) player.posZ);
		if (biomesToSearch.contains(biome.biomeID)) {
			Proxies.common.setBiomefinderCoordinates(player, new ChunkCoordinates((int) player.posX, (int) player.posY, (int) player.posZ));
			return;
		}

		if (Proxies.common.isSimulating(world) && biomesToSearch.size() > 0) {
			ChunkCoordinates target = findNearestBiome(player, biomesToSearch);
			Proxies.common.setBiomefinderCoordinates(player, target);
		}

	}

	private ChunkCoordinates findNearestBiome(EntityPlayer player, ArrayList<Integer> biomesToSearch) {

		int loadChunkDistance = 25;
		ChunkCoordinates coordinates = null;
		Vect pos = new Vect((int) player.posX, (int) player.posY, (int) player.posZ);
		for (int i = 0; i < 100; i++) {
			// Test direction +X
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(10 * i, 0, 0)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;
			// Test direction +X+Z
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(10 * i, 0, 10 * i)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;
			// Test direction +X-Z
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(10 * i, 0, -10 * i)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;

			// Test direction -X
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(-10 * i, 0, 0)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;
			// Test direction -X+Z
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(-10 * i, 0, 10 * i)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;
			// Test direction -X-Z
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(-10 * i, 0, -10 * i)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;

			// Test direction +Z
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(0, 0, 10 * i)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;
			// Test direction -Z
			if ((coordinates = getChunkCoordinates(pos.add(new Vect(0, 0, -10 * i)), player.worldObj, biomesToSearch, i < loadChunkDistance)) != null)
				return coordinates;
		}
		return coordinates;
	}

	private ChunkCoordinates getChunkCoordinates(Vect pos, World world, ArrayList<Integer> biomesToSearch, boolean loadChunks) {

		BiomeGenBase biome = world.getBiomeGenForCoords(pos.x, pos.z);

		if (biomesToSearch.contains(biome.biomeID))
			return new ChunkCoordinates(pos.x, pos.y, pos.z);

		return null;
	}

	/* TEXTURES */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		if(register instanceof TextureMap) {
			TextureAtlasSprite texture = new TextureBiomefinder();
			((TextureMap)register).setTextureEntry("forestry:biomefinder", texture);
			itemIcon = texture;
		}
	}

}
