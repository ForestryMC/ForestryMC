/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gadgets;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.core.config.Defaults;
import forestry.core.gadgets.BlockStructure.EnumStructureState;
import forestry.core.utils.Schemata;
import forestry.core.utils.Schemata.EnumStructureBlock;
import forestry.core.utils.Vect;

public abstract class StructureLogic implements IStructureLogic {

	protected ITileStructure structure;
	protected TileEntity structureTile;

	protected String uid;
	protected Schemata[] schematas;
	protected short activeSchemata = -1;
	protected boolean isRotated = false;
	protected HashMap<EnumStructureBlock, Integer> metaOnValid = new HashMap<EnumStructureBlock, Integer>();

	public StructureLogic(String uid, ITileStructure structure) {
		this.uid = uid;
		this.structure = structure;
		this.structureTile = (TileEntity) structure;
	}

	@Override
	public String getTypeUID() {
		return uid;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("SchemataOrdinal"))
			activeSchemata = nbttagcompound.getShort("SchemataOrdinal");
		isRotated = nbttagcompound.getBoolean("Rotated");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (activeSchemata >= 0)
			nbttagcompound.setShort("SchemataOrdinal", activeSchemata);
		nbttagcompound.setBoolean("Rotated", isRotated);
	}

	@Override
	public void validateStructure() {

		ITileStructure master = structure.getCentralTE();
		if (!structure.isMaster() && master != null) {
			master.validateStructure();
			return;
		}

		EnumStructureState state = EnumStructureState.INDETERMINATE;

		boolean rotate = false;
		for (int i = 0; i < schematas.length; i++) {
			if (!schematas[i].isEnabled())
				continue;

			state = determineMasterState(schematas[i], false);
			rotate = false;
			if (state == EnumStructureState.INVALID && schematas[i].getWidth() != schematas[i].getDepth()) {
				state = determineMasterState(schematas[i], true);
				rotate = true;
			}

			if (state == EnumStructureState.VALID) {
				activeSchemata = (short) i;
				isRotated = rotate;
				break;
			}

		}

		// Structure state is indeterminate, possibly caused by chunkloading. Remain calm, do nothing.
		if (state == EnumStructureState.INDETERMINATE)
			return;

		if (state == EnumStructureState.VALID) {
			// Structure is valid and this block is master, set all other blocks
			// System.out.println(String.format("Structure is valid at %s/%s/%s", ((TileEntity)structure).xCoord, ((TileEntity)structure).yCoord,
			// ((TileEntity)structure).zCoord));
			if (!structure.isMaster()) {
				structure.makeMaster();
				markStructureBlocks(schematas[activeSchemata]);
			}

			/*
			 * int offsetX = schematas[activeSchemata].getxOffset(); int offsetZ = schematas[activeSchemata].getzOffset(); if(isRotated) { offsetX =
			 * schematas[activeSchemata].getzOffset(); offsetZ = schematas[activeSchemata].getxOffset();
			 * 
			 * }
			 */
			// System.out.println(String.format("Offsets: %s and %s.", offsetX, offsetZ));

		} else if (structure.isMaster())
			// Structure is invalid, break it up.
			resetStructureBlocks(schematas[activeSchemata]);
	}

	protected void resetStructureBlocks(Schemata schemata) {

		Vect dimensions = schemata.getDimensions(isRotated);
		int offsetX = schemata.getxOffset();
		int offsetZ = schemata.getzOffset();
		if (isRotated) {
			offsetX = schemata.getzOffset();
			offsetZ = schemata.getxOffset();
		}

		for (int i = 0; i < dimensions.x; i++)
			for (int j = 0; j < schemata.getHeight(); j++)
				for (int k = 0; k < dimensions.z; k++) {
					int x = structureTile.xCoord + i + offsetX;
					int y = structureTile.yCoord + j + schemata.getyOffset();
					int z = structureTile.zCoord + k + offsetZ;

					TileEntity tile = structureTile.getWorldObj().getTileEntity(x, y, z);
					if (!(tile instanceof ITileStructure))
						continue;

					ITileStructure part = (ITileStructure) tile;
					if (!part.getTypeUID().equals(getTypeUID()))
						continue;

					part.onStructureReset();
				}
	}

	protected void markStructureBlocks(Schemata schemata) {

		Vect dimensions = schemata.getDimensions(isRotated);
		int offsetX = schemata.getxOffset();
		int offsetZ = schemata.getzOffset();
		if (isRotated) {
			offsetX = schemata.getzOffset();
			offsetZ = schemata.getxOffset();
		}

		for (int i = 0; i < dimensions.x; i++)
			for (int j = 0; j < schemata.getHeight(); j++)
				for (int k = 0; k < dimensions.z; k++) {
					int x = structureTile.xCoord + i + offsetX;
					int y = structureTile.yCoord + j + schemata.getyOffset();
					int z = structureTile.zCoord + k + offsetZ;

					TileEntity tile = structureTile.getWorldObj().getTileEntity(x, y, z);
					if (!(tile instanceof ITileStructure))
						continue;

					ITileStructure part = (ITileStructure) tile;
					if (!part.getTypeUID().equals(getTypeUID()))
						continue;

					part.setCentralTE((TileEntity) structure);
					EnumStructureBlock type = schemata.getAt(i, j, k, isRotated);
					if (metaOnValid.containsKey(type)) {
						structureTile.getWorldObj().setBlockMetadataWithNotify(x, y, z, metaOnValid.get(type), Defaults.FLAG_BLOCK_SYNCH);
						structureTile.getWorldObj().markBlockForUpdate(x, y, z);
					}
				}
	}

	protected abstract EnumStructureState determineMasterState(Schemata schemata, boolean rotate);

}
