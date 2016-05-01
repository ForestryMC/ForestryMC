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
package forestry.api.greenhouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.common.eventhandler.Event;

import forestry.api.core.EnumCamouflageType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedBlock;

public class GreenhouseEvents extends Event {
	
	@Nullable
	public final IGreenhouseState state;
	
	public GreenhouseEvents(@Nullable IGreenhouseState state) {
		this.state = state;
	}
	
	public static class CamouflageChangeEvent extends GreenhouseEvents{

		@Nullable
		public ICamouflagedBlock camouflagedBlock;
		@Nonnull
		public ICamouflageHandler camouflageHandler;
		@Nonnull
		public final EnumCamouflageType camouflageType;
		
		public CamouflageChangeEvent(IGreenhouseState state, @Nullable ICamouflagedBlock camouflagedBlock, @Nonnull ICamouflageHandler camouflageHandler, @Nonnull EnumCamouflageType camouflageType) {
			super(state);
			
			this.camouflagedBlock = camouflagedBlock;
			this.camouflageHandler = camouflageHandler;
			this.camouflageType = camouflageType;
		}
	}
	
	public static class InternalBlockEvent extends GreenhouseEvents{

		@Nonnull
		public IInternalBlock internalBlock;
		
		public InternalBlockEvent(IGreenhouseState state, @Nonnull IInternalBlock internalBlock) {
			super(state);
			
			this.internalBlock = internalBlock;
		}	
	}
	
	public static class CreateInternalBlockEvent extends InternalBlockEvent{

		public CreateInternalBlockEvent(IGreenhouseState state, IInternalBlock internalBlock) {
			super(state, internalBlock);
		}
	}
	
	public static class CheckInternalBlockFaceEvent extends InternalBlockEvent{

		@Nonnull
		public final IInternalBlockFace face;
		
		public CheckInternalBlockFaceEvent(IGreenhouseState state, IInternalBlock internalBlock, @Nonnull IInternalBlockFace face) {
			super(state, internalBlock);
			
			this.face = face;
		}
	}
	
}
