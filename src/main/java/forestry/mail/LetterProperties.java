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
package forestry.mail;

import java.util.List;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.mail.ILetter;
import forestry.plugins.PluginMail;

public class LetterProperties {
	private enum State {
		FRESH, STAMPED, OPENED, EMPTIED
	}

	private enum Size {
		EMPTY, SMALL, BIG
	}

	public static ItemStack createStampedLetterStack(ILetter letter) {
		Size size = getSize(letter);
		int meta = encodeMeta(State.STAMPED, size);
		return new ItemStack(PluginMail.items.letters, 1, meta);
	}

	public static void closeLetter(ItemStack parent, ILetter letter) {
		State state = getState(parent.getItemDamage());
		Size size = getSize(parent.getItemDamage());

		switch (state) {
			case OPENED:
				if (letter.countAttachments() <= 0) {
					state = State.EMPTIED;
				}
				break;
			case FRESH:
			case STAMPED:
				if (letter.isMailable() && letter.isPostPaid()) {
					state = State.STAMPED;
				} else {
					state = State.FRESH;
				}
				size = getSize(letter);
				break;
			case EMPTIED:
		}

		int meta = encodeMeta(state, size);
		parent.setItemDamage(meta);

		letter.writeToNBT(parent.getTagCompound());
	}

	public static void openLetter(ItemStack parent) {
		int oldMeta = parent.getItemDamage();
		State state = getState(oldMeta);
		if (state == State.FRESH || state == State.STAMPED) {
			Size size = getSize(oldMeta);
			int newMeta = encodeMeta(State.OPENED, size);
			parent.setItemDamage(newMeta);
		}
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	private static ModelResourceLocation[][] models;

	@SideOnly(Side.CLIENT)
	private static class LetterMeshDefinition implements ItemMeshDefinition {
		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			int damage = stack.getItemDamage();
			State state = getState(damage);
			Size size = getSize(damage);
			return models[size.ordinal()][state.ordinal()];
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item, IModelManager manager) {
		models = new ModelResourceLocation[3][4];
		for (int i = 0; i < 3; i++) {
			models[i][0] = new ModelResourceLocation("forestry:mail/letter." + i + ".fresh", "inventory");
			manager.registerVariant(item, "forestry:mail/letter." + i + ".fresh");
			models[i][1] = new ModelResourceLocation("forestry:mail/letter." + i + ".stamped", "inventory");
			manager.registerVariant(item, "forestry:mail/letter." + i + ".stamped");
			models[i][2] = new ModelResourceLocation("forestry:mail/letter." + i + ".opened", "inventory");
			manager.registerVariant(item, "forestry:mail/letter." + i + ".opened");
			models[i][3] = new ModelResourceLocation("forestry:mail/letter." + i + ".emptied", "inventory");
			manager.registerVariant(item, "forestry:mail/letter." + i + ".emptied");
		}
		manager.registerItemModel(item, new LetterMeshDefinition());
	}

	@SuppressWarnings("unchecked")
	public static void getSubItems(Item item, CreativeTabs tab, List list) {
		for (State state : State.values()) {
			for (Size size : Size.values()) {
				int meta = encodeMeta(state, size);
				ItemStack letter = new ItemStack(item, 1, meta);
				list.add(letter);
			}
		}
	}

	private static State getState(int meta) {
		int ordinal = meta & 0x0f;
		State[] values = State.values();
		if (ordinal >= values.length) {
			ordinal = 0;
		}
		return values[ordinal];
	}

	private static Size getSize(int meta) {
		int ordinal = meta >> 4;
		Size[] values = Size.values();
		if (ordinal >= values.length) {
			ordinal = 0;
		}
		return values[ordinal];
	}

	private static int encodeMeta(State state, Size size) {
		int meta = size.ordinal() << 4;
		meta |= state.ordinal();
		return meta;
	}

	private static Size getSize(ILetter letter) {
		int count = letter.countAttachments();

		if (count > 5) {
			return Size.BIG;
		} else if (count > 1) {
			return Size.SMALL;
		} else {
			return Size.EMPTY;
		}
	}
}
