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

import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.utils.Translator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BackpackDefinition implements IBackpackDefinition {
	private final int primaryColor;
	private final int secondaryColor;
	private final Predicate<ItemStack> filter;

	public BackpackDefinition(Color primaryColor, Color secondaryColor) {
		this(primaryColor, secondaryColor, BackpackManager.backpackInterface.createBackpackFilter());
	}

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
	public String getName(ItemStack backpack) {
		Item item = backpack.getItem();
		String display = Translator.translateToLocal(item.getUnlocalizedNameInefficiently(backpack) + ".name").trim();

		NBTTagCompound tagCompound = backpack.getTagCompound();
		if (tagCompound != null && tagCompound.hasKey("display", 10)) {
			NBTTagCompound nbt = tagCompound.getCompoundTag("display");

			if (nbt.hasKey("Name", 8)) {
				display = nbt.getString("Name");
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
