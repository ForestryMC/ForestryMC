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

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.PlantType;

import genetics.api.individual.IIndividual;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.flowers.IFlowerProvider;

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
	public boolean isAcceptedPollinatable(Level world, ICheckPollinatable pollinatable) {

		PlantType plantType = pollinatable.getPlantType();

		return switch (flowerType) {
			case FlowerManager.FlowerTypeNether -> plantType == PlantType.NETHER;
			case FlowerManager.FlowerTypeCacti -> plantType == PlantType.DESERT;
			default -> plantType != PlantType.NETHER;
		};
	}

	@Override
	public Component getDescription() {
		return Component.translatable(this.unlocalizedDescription);
	}

	@Override
	public NonNullList<ItemStack> affectProducts(Level world, IIndividual individual, BlockPos pos, NonNullList<ItemStack> products) {
		return products;
	}

}
