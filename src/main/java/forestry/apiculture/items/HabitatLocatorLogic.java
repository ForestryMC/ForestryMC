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

import com.mojang.datafixers.util.Pair;
import forestry.api.apiculture.genetics.IBee;
import forestry.apiculture.network.packets.PacketHabitatBiomePointer;
import forestry.core.utils.NetworkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class HabitatLocatorLogic {

	private Set<Biome> targetBiomes = new HashSet<>();
	private boolean biomeFound = false;
	@Nullable
	private BlockPos searchCenter;

	public boolean isBiomeFound() {
		return biomeFound;
	}

	public Set<Biome> getTargetBiomes() {
		return targetBiomes;
	}

	public void startBiomeSearch(IBee bee, Player player) {
		this.targetBiomes = new HashSet<>(bee.getSuitableBiomes());
		this.biomeFound = false;
		this.searchCenter = player.blockPosition();

		// reset the locator coordinates
		if (player.level.isClientSide) {
			//TextureHabitatLocator.getInstance().setTargetCoordinates(null);//TODO: TextureHabitatLocator
		}
	}

	public void onUpdate(Level world, Entity player) {
		if (!(player instanceof ServerPlayer serverPlayer) || targetBiomes.isEmpty() || searchCenter == null) {
			return;
		}

		// once we've found the biome, slow down
		if (biomeFound && world.getGameTime() % 50 != 0) {
			return;
		}

		Pair<BlockPos, Holder<Biome>> pair = ((ServerLevel) player.getLevel()).findClosestBiome3d(biome -> targetBiomes.contains(biome.get()), searchCenter, 4096, 32, 64);

		// send an update if we find the biome
		if (pair != null) {
			NetworkUtil.sendToPlayer(new PacketHabitatBiomePointer(pair.getFirst()), serverPlayer);
			biomeFound = true;
		}
	}
}
