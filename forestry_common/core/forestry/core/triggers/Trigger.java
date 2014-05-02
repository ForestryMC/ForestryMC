/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.triggers;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITileTrigger;
import buildcraft.api.gates.ITriggerParameter;

import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public abstract class Trigger implements ITileTrigger {

	private final String uid;
	private final String unlocalized;

	public Trigger(String uid) {
		this(uid, uid);
	}

	public Trigger(String uid, String localization) {
		this.uid = "forestry." + uid;
		unlocalized = "trigger." + localization;
		ActionManager.registerTrigger(this);
	}

	@Override
	public String getUniqueTag() {
		return uid;
	}

	@Override
	public String getDescription() {
		return StringUtil.localize(unlocalized);
	}

	@Override
	public boolean hasParameter() {
		return false;
	}

	@Override
	public boolean requiresParameter() {
		return false;
	}

	@Override
	public ITriggerParameter createParameter() {
		return null;
	}
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		icon = TextureManager.getInstance().registerTex(register, "triggers/" + unlocalized.replace("trigger.", ""));
	}
}
