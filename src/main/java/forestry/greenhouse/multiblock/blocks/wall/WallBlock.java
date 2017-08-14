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
package forestry.greenhouse.multiblock.blocks.wall;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.math.BlockPos;

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateSource;
import forestry.api.climate.IClimateSourceOwner;
import forestry.api.core.ForestryAPI;
import forestry.api.greenhouse.IBlankBlock;
import forestry.api.greenhouse.IGreenhouseBlockHandler;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.greenhouse.IWallBlock;
import forestry.api.greenhouse.Position2D;
import forestry.greenhouse.multiblock.blocks.GreenhouseBlock;

public class WallBlock extends GreenhouseBlock<IBlankBlock> implements IWallBlock {

	private final List<IClimateSource> sources;

	public WallBlock(IGreenhouseProvider manager, BlockPos pos) {
		super(manager, pos);
		this.sources = new ArrayList<>();
	}

	@Override
	public void setRoot(IBlankBlock parent) {
		this.root = parent;
	}

	@Override
	public void onCreate() {
		IClimateContainer container = provider.getClimateContainer();
		for (IClimateSourceOwner sourceOwner : ForestryAPI.climateManager.getSources(provider.getWorld(), new Position2D(pos))) {
			IClimateSource source = sourceOwner.getClimateSource();
			sources.add(source);
			container.addClimateSource(source);
			source.onAdded(container);
		}
	}

	@Override
	public void onRemove() {
		IClimateContainer container = provider.getClimateContainer();
		Iterator<IClimateSource> iterator = sources.iterator();
		while (iterator.hasNext()) {
			IClimateSource source = iterator.next();
			source.onRemoved(container);
			container.removeClimateSource(source);
			iterator.remove();
		}
	}

	@Override
	public void add(IClimateSource source) {
		sources.add(source);
	}

	@Override
	public IGreenhouseBlockHandler getHandler() {
		return WallBlockHandler.getInstance();
	}

}
