/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.IPostalState;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginMail;

public class PostalCarrier implements IPostalCarrier {

	private final String uid;
	private final String iconID;
	private final EnumAddressee type;

	public PostalCarrier(EnumAddressee type) {
		uid = type.toString().toLowerCase(Locale.ENGLISH);
		iconID = "mail/carrier." + uid;
		this.type = type;
	}

	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public String getName() {
		return StringUtil.localize("gui.addressee." + uid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return TextureManager.getInstance().getDefault(iconID);
	}

	@Override
	public IPostalState deliverLetter(World world, IPostOffice office, GameProfile recipient, ItemStack letterstack, boolean doDeliver) {
		if(type == EnumAddressee.TRADER)
			return handleTradeLetter(world, office, recipient, letterstack, doDeliver);
		else
			return storeInPOBox(world, office, recipient, letterstack, doDeliver);
	}

	private IPostalState handleTradeLetter(World world, IPostOffice office, GameProfile recipient, ItemStack letterstack, boolean doLodge) {
		IPostalState state = EnumDeliveryState.NOT_MAILABLE;

		ITradeStation trade = PostManager.postRegistry.getTradeStation(world, recipient);
		if (trade == null)
			return EnumDeliveryState.NO_MAILBOX;

		state = trade.handleLetter(world, recipient, letterstack, doLodge);

		return state;
	}

	private EnumDeliveryState storeInPOBox(World world, IPostOffice office, GameProfile recipient, ItemStack letterstack, boolean doLodge) {

		POBox pobox = PostRegistry.getPOBox(world, recipient);
		if (pobox == null)
			return EnumDeliveryState.NO_MAILBOX;

		if (!pobox.storeLetter(letterstack.copy()))
			return EnumDeliveryState.MAILBOX_FULL;
		else
			PluginMail.proxy.setPOBoxInfo(world, recipient, pobox.getPOBoxInfo());

		return EnumDeliveryState.OK;
	}

}
