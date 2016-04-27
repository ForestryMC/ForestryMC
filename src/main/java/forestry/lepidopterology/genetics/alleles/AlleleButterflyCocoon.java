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
package forestry.lepidopterology.genetics.alleles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import forestry.api.genetics.AlleleManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumCocoonType;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleCategorized;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class AlleleButterflyCocoon extends AlleleCategorized implements IAlleleButterflyCocoon {

	public static IAlleleButterflyCocoon cocoonDefault;
	public static IAlleleButterflyCocoon cocoonSilk;
	private static List<IAlleleButterflyCocoon> cocoonAlleles;

	public static void createAlleles() {
		List<IAlleleButterflyCocoon> cocoonAlleles = Arrays.asList(
				cocoonDefault = new AlleleButterflyCocoon("default", EnumCocoonType.DEFAULT, false),
				cocoonSilk = new AlleleButterflyCocoon("silk", EnumCocoonType.SILK, false)
		);
		
		cocoonSilk.getCocoonLoot().put(PluginCore.items.craftingMaterial.getSilkWisp(), 0.75F);
		cocoonSilk.getCocoonLoot().put(PluginCore.items.craftingMaterial.getSilkWisp(), 0.25F);

		for (IAlleleButterflyCocoon cocoonAllele : cocoonAlleles) {
			AlleleManager.alleleRegistry.registerAllele(cocoonAllele, EnumButterflyChromosome.COCOON);
		}
	}
	
	@Nonnull
	private EnumCocoonType type;
	private Map<ItemStack, Float> loot = new HashMap();
	
	public AlleleButterflyCocoon(String name, @Nonnull EnumCocoonType type, boolean isDominant) {
		super(Constants.MOD_ID, "cocoon", name, isDominant);
		this.type = type;
	}

	@Override
	public EnumCocoonType getCocoonType() {
		return type;
	}
	
	@Override
	public String getCocoonTexture(int age) {
		return "textures/blocks/lepidopterology/cocoons/" + type + "_" + age + ".png";
	}
	
	@Override
	public ModelResourceLocation getCocoonItemModel(int age) {
		return new ModelResourceLocation(Constants.RESOURCE_ID + ":lepidopterology/cocoons/" + type + "_" + age + ".png", "inventory");
	}

	@Override
	public Map<ItemStack, Float> getCocoonLoot() {
		return loot;
	}
	
}
