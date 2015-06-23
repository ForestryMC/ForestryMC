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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;

import org.junit.Assert;
import org.junit.Test;

public class NetworkTest {
	@Test
	public void testVarInt() {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStreamForestry dataOutput = new DataOutputStreamForestry(bytesOut);

		final int oneByteThreshold = (int) Math.pow(2, 7);
		final int twoByteThreshold = (int) Math.pow(2, 14);
		final int threeByteThreshold = (int) Math.pow(2, 21);
		final int fourByteThreshold = (int)  Math.pow(2, 28);

		try {
			int prevSize = 0;
			for (int i = 0; i < Integer.MAX_VALUE; i = (i * 2) + 1) {
				dataOutput.writeVarInt(i);

				int sizeGained = dataOutput.size() - prevSize;
				prevSize = dataOutput.size();
				if (i < oneByteThreshold) {
					Assert.assertEquals(1, sizeGained);
				} else if (i < twoByteThreshold) {
					Assert.assertEquals(2, sizeGained);
				} else if (i < threeByteThreshold) {
					Assert.assertEquals(3, sizeGained);
				} else if (i < fourByteThreshold) {
					Assert.assertEquals(4, sizeGained);
				} else {
					Assert.assertEquals(5, sizeGained);
				}
			}
		} catch (IOException e) {
			Assert.fail("Writing var int threw an exception.");
		}

		ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
		DataInputStreamForestry dataInput = new DataInputStreamForestry(bytesIn);

		try {
			for (int i = 0; i < Integer.MAX_VALUE; i = (i * 2) + 1) {
				int varInt = dataInput.readVarInt();
				Assert.assertEquals("Var int didn't read back correctly", i, varInt);
			}
		} catch (IOException e) {
			Assert.fail("Reading var int threw an exception.");
		}
	}
}
