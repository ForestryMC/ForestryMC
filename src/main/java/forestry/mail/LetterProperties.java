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

import forestry.api.mail.ILetter;
import forestry.mail.features.MailItems;
import forestry.mail.items.ItemLetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LetterProperties {

    public static ItemStack createStampedLetterStack(ILetter letter) {
        ItemLetter.Size size = getSize(letter);
        return MailItems.LETTERS.stack(size, ItemLetter.State.STAMPED, 1);
    }

    public static ItemStack closeLetter(ItemStack parent, ILetter letter) {
        Item item = parent.getItem();
        if (!(item instanceof ItemLetter)) {    //TODO can just do == check?
            return parent;
        }
        ItemLetter itemLetter = (ItemLetter) item;
        ItemLetter.State state = itemLetter.getState();
        ItemLetter.Size size = itemLetter.getSize();

        switch (state) {
            case OPENED:
                if (letter.countAttachments() <= 0) {
                    state = ItemLetter.State.EMPTIED;
                }
                break;
            case FRESH:
            case STAMPED:
                if (letter.isMailable() && letter.isPostPaid()) {
                    state = ItemLetter.State.STAMPED;
                } else {
                    state = ItemLetter.State.FRESH;
                }
                size = getSize(letter);
                break;
            case EMPTIED:
        }
        ItemStack ret = MailItems.LETTERS.stack(size, state, parent.getCount());
        ret.setTag(parent.getTag());
        letter.write(parent.getTag());
        return ret;
    }

    public static ItemStack openLetter(ItemStack parent) {
        Item item = parent.getItem();
        if (!(item instanceof ItemLetter)) {    //TODO can just do == check?
            return parent;
        }
        ItemLetter itemLetter = (ItemLetter) item;

        ItemLetter.State state = itemLetter.getState();
        if (state == ItemLetter.State.FRESH || state == ItemLetter.State.STAMPED) {
            ItemLetter.Size size = itemLetter.getSize();
            ItemStack ret = MailItems.LETTERS.stack(size, state, parent.getCount());
            ret.setTag(parent.getTag());
            return ret;
        } else {
            return parent;
        }
    }

    private static ItemLetter.Size getSize(ILetter letter) {
        int count = letter.countAttachments();

        if (count > 5) {
            return ItemLetter.Size.BIG;
        } else if (count > 1) {
            return ItemLetter.Size.SMALL;
        } else {
            return ItemLetter.Size.EMPTY;
        }
    }
}
