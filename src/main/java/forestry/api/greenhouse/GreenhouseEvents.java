/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.common.eventhandler.Event;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.multiblock.IGreenhouseController;

public class GreenhouseEvents extends Event {
	
	@Nullable
	public final IGreenhouseController controller;
	
	public GreenhouseEvents(@Nullable IGreenhouseController state) {
		this.controller = state;
	}
	
	public static class CamouflageChangeEvent extends GreenhouseEvents{

		@Nullable
		public ICamouflagedTile camouflagedBlock;
		@Nonnull
		public ICamouflageHandler camouflageHandler;
		@Nonnull
		public final String camouflageType;
		
		public CamouflageChangeEvent(IGreenhouseController controller, @Nullable ICamouflagedTile camouflagedBlock, @Nonnull ICamouflageHandler camouflageHandler, @Nonnull String camouflageType) {
			super(controller);
			
			this.camouflagedBlock = camouflagedBlock;
			this.camouflageHandler = camouflageHandler;
			this.camouflageType = camouflageType;
		}
	}
	
	public static class InternalBlockEvent extends GreenhouseEvents{

		@Nonnull
		public IInternalBlock internalBlock;
		
		public InternalBlockEvent(IGreenhouseController controller, @Nonnull IInternalBlock internalBlock) {
			super(controller);
			
			this.internalBlock = internalBlock;
		}	
	}
	
	public static class CreateInternalBlockEvent extends InternalBlockEvent{

		public CreateInternalBlockEvent(IGreenhouseController controller, IInternalBlock internalBlock) {
			super(controller, internalBlock);
		}
	}
	
	public static class CheckInternalBlockFaceEvent extends InternalBlockEvent{

		@Nonnull
		public final IInternalBlockFace face;
		
		public CheckInternalBlockFaceEvent(IGreenhouseController controller, IInternalBlock internalBlock, @Nonnull IInternalBlockFace face) {
			super(controller, internalBlock);
			
			this.face = face;
		}
	}
	
}
