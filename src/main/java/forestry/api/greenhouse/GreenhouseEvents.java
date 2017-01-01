/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import forestry.api.multiblock.IGreenhouseController;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GreenhouseEvents extends Event {

	public final IGreenhouseController controller;

	public GreenhouseEvents(IGreenhouseController state) {
		this.controller = state;
	}

	public static class InternalBlockEvent extends GreenhouseEvents {
		public final IInternalBlock internalBlock;

		public InternalBlockEvent(IGreenhouseController controller, IInternalBlock internalBlock) {
			super(controller);

			this.internalBlock = internalBlock;
		}
	}

	public static class CreateInternalBlockEvent extends InternalBlockEvent {
		public CreateInternalBlockEvent(IGreenhouseController controller, IInternalBlock internalBlock) {
			super(controller, internalBlock);
		}
	}

	public static class CheckInternalBlockFaceEvent extends InternalBlockEvent {
		public final IInternalBlockFace face;

		public CheckInternalBlockFaceEvent(IGreenhouseController controller, IInternalBlock internalBlock, IInternalBlockFace face) {
			super(controller, internalBlock);

			this.face = face;
		}
	}

}
