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
package forestry.apiculture.flowers;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.utils.StringUtil;

public class FlowerProvider implements IFlowerProvider {
	@Nonnull
	private final String flowerType;
	@Nonnull
	private final String description;

	public FlowerProvider(@Nonnull String flowerType, @Nonnull String description) {
		this.flowerType = flowerType;
		this.description = description;
	}

	@Nonnull
	@Override
	public String getFlowerType() {
		return flowerType;
	}

	@Override
	public boolean isAcceptedPollinatable(@Nonnull World world, @Nonnull IPollinatable pollinatable) {

		EnumSet<EnumPlantType> plantTypes = pollinatable.getPlantType();

		switch (flowerType) {
			case FlowerManager.FlowerTypeNether:
				return plantTypes.contains(EnumPlantType.Nether);
			case FlowerManager.FlowerTypeCacti:
				return plantTypes.contains(EnumPlantType.Desert);
			default:
				return plantTypes.size() > 1 || !plantTypes.contains(EnumPlantType.Nether);
		}
	}

	@Nonnull
	@Override
	public String getDescription() {
		return StringUtil.localize(this.description);
	}

	@Nonnull
	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, BlockPos pos, ItemStack[] products) {
		return products;
	}

}
