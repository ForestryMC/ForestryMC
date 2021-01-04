///*
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// */
//package forestry.mail.triggers;
//
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//
//import forestry.core.triggers.Trigger;
//import forestry.mail.tiles.TileTrader;
//
////import buildcraft.api.statements.IStatementContainer;
////import buildcraft.api.statements.IStatementParameter;
//
//public class TriggerLowStamps extends Trigger {
//
//	private final int threshold;
//
//	public TriggerLowStamps(String tag, int threshold) {
//		super(tag, "lowStamps", "low_stamps");
//		this.threshold = threshold;
//	}
//
//	@Override
//	public String getDescription() {
//		return super.getDescription() + " < " + threshold + "p";
//	}
//
//	@Override
//	public boolean isTriggerActive(TileEntity tile, Direction side, IStatementContainer source, IStatementParameter[] parameters) {
//
//		if (!(tile instanceof TileTrader)) {
//			return false;
//		}
//
//		return !((TileTrader) tile).hasPostageMin(threshold);
//	}
//
//}
