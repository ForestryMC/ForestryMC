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
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.model.ModelResourceLocation;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpeciesCustom;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafIconProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelProvider;
import forestry.api.core.sprite.ISprite;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.utils.GeneticsUtil;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpeciesCustom {

	private final ITreeGenerator generator;
	//private final IGermlingIconProvider germlingIconProvider;
	private final IGermlingModelProvider germlingModelProvider;
	private final ILeafIconProvider leafIconProvider;
	private final List<IFruitFamily> fruits = new ArrayList<IFruitFamily>();

	private EnumPlantType nativeType = EnumPlantType.Plains;

	public AlleleTreeSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean isDominant, IClassification branch, String binomial, ILeafIconProvider leafIconProvider, IGermlingModelProvider germlingModelProvider, ITreeGenerator generator) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial, false);

		this.generator = generator;
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

	/* RESEARCH */
	@Override
	public int getComplexity() {
		return GeneticsUtil.getResearchComplexity(this, EnumTreeChromosome.SPECIES);
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
	public ISprite getLeafIcon(boolean pollinated, boolean fancy) {
		return leafIconProvider.getIcon(pollinated, fancy);
	}

	@Override
	public int getLeafColour(boolean pollinated) {
		return leafIconProvider.getColor(pollinated);
	}

	@Override
	public int getIconColour(int renderPass) {
		return 0xffffff;
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		germlingIconProvider.registerIcons(register);
	}*/
	
	@Override
	public IModelProvider getModelProvider() {
		return null;
	}
	

	@Override
	public ModelResourceLocation getGermlingModel(EnumGermlingType type) {
		return germlingModelProvider.getModel(type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getGermlingColour(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.SAPLING) {
			return 0xFFFFFF;
		}
		return getLeafColour(false);
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public ISpriteProvider getIconProvider() {
		return this;
	}*/

	/*@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(short texUID) {
		return TextureManager.getInstance().getIcon(texUID);
	}*/

	@Override
	public ISprite getGermlingIcon(EnumGermlingType type, int renderPass) {
		return null;
	}

	@Override
	public void registerModels(IModelManager manager) {
		germlingModelProvider.registerModels(manager);
	}

}
