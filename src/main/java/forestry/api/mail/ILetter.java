/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;

import forestry.api.core.INbtWritable;

public interface ILetter extends Container, INbtWritable {

	NonNullList<ItemStack> getPostage();

	void setProcessed(boolean flag);

	boolean isProcessed();

	boolean isMailable();

	void setSender(IMailAddress address);

	IMailAddress getSender();

	boolean hasRecipient();

	void setRecipient(@Nullable IMailAddress address);

	@Nullable
	IMailAddress getRecipient();

	String getRecipientString();

	void setText(String text);

	String getText();

	void addTooltip(List<Component> list);

	boolean isPostPaid();

	int requiredPostage();

	void invalidatePostage();

	NonNullList<ItemStack> getAttachments();

	void addAttachment(ItemStack itemstack);

	void addAttachments(NonNullList<ItemStack> itemstacks);

	int countAttachments();

	void addStamps(ItemStack stamps);

}
