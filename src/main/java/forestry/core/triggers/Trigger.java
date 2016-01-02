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
package forestry.core.triggers;

import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.api.core.SheetIcon;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.StatementManager;

public abstract class Trigger implements ITriggerExternal {

	private final String uid;
	private final String unlocalized;

	protected Trigger(String uid) {
		this(uid, uid);
	}

	protected Trigger(String uid, String localization) {
		this.uid = "forestry:" + uid;
		unlocalized = "trigger." + localization;
		StatementManager.registerStatement(this);
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
	public IStatementParameter createParameter(int index) {
		return null;
	}

	@Override
	public int maxParameters() {
		return 0;
	}

	@Override
	public int minParameters() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite icon;
	private ResourceLocation location;

	@Override
	public SheetIcon getIcon() {
		return new SheetIcon(location, icon.getOriginX(), icon.getOriginY());
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons() {
		icon = TextureManager.registerSprite("triggers/" + unlocalized.replace("trigger.", ""));
		location = new ResourceLocation("forestry", "triggers/" + unlocalized.replace("trigger.", ""));
	}

	@Override
	public IStatement rotateLeft() {
		return this;
	}
}
