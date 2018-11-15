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

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.core.utils.Translator;

public class FlowerProvider implements IFlowerProvider {

	private final String flowerType;
	private final String unlocalizedDescription;

	public FlowerProvider(String flowerType, String unlocalizedDescription) {
		this.flowerType = flowerType;
		this.unlocalizedDescription = unlocalizedDescription;
	}

	@Override
	public String getFlowerType() {
		return flowerType;
	}

	@Override
	public boolean isAcceptedPollinatable(World world, ICheckPollinatable pollinatable) {

		EnumPlantType plantType = pollinatable.getPlantType();

		switch (flowerType) {
			case FlowerManager.FlowerTypeNether:
				return plantType == EnumPlantType.Nether;
			case FlowerManager.FlowerTypeCacti:
				return plantType == EnumPlantType.Desert;
			default:
				return plantType != EnumPlantType.Nether;
		}
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocal(this.unlocalizedDescription);
	}

	@Override
	public NonNullList<ItemStack> affectProducts(World world, IIndividual individual, BlockPos pos, NonNullList<ItemStack> products) {
		return products;
	}

}
