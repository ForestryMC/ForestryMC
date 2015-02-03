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

import java.util.EnumSet;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.utils.StringUtil;

public class FlowerProvider implements IFlowerProvider {

	private final String flowerType;
	private final String description;

	public FlowerProvider(String flowerType, String description) {
		this.flowerType = flowerType;
		this.description = description;
	}

	@Override
	public boolean isAcceptedFlower(World world, IIndividual individual, BlockPos pos) {
		return FlowerManager.flowerRegistry.isAcceptedFlower(this.flowerType, world, individual, pos);
	}

	@Override
	public boolean isAcceptedPollinatable(World world, IPollinatable pollinatable) {
		EnumSet<EnumPlantType> types = pollinatable.getPlantType();

		if (flowerType.equals(FlowerManager.FlowerTypeNether)) {
			return types.contains(EnumPlantType.Nether);
		} else if (flowerType.equals(FlowerManager.FlowerTypeCacti)) {
			return types.contains(EnumPlantType.Desert);
		} else {
			return types.size() > 1 || !types.contains(EnumPlantType.Nether);
		}
	}

	@Override
	public boolean growFlower(World world, IIndividual individual, BlockPos pos) {
		return FlowerManager.flowerRegistry.growFlower(this.flowerType, world, individual, pos);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize(this.description);
	}

	@Override
	public ItemStack[] affectProducts(World world, IIndividual individual, BlockPos pos, ItemStack[] products) {
		return products;
	}

	@Override
	public List<IFlower> getFlowers() {
		return FlowerManager.flowerRegistry.getAcceptableFlowers(this.flowerType);
	}

}
