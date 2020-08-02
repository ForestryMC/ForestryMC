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
package forestry.mail.items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemSubtype;
import forestry.api.mail.ILetter;
import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.mail.Letter;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.inventory.ItemInventoryLetter;

public class ItemLetter extends ItemWithGui {

    public enum State implements IItemSubtype {
        FRESH, STAMPED, OPENED, EMPTIED;

        @Override
        public String getString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public enum Size implements IItemSubtype {
        EMPTY, SMALL, BIG;

        @Override
        public String getString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    private final Size size;
    private final State state;

    public ItemLetter(Size size, State state) {
        super((new Item.Properties())
                .group(ItemGroupForestry.tabForestry)
                .maxStackSize(64));
        this.size = size;
        this.state = state;
    }

    public Size getSize() {
        return size;
    }

    public State getState() {
        return state;
    }

    @Override
    public String getTranslationKey() {
        return "item.forestry.letter";
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (heldItem.getCount() == 1) {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        } else {
            playerIn.sendMessage(new TranslationTextComponent("for.chat.mail.wrongstacksize"), Util.DUMMY_UUID);
            return ActionResult.resultFail(heldItem);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemstack, world, list, flag);

        CompoundNBT compoundNBT = itemstack.getTag();
        if (compoundNBT == null) {
            list.add(new StringTextComponent("<")
                    .append(new TranslationTextComponent("for.gui.blank").appendString(">"))
                    .mergeStyle(TextFormatting.GRAY));
            return;
        }

        ILetter letter = new Letter(compoundNBT);
        letter.addTooltip(list);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (isInGroup(group) && state == State.FRESH && size == Size.EMPTY) {
            stacks.add(new ItemStack(this));
        }
    }

    @Nullable
    @Override
    public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
        return new ContainerLetter(windowId, player, new ItemInventoryLetter(player, heldItem));
    }
}
