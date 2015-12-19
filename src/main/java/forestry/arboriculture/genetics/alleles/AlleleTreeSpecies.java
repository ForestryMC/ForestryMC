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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IAlleleTreeSpeciesCustom;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.IGermlingSpriteProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteProvider;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.render.TextureManager;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpeciesCustom, ISpriteProvider {
	private final ITreeGenerator generator;
	private final IGermlingSpriteProvider germlingIconProvider;
	private final IGermlingModelProvider germlingModelProvider;
	private final ILeafSpriteProvider leafIconProvider;
	private final List<IFruitFamily> fruits = new ArrayList<>();

	private EnumPlantType nativeType = EnumPlantType.Plains;

	public AlleleTreeSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean isDominant, IClassification branch, String binomial, ILeafSpriteProvider leafIconProvider, IGermlingSpriteProvider germlingIconProvider, IGermlingModelProvider germlingModelProvider, ITreeGenerator generator) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);

		this.generator = generator;
		this.germlingIconProvider = germlingIconProvider;
		this.germlingModelProvider = germlingModelProvider;
		this.leafIconProvider = leafIconProvider;
	}

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
	@Override
	public EnumPlantType getPlantType() {
		return nativeType;
	}

	@Override
	public List<IFruitFamily> getSuitableFruit() {
		return fruits;
	}

	@Override
	public ITreeGenerator getGenerator() {
		return generator;
	}

	@Override
	public TextureAtlasSprite getLeafSprite(boolean pollinated, boolean fancy) {
		return leafIconProvider.getSprite(pollinated, fancy);
	}

	@Override
	public int getLeafColour(boolean pollinated) {
		return leafIconProvider.getColor(pollinated);
	}

	@Override
	public int getSpriteColour(int renderPass) {
		return leafIconProvider.getColor(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprites() {
		germlingIconProvider.registerIcons();
	}

	@Override
	public ModelResourceLocation getGermlingModel(EnumGermlingType type) {
		return germlingModelProvider.getModel(type);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getGermlingSprite(EnumGermlingType type, int renderPass) {
		return germlingIconProvider.getSprite(type, renderPass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getGermlingColour(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.SAPLING) {
			return 0xFFFFFF;
		}
		return getLeafColour(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISpriteProvider getSpriteProvider() {
		return this;
	}
	
	@Override
	public void registerModels(IModelManager manager) {
		germlingModelProvider.registerModels(manager);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getSprite(short texUID) {
		return TextureManager.getInstance().getSprite(texUID);
	}

	@Override
	public String getModID() {
		return "forestry";
	}

	@Override
	public int compareTo(IAlleleTreeSpecies o) {
		return 0;
	}

}
