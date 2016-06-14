/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

/**
 * Needs to be implemented by TileEntities that want to be part of an alveary.
 * The sub-interfaces can be implemented to alter the operation of the alveary.
 * They are automatically detected and handled by the alveary when they join its structure.
 */
public interface IDistillVatComponent<T extends IMultiblockLogicDistillVat> extends IMultiblockComponent {
	/**
	 * @return the multiblock logic for this component
	 */
	@Override
	T getMultiblockLogic();

	/**
	 * Implemented by alveary parts to receive ticks from the completed structure.
	 */
	interface Active extends IDistillVatComponent {
		void updateServer(int tickCount);

		void updateClient(int tickCount);
	}

}
