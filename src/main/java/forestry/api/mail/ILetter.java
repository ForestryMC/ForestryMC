/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.mail;

import forestry.api.core.INbtWritable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;

public interface ILetter extends IInventory, INbtWritable {

    NonNullList<ItemStack> getPostage();

    boolean isProcessed();

    void setProcessed(boolean flag);

    boolean isMailable();

    IMailAddress getSender();

    void setSender(IMailAddress address);

    boolean hasRecipient();

    @Nullable
    IMailAddress getRecipient();

    void setRecipient(@Nullable IMailAddress address);

    String getRecipientString();

    String getText();

    void setText(String text);

    void addTooltip(List<ITextComponent> list);

    boolean isPostPaid();

    int requiredPostage();

    void invalidatePostage();

    NonNullList<ItemStack> getAttachments();

    void addAttachment(ItemStack itemstack);

    void addAttachments(NonNullList<ItemStack> itemstacks);

    int countAttachments();

    void addStamps(ItemStack stamps);

}
