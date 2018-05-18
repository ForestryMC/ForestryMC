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

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import buildcraft.api.core.render.ISprite;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.lib.client.sprite.SpriteRaw;
import forestry.Forestry;
import forestry.core.config.Constants;
import forestry.core.utils.Translator;

public abstract class Trigger implements ITriggerExternal {

	private final String uid;
	private final String localization;
	private final String textureName;

	protected Trigger(String uid) {
		this(uid, uid);
	}

	protected Trigger(String uid, String localization) {
		this.uid = "forestry:" + uid;
		this.localization = localization;
		this.textureName = localization.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
		StatementManager.registerStatement(this);
	}

	@Override
	public String getUniqueTag() {
		return uid;
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocal("for.trigger." + localization);
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
	@Nullable
	private ISprite icon;

	@Nullable
	@Override
	public ISprite getSprite() {
		if (icon == null) {
			icon = new SpriteRaw(new ResourceLocation(Constants.MOD_ID, String.format("textures/gui/triggers/%s.png", textureName)), 0, 0, 16, 16, 16);
		}
		return icon;
	}

	@Override
	public IStatement rotateLeft() {
		return this;
	}

	@Override
	public IStatement[] getPossible() {
		return new IStatement[0];
	}
}
