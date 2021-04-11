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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import forestry.api.apiculture.genetics.IBee;
import forestry.apiculture.network.packets.PacketHabitatBiomePointer;
import forestry.core.utils.NetworkUtil;

public class HabitatLocatorLogic {
	private static final int maxChecksPerTick = 100;
	private static final int maxSearchRadiusIterations = 500;
	private static final int spacing = 20;
	private static final int minBiomeRadius = 8;

	private static final Set<ResourceLocation> waterBiomes = new HashSet<>();
	private static final Set<ResourceLocation> netherBiomes = new HashSet<>();
	private static final Set<ResourceLocation> endBiomes = new HashSet<>();

	@SubscribeEvent
	static void onBiomeLoaded(BiomeLoadingEvent event) {
		switch (event.getCategory()) {
			case BEACH:
			case RIVER:
			case OCEAN:
				waterBiomes.add(event.getName());
				break;
			case NETHER:
				netherBiomes.add(event.getName());
				break;
			case THEEND:
				endBiomes.add(event.getName());
				break;
		}
	}

	private Set<ResourceLocation> targetBiomes = new HashSet<>();
	private boolean biomeFound = false;
	private int searchRadiusIteration = 0;
	private int searchAngleIteration = 0;
	@Nullable
	private BlockPos searchCenter;

	public boolean isBiomeFound() {
		return biomeFound;
	}

	public Set<ResourceLocation> getTargetBiomes() {
		return targetBiomes;
	}

	public void startBiomeSearch(IBee bee, PlayerEntity player) {
		this.targetBiomes = new HashSet<>(bee.getSuitableBiomes());
		this.searchAngleIteration = 0;
		this.searchRadiusIteration = 0;
		this.biomeFound = false;
		this.searchCenter = player.blockPosition();

		Biome currentBiome = player.level.getBiome(searchCenter);
		removeInvalidBiomes(currentBiome, targetBiomes);

		// reset the locator coordinates
		if (player.level.isClientSide) {
			//TextureHabitatLocator.getInstance().setTargetCoordinates(null);//TODO: TextureHabitatLocator
		}
	}

	public void onUpdate(World world, Entity player) {
		if (world.isClientSide) {
			return;
		}

		if (targetBiomes.isEmpty()) {
			return;
		}

		// once we've found the biome, slow down to conserve cpu and network data
		if (biomeFound && world.getGameTime() % 20 != 0) {
			return;
		}

		BlockPos target = findNearestBiome(player, targetBiomes);

		// send an update if we find the biome
		if (target != null && player instanceof ServerPlayerEntity) {
			NetworkUtil.sendToPlayer(new PacketHabitatBiomePointer(target), (ServerPlayerEntity) player);
			biomeFound = true;
		}
	}

	@Nullable
	private BlockPos findNearestBiome(Entity player, Collection<ResourceLocation> biomesToSearch) {
		if (searchCenter == null) {
			return null;
		}

		BlockPos playerPos = player.blockPosition();

		// If we are in a valid spot, we point to ourselves.
		BlockPos coordinates = getChunkCoordinates(playerPos, player.level, biomesToSearch);
		if (coordinates != null) {
			searchAngleIteration = 0;
			searchRadiusIteration = 0;
			return playerPos;
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
			BlockPos pos = searchCenter.offset(xOffset, 0, zOffset);

			coordinates = getChunkCoordinates(pos, player.level, biomesToSearch);
			if (coordinates != null) {
				searchAngleIteration = 0;
				searchRadiusIteration = 0;
				return coordinates;
			}
		}

		return null;
	}

	@Nullable
	private static BlockPos getChunkCoordinates(BlockPos pos, World world, Collection<ResourceLocation> biomesToSearch) {
		Biome biome;

		biome = world.getBiome(pos);
		if (!biomesToSearch.contains(biome.getRegistryName())) {
			return null;
		}

		biome = world.getBiome(pos.offset(-minBiomeRadius, 0, 0));
		if (!biomesToSearch.contains(biome.getRegistryName())) {
			return null;
		}

		biome = world.getBiome(pos.offset(minBiomeRadius, 0, 0));
		if (!biomesToSearch.contains(biome.getRegistryName())) {
			return null;
		}

		biome = world.getBiome(pos.offset(0, 0, -minBiomeRadius));
		if (!biomesToSearch.contains(biome.getRegistryName())) {
			return null;
		}

		biome = world.getBiome(pos.offset(0, 0, minBiomeRadius));
		if (!biomesToSearch.contains(biome.getRegistryName())) {
			return null;
		}

		return pos;
	}

	private static void removeInvalidBiomes(Biome currentBiome, Set<ResourceLocation> biomesToSearch) {
		biomesToSearch.removeAll(waterBiomes);

		if (currentBiome.getBiomeCategory() == Biome.Category.NETHER) {
			biomesToSearch.retainAll(netherBiomes);
		} else {
			biomesToSearch.removeAll(netherBiomes);
		}

		if (currentBiome.getBiomeCategory() == Biome.Category.THEEND) {
			biomesToSearch.retainAll(endBiomes);
		} else {
			biomesToSearch.removeAll(endBiomes);
		}
	}
}
