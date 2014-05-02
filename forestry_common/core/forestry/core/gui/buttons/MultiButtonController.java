/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.buttons;

import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MultiButtonController<T extends IMultiButtonState> {

	private int currentState;
	private final T[] validStates;

	public MultiButtonController(int startState, T... validStates) {
		this.currentState = startState;
		this.validStates = validStates;
	}

	protected MultiButtonController(MultiButtonController<T> controller) {
		this.currentState = controller.currentState;
		this.validStates = controller.validStates;
	}

	public MultiButtonController<T> copy() {
		return new MultiButtonController<T>(this);
	}

	public T[] getValidStates() {
		return validStates;
	}

	public int incrementState() {
		int newState = currentState + 1;
		if (newState >= validStates.length) {
			newState = 0;
		}
		currentState = newState;
		return currentState;
	}

	public int decrementState() {
		int newState = currentState - 1;
		if (newState < 0) {
			newState = validStates.length - 1;
		}
		currentState = newState;
		return currentState;
	}

	public void setCurrentState(int state) {
		currentState = state;
	}

	public void setCurrentState(T state) {
		for (int i = 0; i < validStates.length; i++) {
			if (validStates[i] == state) {
				currentState = i;
				return;
			}
		}
	}

	public int getCurrentState() {
		return currentState;
	}

	public T getButtonState() {
		return validStates[currentState];
	}

	public void writeToNBT(NBTTagCompound nbt, String tag) {
		nbt.setString(tag, getButtonState().name());
	}

	public void readFromNBT(NBTTagCompound nbt, String tag) {
		if (nbt.getTag(tag) instanceof NBTTagString) {
			String name = nbt.getString(tag);
			for (int i = 0; i < validStates.length; i++) {
				if (validStates[i].name().equals(name)) {
					currentState = i;
					break;
				}
			}
		} else if (nbt.getTag(tag) instanceof NBTTagByte) {
			currentState = nbt.getByte(tag);
		}
	}

}
