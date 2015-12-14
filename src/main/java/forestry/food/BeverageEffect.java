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
package forestry.food;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.utils.StringUtil;

public abstract class BeverageEffect implements IBeverageEffect {

	public static final IBeverageEffect weakAlcoholic = new BeverageEffectDrunk(10, 0.2f);
	public static final IBeverageEffect weakAntidote = new BeverageEffectAntidote(20, 0.5f);
	public static final IBeverageEffect strongAntidote = new BeverageEffectAntidote(21, 1.0f);

	private final int id;
	protected String description;

	protected BeverageEffect(int id) {
		this.id = id;

		if (BeverageManager.effectList[id] != null) {
			throw new RuntimeException("Beverage effect slot " + id + " was already occupied by " + BeverageManager.effectList[id].toString()
					+ " when trying to add " + this.toString());
		} else {
			BeverageManager.effectList[id] = this;
		}
	}

	public int getId() {
		return this.id;
	}

	protected String getLevel() {
		return null;
	}

	@Override
	public String getDescription() {
		if (getLevel() != null) {
			return StringUtil.localize(description) + " " + getLevel();
		} else {
			return StringUtil.localize(description);
		}
	}

	public static List<IBeverageEffect> loadEffects(ItemStack stack) {
		List<IBeverageEffect> effectsList = new ArrayList<>();

		NBTTagCompound nbttagcompound = stack.getTagCompound();
		if (nbttagcompound == null) {
			return effectsList;
		}

		if (nbttagcompound.hasKey("E")) {
			int effectLength = nbttagcompound.getInteger("L");
			NBTTagList nbttaglist = nbttagcompound.getTagList("E", 10);
			IBeverageEffect[] effects = new IBeverageEffect[effectLength];
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound1.getByte("S");
				if (byte0 >= 0 && byte0 < effects.length) {
					effects[byte0] = BeverageManager.effectList[nbttagcompound1.getInteger("ID")];
				}
			}
			effectsList = Arrays.asList(effects);
		}

		return effectsList;
	}

	public static void saveEffects(ItemStack stack, List<IBeverageEffect> effects) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		NBTTagList nbttaglist = new NBTTagList();
		nbttagcompound.setInteger("L", effects.size());
		for (int i = 0; i < effects.size(); i++) {
			IBeverageEffect effect = effects.get(i);
			if (effect != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("S", (byte) i);
				nbttagcompound1.setInteger("ID", effect.getId());
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("E", nbttaglist);

		stack.setTagCompound(nbttagcompound);
	}
}
