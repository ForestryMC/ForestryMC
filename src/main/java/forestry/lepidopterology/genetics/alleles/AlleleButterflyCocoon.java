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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.lepidopterology.blocks.PropertyCocoon;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class AlleleButterflyCocoon extends AlleleCategorized implements IAlleleButterflyCocoon {
	public static final PropertyCocoon COCOON = new PropertyCocoon("cocoon");
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);

	private final Map<ItemStack, Float> loot = new HashMap<>();
	private final String name;

	public AlleleButterflyCocoon(String name, boolean isDominant) {
		super(Constants.MOD_ID, "cocoon", name, isDominant);
		this.name = name;
	}

	private static String getAgeKey(int age) {
		if (age == 0) {
			return "early";
		} else if (age == 1) {
			return "middle";
		} else {
			return "late";
		}
	}

	@Override
	public String getCocoonName() {
		return name;
	}

	@Override
	public ModelResourceLocation getCocoonItemModel(int age) {
		return new ModelResourceLocation(
				Constants.MOD_ID + ":lepidopterology/cocoons/cocoon_" + name + "_" + getAgeKey(age), "inventory");
	}

	@Override
	public void clearLoot() {
		this.loot.clear();
	}

	@Override
	public void addLoot(ItemStack loot, float chance) {
		this.loot.put(loot, chance);
	}

	@Override
	public Map<ItemStack, Float> getCocoonLoot() {
		return Collections.unmodifiableMap(loot);
	}

	@Override
	public int compareTo(IAlleleButterflyCocoon o) {
		return 0;
	}

}
