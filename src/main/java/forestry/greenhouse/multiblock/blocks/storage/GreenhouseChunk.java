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
package forestry.greenhouse.multiblock.blocks.storage;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.api.greenhouse.IGreenhouseChunk;
import forestry.api.greenhouse.IGreenhouseProvider;

public class GreenhouseChunk implements IGreenhouseChunk {

	/**
	 * The greenhouse managers of the greenhouses that are located in the world chunk on this chunk position.
	 */
	public final Set<IGreenhouseProvider> providers;
	/**
	 * The managers that were modified in the last tick.
	 */
	public final Set<IGreenhouseProvider> dirtyProviders;

	public GreenhouseChunk() {
		this.providers = new HashSet<>();
		this.dirtyProviders = new HashSet<>();
	}

	public Set<IGreenhouseProvider> getProviders() {
		return providers;
	}

	public synchronized void markProviderDirty(BlockPos pos) {
		IGreenhouseBlock block = get(pos);
		if (block != null) {
			dirtyProviders.add(block.getProvider());
		}
	}

	public synchronized Collection<IGreenhouseProvider> getDirtyProviders() {
		return dirtyProviders;
	}

	@Nullable
	public IGreenhouseBlock get(BlockPos pos) {
		IGreenhouseBlock block = null;
		for (IGreenhouseProvider provider : providers) {
			block = provider.getStorage().getBlock(pos);
			if (block != null) {
				break;
			}
		}
		return block;
	}

	public void add(IGreenhouseProvider manager) {
		providers.add(manager);
	}

	public void remove(IGreenhouseProvider manager) {
		providers.remove(manager);
	}

	public synchronized void markProviderDirty(IGreenhouseProvider dirtyProvider) {
		if (dirtyProvider != null) {
			dirtyProviders.add(dirtyProvider);
		}
	}
}
