/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.mail;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.api.core.INBTTagable;

public interface ILetter extends IInventory, INBTTagable {

	ItemStack[] getPostage();

	void setProcessed(boolean flag);

	boolean isProcessed();

	boolean isMailable();

	void setSender(MailAddress address);

	MailAddress getSender();

	boolean hasRecipient();

	void setRecipient(MailAddress address);

	MailAddress[] getRecipients();

	String getRecipientString();

	void setText(String text);

	String getText();

	@SuppressWarnings("rawtypes")
	void addTooltip(List list);

	boolean isPostPaid();

	int requiredPostage();

	void invalidatePostage();

	ItemStack[] getAttachments();

	void addAttachment(ItemStack itemstack);

	void addAttachments(ItemStack[] itemstacks);

	int countAttachments();

	void addStamps(ItemStack stamps);

}
