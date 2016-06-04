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

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Translator;

public class BackpackDefinition implements IBackpackDefinition {

	private final int primaryColor;
	private final int secondaryColor;

	private final Set<String> validItemStacks = new HashSet<>();
	private final Set<Integer> validOreIds = new HashSet<>();

	public BackpackDefinition(@Nonnull Color primaryColor) {
		this(primaryColor, new Color(0xffffff));
	}

	public BackpackDefinition(@Nonnull Color primaryColor, @Nonnull Color secondaryColor) {
		this.primaryColor = primaryColor.getRGB();
		this.secondaryColor = secondaryColor.getRGB();
	}

	public Set<Integer> getValidOreIds() {
		return validOreIds;
	}

	public Set<String> getValidItemStacks() {
		return validItemStacks;
	}

	@Override
	public String getName(ItemStack backpack) {
		Item item = backpack.getItem();
		String display = Translator.translateToLocal(item.getUnlocalizedNameInefficiently(backpack) + ".name").trim();

		if (backpack.hasTagCompound() && backpack.getTagCompound().hasKey("display", 10)) {
			NBTTagCompound nbt = backpack.getTagCompound().getCompoundTag("display");

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

	@Override
	public void addValidItem(ItemStack validItem) {
		String itemStackString = ItemStackUtil.getStringForItemStack(validItem);
		if (itemStackString != null) {
			this.validItemStacks.add(itemStackString);
		}
	}

	public void clearAllValid() {
		validItemStacks.clear();
		validOreIds.clear();
	}

	@Override
	public void addValidItems(List<ItemStack> validItems) {
		for (ItemStack validItem : validItems) {
			addValidItem(validItem);
		}
	}

	@Override
	public void addValidOreDictName(String oreDictName) {
		if (OreDictionary.doesOreNameExist(oreDictName)) {
			int oreId = OreDictionary.getOreID(oreDictName);
			validOreIds.add(oreId);
		}
	}

	public void addValidOreDictNames(List<String> oreDictNames) {
		for (String oreDictName : oreDictNames) {
			addValidOreDictName(oreDictName);
		}
	}

	@Override
	public boolean test(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}

		Item item = itemStack.getItem();
		if (item == null) {
			return false;
		}

		String itemStackStringWild = ItemStackUtil.getItemNameFromRegistryAsString(item);
		if (validItemStacks.contains(itemStackStringWild)) {
			return true;
		}

		int meta = itemStack.getItemDamage();
		if (meta != OreDictionary.WILDCARD_VALUE) {
			String itemStackString = itemStackStringWild + ':' + meta;
			if (validItemStacks.contains(itemStackString)) {
				return true;
			}
		}

		int[] oreIds = OreDictionary.getOreIDs(itemStack);
		for (int oreId : oreIds) {
			if (validOreIds.contains(oreId)) {
				validItemStacks.add(itemStackStringWild);
				return true;
			}
		}

		return false;
	}

	public static class BackpackDefinitionNaturalist extends BackpackDefinition {
		@Nonnull
		private final String speciesRootUid;

		public BackpackDefinitionNaturalist(@Nonnull Color primaryColor, @Nonnull String speciesRootUid) {
			super(primaryColor);
			this.speciesRootUid = speciesRootUid;
		}

		@Override
		public boolean test(ItemStack itemStack) {
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(speciesRootUid);
			return speciesRoot.isMember(itemStack);
		}
	}
}
