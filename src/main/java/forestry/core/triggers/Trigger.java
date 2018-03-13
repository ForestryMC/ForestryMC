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

// TODO: buildcraft for 1.9
public abstract class Trigger {//implements ITriggerExternal {

	//	private final String uid;
	//	private final String localization;

	//	protected Trigger(String uid) {
	//		this(uid, uid);
	//	}
	//
	//	protected Trigger(String uid, String localization) {
	//		this.uid = "forestry:" + uid;
	//		this.localization = localization;
	//		StatementManager.registerStatement(this);
	//	}
	//
	//	@Override
	//	public String getUniqueTag() {
	//		return uid;
	//	}
	//
	//	@Override
	//	public String getDescription() {
	//		return Translator.translateToLocal("for.trigger." + localization);
	//	}
	//
	//	@Override
	//	public IStatementParameter createParameter(int index) {
	//		return null;
	//	}
	//
	//	@Override
	//	public int maxParameters() {
	//		return 0;
	//	}
	//
	//	@Override
	//	public int minParameters() {
	//		return 0;
	//	}
	//
	//	@SideOnly(Side.CLIENT)
	//	private TextureAtlasSprite icon;
	//
	//	@SideOnly(Side.CLIENT)
	//	public void registerSprites() {
	//		icon = TextureManager.registerSprite("triggers/" + localization);
	//	}
	//
	//	@Override
	//	public TextureAtlasSprite getGuiSprite() {
	//		return icon;
	//	}
	//
	//	@Override
	//	public IStatement rotateLeft() {
	//		return this;
	//	}
}
