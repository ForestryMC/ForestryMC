/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2013-2014 Slime Knights (mDiyo, fuj1n, Sunstrike, progwml6, pillbox, alexbegt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Any alternate licenses are noted where appropriate.
 ******************************************************************************/
package forestry.book.data.structure;

import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StructureInfo {
	public IBlockState[][][] data;
	public int blockCount = 0;
	public int[] countPerLevel;
	public int structureHeight = 0;
	public int structureLength = 0;
	public int structureWidth = 0;
	public int showLayer = -1;

	private int blockIndex = 0;
	private int maxBlockIndex;

	public StructureInfo(int length, int height, int width, BlockData[] blockData) {
		this.structureWidth = width;
		this.structureHeight = height;
		this.structureLength = length;
		IBlockState[][][] states = new IBlockState[height][length][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < length; x++) {
				for (int z = 0; z < width; z++) {
					for (BlockData data : blockData) {
						if (inside(x, y, z, data.pos, data.endPos)) {
							states[y][x][z] = convert(data);
							break;
						}
					}
				}
			}
		}

		data = states;
		maxBlockIndex = blockIndex = structureHeight * structureLength * structureWidth;
	}

	private IBlockState convert(BlockData data) {
		Block block = Block.getBlockFromName(data.block);
		if (block == null) {
			return Blocks.AIR.getDefaultState();
		}
		IBlockState state;
		if (data.state == null || data.state.isEmpty()) {
			state = block.getStateFromMeta(data.meta);
		} else {
			state = block.getDefaultState();

			for (Map.Entry<String, String> entry : data.state.entrySet()) {
				Optional<IProperty<?>> property = state.getPropertyKeys().stream().filter(iProperty -> entry.getKey().equals(iProperty.getName())).findFirst();

				if (property.isPresent()) {
					state = setProperty(state, property.get(), entry.getValue());
				}
			}
		}
		return state;
	}

	private <T extends Comparable<T>> IBlockState setProperty(IBlockState state, IProperty<T> prop, String valueString) {
		com.google.common.base.Optional<T> value = prop.parseValue(valueString);
		if (value.isPresent()) {
			state = state.withProperty(prop, value.get());
		}
		return state;
	}


	private boolean inside(int x, int y, int z, int[] rangeStart, int[] rangeEnd) {
		if (x >= rangeStart[0] && x <= rangeEnd[0]) {
			if (y >= rangeStart[1] && y <= rangeEnd[1]) {
				return z >= rangeStart[2] && z <= rangeEnd[2];
			}
		}

		return false;
	}

	public void setShowLayer(int layer) {
		showLayer = layer;
		blockIndex = (layer + 1) * (structureLength * structureWidth) - 1;
	}

	public void reset() {
		blockIndex = maxBlockIndex;
	}

	public boolean canStep() {
		int index = blockIndex;
		do {
			if (++index >= maxBlockIndex) {
				return false;
			}
		} while (isEmpty(index));
		return true;
	}

	public void step() {
		int start = blockIndex;
		do {
			if (++blockIndex >= maxBlockIndex) {
				blockIndex = 0;
			}
		} while (isEmpty(blockIndex) && blockIndex != start);
	}

	private boolean isEmpty(int index) {
		int y = index / (structureLength * structureWidth);
		int r = index % (structureLength * structureWidth);
		int x = r / structureWidth;
		int z = r % structureWidth;

		return data[y][x][z] == null || data[y][x][z].getBlock() == Blocks.AIR;
	}

	public int getLimiter() {
		return blockIndex;
	}
}
