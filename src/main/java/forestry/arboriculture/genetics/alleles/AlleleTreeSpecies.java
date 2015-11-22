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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import net.minecraftforge.common.EnumPlantType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpeciesCustom;
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.api.arboriculture.ILeafIconProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.render.TextureManager;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpeciesCustom, IIconProvider {
	private final ITreeGenerator generator;
	private final IGermlingIconProvider germlingIconProvider;
	private final ILeafIconProvider leafIconProvider;
	private final List<IFruitFamily> fruits = new ArrayList<>();

	private EnumPlantType nativeType = EnumPlantType.Plains;

	public AlleleTreeSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean isDominant, IClassification branch, String binomial, ILeafIconProvider leafIconProvider, IGermlingIconProvider germlingIconProvider, ITreeGenerator generator) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);

		this.generator = generator;
		this.germlingIconProvider = germlingIconProvider;
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
	public IIcon getLeafIcon(boolean pollinated, boolean fancy) {
		return leafIconProvider.getIcon(pollinated, fancy);
	}

	@Override
	public int getLeafColour(boolean pollinated) {
		return leafIconProvider.getColor(pollinated);
	}

	@Override
	public int getIconColour(int renderPass) {
		return leafIconProvider.getColor(false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		germlingIconProvider.registerIcons(register);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getGermlingIcon(EnumGermlingType type, int renderPass) {
		return germlingIconProvider.getIcon(type, renderPass);
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
	public IIconProvider getIconProvider() {
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(short texUID) {
		return TextureManager.getInstance().getIcon(texUID);
	}

}
