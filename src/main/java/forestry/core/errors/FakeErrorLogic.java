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
package forestry.core.errors;

import com.google.common.collect.ImmutableSet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;

public class FakeErrorLogic implements IErrorLogic {

	public static final FakeErrorLogic instance = new FakeErrorLogic();

	private FakeErrorLogic() {

	}

	@Override
	public boolean setCondition(boolean condition, IErrorState errorState) {
		return false;
	}

	@Override
	public boolean contains(IErrorState state) {
		return false;
	}

	@Override
	public boolean hasErrors() {
		return true;
	}

	@Override
	public void clearErrors() {

	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

	}

	@Override
	public void readData(DataInputStream data) throws IOException {

	}

	@Override
	public ImmutableSet<IErrorState> getErrorStates() {
		return ImmutableSet.of();
	}
}
