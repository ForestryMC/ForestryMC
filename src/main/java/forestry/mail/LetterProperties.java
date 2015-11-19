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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.mail.ILetter;
import forestry.core.render.TextureManager;
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

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private static IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	public static void registerIcons(IIconRegister register) {
		icons = new IIcon[3][4];
		for (int i = 0; i < 3; i++) {
			icons[i][0] = TextureManager.registerTex(register, "mail/letter." + i + ".fresh");
			icons[i][1] = TextureManager.registerTex(register, "mail/letter." + i + ".stamped");
			icons[i][2] = TextureManager.registerTex(register, "mail/letter." + i + ".opened");
			icons[i][3] = TextureManager.registerTex(register, "mail/letter." + i + ".emptied");
		}
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

	@SideOnly(Side.CLIENT)
	public static IIcon getIconFromDamage(int damage) {
		State state = getState(damage);
		Size size = getSize(damage);

		return icons[size.ordinal()][state.ordinal()];
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
