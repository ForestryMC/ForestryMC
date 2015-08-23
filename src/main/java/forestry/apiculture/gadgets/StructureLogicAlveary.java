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
package forestry.apiculture.gadgets;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.core.ITileStructure;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.BlockStructure.EnumStructureState;
import forestry.core.inventory.InvTools;
import forestry.core.gadgets.StructureLogic;
import forestry.core.utils.Schemata;
import forestry.core.utils.Schemata.EnumStructureBlock;
import forestry.core.vect.Vect;

public class StructureLogicAlveary extends StructureLogic {

	/* CONSTANTS */
	public static final String UID_ALVEARY = "alveary";
	public static final Schemata SCHEMATA_ALVEARY = new Schemata("alveary3x3", 5, 6, 5, "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FAAAF",
			"FAAAF", "FABAF", "FCCCF", "FFFFF", "FFFFF", "FAAAF", "FAAAF", "FBMBF", "FCCCF", "FFFFF", "FFFFF", "FAAAF", "FAAAF", "FABAF", "FCCCF", "FFFFF",
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF").setOffsets(-2, -3, -2);

	public static final HashSet<Block> slabBlocks = new HashSet<Block>();

	static {
		slabBlocks.add(Blocks.wooden_slab);
		slabBlocks.add(ForestryBlock.slabs1.block());
		slabBlocks.add(ForestryBlock.slabs2.block());
		slabBlocks.add(ForestryBlock.slabs3.block());
		slabBlocks.add(ForestryBlock.slabs4.block());
	}

	public StructureLogicAlveary(ITileStructure structure) {
		super(UID_ALVEARY, structure);
		schematas = new Schemata[]{SCHEMATA_ALVEARY};
		metaOnValid.put(EnumStructureBlock.BLOCK_B, 1);
	}

	@Override
	protected EnumStructureState determineMasterState(Schemata schemata, boolean rotate) {

		Vect dimensions = schemata.getDimensions(rotate);
		int offsetX = schemata.getxOffset();
		int offsetZ = schemata.getzOffset();
		if (rotate) {
			offsetX = schemata.getzOffset();
			offsetZ = schemata.getxOffset();
		}

		for (int i = 0; i < dimensions.x; i++) {
			for (int j = 0; j < schemata.getHeight(); j++) {
				for (int k = 0; k < dimensions.z; k++) {
					int x = structureTile.getPos().getX() + i + offsetX;
					int y = structureTile.getPos().getY() + j + schemata.getyOffset();
					int z = structureTile.getPos().getZ() + k + offsetZ;

					if (!InvTools.blockExists(x, y, z, structureTile.getWorld())) {
						return EnumStructureState.INDETERMINATE;
					}

					EnumStructureBlock required = schemata.getAt(i, j, k, rotate);
					if (required == EnumStructureBlock.ANY) {
						continue;
					}
					
					BlockPos pos = new BlockPos(x, y, z);
					TileEntity tile = structureTile.getWorld().getTileEntity(pos);
					IBlockState state = structureTile.getWorld().getBlockState(pos);
					Block block = state.getBlock();

					switch (required) {
						case AIR:
							if (!block.isAir(structureTile.getWorld(), pos)) {
								return EnumStructureState.INVALID;
							}
							break;
						case BLOCK_A:
							if (tile == null || !(tile instanceof IAlvearyComponent)) {
								return EnumStructureState.INVALID;
							}
							if (!((ITileStructure) tile).getTypeUID().equals(UID_ALVEARY)) {
								return EnumStructureState.INVALID;
							}
							break;
						case MASTER:
						case BLOCK_B:
							if (tile == null || !(tile instanceof TileAlvearyPlain)) {
								return EnumStructureState.INVALID;
							}
							break;
						case BLOCK_C:
							if (!slabBlocks.contains(block)) {
								return EnumStructureState.INVALID;
							}
							if ((block.getMetaFromState(state) & 8) != 0) {
								return EnumStructureState.INVALID;
							}
							break;
						case BLOCK_D:
							if (block != Blocks.spruce_stairs) {
								return EnumStructureState.INVALID;
							}
							break;
						case FOREIGN:
							if (tile instanceof ITileStructure) {
								return EnumStructureState.INVALID;
							}
							break;
						default:
							return EnumStructureState.INDETERMINATE;
					}
				}
			}
		}

		return EnumStructureState.VALID;
	}

}
