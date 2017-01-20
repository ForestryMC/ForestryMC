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
package forestry.arboriculture.genetics.alleles;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IAlleleTreeSpeciesBuilder;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.PluginArboriculture;
import forestry.core.genetics.alleles.AlleleSpecies;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpeciesBuilder, IAlleleTreeSpecies {
	@Nonnull
	private final ITreeGenerator generator;
	@Nonnull
	private final IWoodProvider woodProvider;
	@Nonnull
	private final IGermlingModelProvider germlingModelProvider;
	@Nonnull
	private final ILeafSpriteProvider leafSpriteProvider;
	@Nonnull
	private final List<IFruitFamily> fruits = new ArrayList<>();
	@Nonnull
	private final String modID;
	@Nonnull
	private EnumPlantType nativeType = EnumPlantType.Plains;

	public AlleleTreeSpecies(
			@Nonnull String uid,
			@Nonnull String unlocalizedName,
			@Nonnull String authority,
			@Nonnull String unlocalizedDescription,
			boolean isDominant,
			@Nonnull IClassification branch,
			@Nonnull String binomial,
			@Nonnull String modID,
			@Nonnull ILeafSpriteProvider leafIconProvider,
			@Nonnull IGermlingModelProvider germlingModelProvider,
			@Nonnull IWoodProvider woodProvider,
			@Nonnull ITreeGenerator generator) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);

		this.generator = generator;
		this.germlingModelProvider = germlingModelProvider;
		this.woodProvider = woodProvider;
		this.leafSpriteProvider = leafIconProvider;
		
		this.modID = modID;
	}

	@Override
	public IAlleleTreeSpecies build() {
		AlleleManager.alleleRegistry.registerAllele(this, EnumTreeChromosome.SPECIES);
		return this;
	}

	@Nonnull
	@Override
	public ITreeRoot getRoot() {
		return TreeManager.treeRoot;
	}

	@Override
	public AlleleTreeSpecies setPlantType(EnumPlantType type) {
		nativeType = type;
		return this;
	}

	@Override
	public AlleleTreeSpecies addFruitFamily(IFruitFamily family) {
		fruits.add(family);
		return this;
	}

	/* OTHER */
	@Nonnull
	@Override
	public EnumPlantType getPlantType() {
		return nativeType;
	}

	@Nonnull
	@Override
	public List<IFruitFamily> getSuitableFruit() {
		return fruits;
	}

	@Nonnull
	@Override
	public ITreeGenerator getGenerator() {
		return generator;
	}

	@Nonnull
	@Override
	public ILeafSpriteProvider getLeafSpriteProvider() {
		return leafSpriteProvider;
	}

	@Override
	public int getSpriteColour(int renderPass) {
		return leafSpriteProvider.getColor(false);
	}
	
	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public ModelResourceLocation getGermlingModel(EnumGermlingType type) {
		return germlingModelProvider.getModel(type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getGermlingColour(EnumGermlingType type, int renderPass) {
		return germlingModelProvider.getSpriteColor(type, renderPass);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager, EnumGermlingType type) {
		germlingModelProvider.registerModels(item, manager, type);
	}
	
	@Override
	@Nonnull
	public IWoodProvider getWoodProvider() {
		return woodProvider;
	}

	@Nonnull
	@Override
	public String getModID() {
		return modID;
	}
	
	@Override
	public int compareTo(@Nonnull IAlleleTreeSpecies o) {
		return 0;
	}

	@Override
	public ItemStack getDecorativeLeave() {
		return PluginArboriculture.blocks.getDecorativeLeaves(getUID());
	}

}
