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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.PlantType;

import genetics.api.individual.IIndividual;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IFlowerProvider;

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

		PlantType plantType = pollinatable.getPlantType();

		switch (flowerType) {
			case FlowerManager.FlowerTypeNether:
				return plantType == PlantType.Nether;
			case FlowerManager.FlowerTypeCacti:
				return plantType == PlantType.Desert;
			default:
				return plantType != PlantType.Nether;
		}
	}

	@Override
	public ITextComponent getDescription() {
		return new TranslationTextComponent(this.unlocalizedDescription);
	}

	@Override
	public NonNullList<ItemStack> affectProducts(World world, IIndividual individual, BlockPos pos, NonNullList<ItemStack> products) {
		return products;
	}

}
