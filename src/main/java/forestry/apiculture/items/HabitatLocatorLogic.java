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
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.apiculture.IBee;
import forestry.apiculture.network.packets.PacketHabitatBiomePointer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.vect.Vect;

public class HabitatLocatorLogic {
	private static final int maxChecksPerTick = 100;
	private static final int maxSearchRadiusIterations = 500;
	private static final int spacing = 20;
	private static final int minBiomeRadius = 8;

	private static final Set<BiomeGenBase> waterBiomes = new HashSet<>();
	private static final Set<BiomeGenBase> netherBiomes = new HashSet<>();
	private static final Set<BiomeGenBase> endBiomes = new HashSet<>();

	static {
		Collections.addAll(waterBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.BEACH));
		Collections.addAll(waterBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.OCEAN));
		Collections.addAll(waterBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.RIVER));

		Collections.addAll(netherBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.NETHER));

		Collections.addAll(endBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.END));
	}

	private Set<BiomeGenBase> targetBiomes = new HashSet<>();
	private boolean biomeFound = false;
	private int searchRadiusIteration = 0;
	private int searchAngleIteration = 0;
	private Vect searchCenter;

	public boolean isBiomeFound() {
		return biomeFound;
	}

	public Set<BiomeGenBase> getTargetBiomes() {
		return targetBiomes;
	}

	public void startBiomeSearch(IBee bee, EntityPlayer player) {
		this.targetBiomes = new HashSet<>(bee.getSuitableBiomes());
		this.searchAngleIteration = 0;
		this.searchRadiusIteration = 0;
		this.biomeFound = false;
		this.searchCenter = new Vect(player);

		BiomeGenBase currentBiome = player.worldObj.getBiomeGenForCoords(searchCenter.x, searchCenter.z);
		removeInvalidBiomes(currentBiome, targetBiomes);

		// reset the locator coordinates
		Proxies.render.setHabitatLocatorTexture(null, null);
	}

	public void onUpdate(World world, Entity player) {
		if (world.isRemote) {
			return;
		}

		if (targetBiomes.isEmpty()) {
			return;
		}

		// once we've found the biome, slow down to conserve cpu and network data
		if (biomeFound && world.getTotalWorldTime() % 20 != 0) {
			return;
		}

		ChunkCoordinates target = findNearestBiome(player, targetBiomes);

		// send an update if we find the biome
		if (target != null && player instanceof EntityPlayerMP) {
			Proxies.net.sendToPlayer(new PacketHabitatBiomePointer(target), (EntityPlayerMP) player);
			biomeFound = true;
		}
	}

	private ChunkCoordinates findNearestBiome(Entity player, Collection<BiomeGenBase> biomesToSearch) {
		Vect playerPos = new Vect(player);

		// If we are in a valid spot, we point to ourselves.
		ChunkCoordinates coordinates = getChunkCoordinates(playerPos, player.worldObj, biomesToSearch);
		if (coordinates != null) {
			searchAngleIteration = 0;
			searchRadiusIteration = 0;
			return new ChunkCoordinates(playerPos.x, playerPos.y, playerPos.z);
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

	private static ChunkCoordinates getChunkCoordinates(Vect pos, World world, Collection<BiomeGenBase> biomesToSearch) {
		BiomeGenBase biome;

		biome = world.getBiomeGenForCoords(pos.x, pos.z);
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(pos.x - minBiomeRadius, pos.z);
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(pos.x + minBiomeRadius, pos.z);
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(pos.x, pos.z - minBiomeRadius);
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		biome = world.getBiomeGenForCoords(pos.x, pos.z + minBiomeRadius);
		if (!biomesToSearch.contains(biome)) {
			return null;
		}

		return new ChunkCoordinates(pos.x, pos.y, pos.z);
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
