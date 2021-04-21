/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.PlantType;

import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.alleles.IAlleleProperty;

public interface IAlleleTreeSpecies extends IAlleleForestrySpecies, IAlleleProperty<IAlleleTreeSpecies> {

	@Override
	@Nonnull
	ITreeRoot getRoot();

	/**
	 * @return Native plant type of this species.
	 */
	PlantType getPlantType();

	/**
	 * @return List of all {@link IFruitFamily}s which can grow on leaves generated by this species.
	 */
	Collection<IFruitFamily> getSuitableFruit();

	/**
	 * @return Tree generator for this species.
	 */
	ITreeGenerator getGenerator();

	/**
	 * @return Float between 0 and 1 representing the rarity of the species, will affect spawn rate. If it's 0, it will not spawn.
	 */
	float getRarity();

	Optional<ILeafProvider> getLeafProvider();

	default ItemStack getDecorativeLeaves() {
		return getLeafProvider().map(ILeafProvider::getDecorativeLeaves).orElse(ItemStack.EMPTY);
	}

	IGrowthProvider getGrowthProvider();

	/* MODELS AND OVERRIDES */
	@OnlyIn(Dist.CLIENT)
	ILeafSpriteProvider getLeafSpriteProvider();

	@OnlyIn(Dist.CLIENT)
	int getGermlingColour(EnumGermlingType type, int renderPass);

	@OnlyIn(Dist.CLIENT)
	ModelResourceLocation getItemModel();

	@OnlyIn(Dist.CLIENT)
	ResourceLocation getBlockModel();

}
