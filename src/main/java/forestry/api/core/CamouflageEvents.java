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
package forestry.api.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.common.eventhandler.Event;

public class CamouflageEvents extends Event {

	@Nullable
	public ICamouflagedBlock camouflagedBlock;
	@Nonnull
	public ICamouflageHandler camouflageHandler;
	@Nonnull
	public final EnumCamouflageType camouflageType;
	
	private CamouflageEvents(ICamouflagedBlock camouflagedBlock, ICamouflageHandler camouflageHandler, EnumCamouflageType camouflageType) {
		this.camouflagedBlock = camouflagedBlock;
		this.camouflageHandler = camouflageHandler;
		this.camouflageType = camouflageType;
	}
	
	public static class CamouflageChangeEvent extends CamouflageEvents{

		public CamouflageChangeEvent(ICamouflagedBlock camouflagedBlock, ICamouflageHandler camouflageHandler, EnumCamouflageType camouflageType) {
			super(camouflagedBlock, camouflageHandler, camouflageType);
		}
		
	}
	
}
