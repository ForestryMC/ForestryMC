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
package forestry.storage;

import java.awt.Color;
import java.util.function.Predicate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.api.storage.IBackpackDefinition;

public class BackpackDefinition implements IBackpackDefinition {
	private final int primaryColor;
	private final int secondaryColor;
	private final Predicate<ItemStack> filter;

	public BackpackDefinition(Color primaryColor, Color secondaryColor, Predicate<ItemStack> filter) {
		this.primaryColor = primaryColor.getRGB();
		this.secondaryColor = secondaryColor.getRGB();
		this.filter = filter;
	}

	@Override
	public Predicate<ItemStack> getFilter() {
		return filter;
	}

	@Override
	public ITextComponent getName(ItemStack backpack) {
		Item item = backpack.getItem();
		ITextComponent display = new TranslationTextComponent((item.getDescriptionId(backpack)).trim());

		CompoundNBT tagCompound = backpack.getTag();
		if (tagCompound != null && tagCompound.contains("display", 10)) {
			CompoundNBT nbt = tagCompound.getCompound("display");

			if (nbt.contains("Name", 8)) {
				display = new StringTextComponent(nbt.getString("Name"));
			}
		}

		return display;
	}

	@Override
	public int getPrimaryColour() {
		return primaryColor;
	}

	@Override
	public int getSecondaryColour() {
		return secondaryColor;
	}
}
