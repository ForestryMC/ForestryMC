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
package forestry.apiculture.multiblock;

import net.minecraft.util.StatCollector;

import forestry.apiculture.gadgets.BlockAlveary;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.rectangular.RectangularMultiblockTileEntityBase;

public abstract class TileAlveary extends RectangularMultiblockTileEntityBase {

	public static final int PLAIN_META = 0;
	public static final int ENTRANCE_META = 1;
	public static final int SWARMER_META = 2;
	public static final int FAN_META = 3;
	public static final int HEATER_META = 4;
	public static final int HYGRO_META = 5;
	public static final int STABILIZER_META = 6;
	public static final int SIEVE_META = 7;

	/* TEXTURES */
	public int getIcon(int side, int metadata) {
		return BlockAlveary.PLAIN;
	}

	@Override
	public void isGoodForExteriorLevel(int level) throws MultiblockValidationException {
		if (level == 2 && !(this instanceof TileAlvearyPlain)) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.alveary.error.needPlainOnTop"));
		}
	}

	@Override
	public void isGoodForInterior() throws MultiblockValidationException {
		if (!(this instanceof TileAlvearyPlain)) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.alveary.error.needPlainInterior"));
		}
	}

	@Override
	public void onMachineActivated() {

	}

	@Override
	public void onMachineDeactivated() {

	}

	@Override
	public MultiblockControllerBase createNewMultiblock() {
		return new AlvearyController(worldObj);
	}

	@Override
	public Class<? extends MultiblockControllerBase> getMultiblockControllerType() {
		return AlvearyController.class;
	}

	public IAlvearyController getAlvearyController() {
		if (isConnected()) {
			return (IAlvearyController) getMultiblockController();
		} else {
			return FakeAlvearyController.instance;
		}
	}

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);

		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

}
