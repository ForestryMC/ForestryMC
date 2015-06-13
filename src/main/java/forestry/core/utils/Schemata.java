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
package forestry.core.utils;

import forestry.core.config.Config;
import forestry.core.vect.Vect;

public class Schemata {
	public enum EnumStructureBlock {
		ANY('X'), FOREIGN('F'), AIR('O'), MASTER('M'), GLASS('G'), BLOCK_A('A'), BLOCK_B('B'), BLOCK_C('C'), BLOCK_D('D'), BLOCK_E('E');

		private final char key;

		EnumStructureBlock(char key) {
			this.key = key;
		}

		public char getKey() {
			return this.key;
		}
	}

	private final String uid;
	private final EnumStructureBlock[][][] structure;
	private final int width;
	private final int height;
	private final int depth;
	private int xOffset, yOffset, zOffset = -1;

	public Schemata(String uid, int width, int height, int depth, String... patterns) {
		this(uid, width, height, depth);
		this.setStructure(patterns);
	}

	public Schemata(String uid, int width, int height, int depth) {
		this.uid = uid;
		this.structure = new EnumStructureBlock[width][height][depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	private void setStructure(String... patterns) {

		String fullpattern = "";

		for (String pattern : patterns) {
			fullpattern = (new StringBuilder()).append(fullpattern).append(pattern).toString();
		}

		if (fullpattern.length() != getWidth() * getHeight() * getDepth()) {
			throw new RuntimeException("Incorrect pattern " + fullpattern + " (" + fullpattern.length() + ") for (" + getWidth() + "/" + getHeight() + "/"
					+ getDepth() + ")");
		}

		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				for (int k = 0; k < getDepth(); k++) {
					for (EnumStructureBlock type : EnumStructureBlock.values()) {
						if (type.getKey() == fullpattern.charAt(i * getHeight() * getDepth() + j * getDepth() + k)) {
							structure[i][j][k] = type;
							break;
						}
					}
				}
			}
		}

		/*
		 * for(int i = 0; i < getWidth(); i++) { for(int j = 0; j < getHeight(); j++) { System.out.print("["); for(int k = 0; k < getDepth(); k++) {
		 * System.out.print(structure[i][j][k].getKey()); } System.out.print("]"); } System.out.println(); }
		 */

	}

	public Schemata setOffsets(int xOffset, int yOffset, int zOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;

		return this;
	}

	public EnumStructureBlock getAt(int x, int y, int z, boolean rotate) {
		if (rotate) {
			return structure[z][y][x];
		}
		return structure[x][y][z];
	}

	public Vect getDimensions(boolean rotate) {
		if (rotate) {
			return new Vect(getDepth(), getHeight(), getWidth());
		} else {
			return new Vect(getWidth(), getHeight(), getDepth());
		}
	}

	public boolean isEnabled() {
		return Config.isStructureEnabled(uid);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @return the xOffset
	 */
	public int getxOffset() {
		return xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public int getyOffset() {
		return yOffset;
	}

	/**
	 * @return the zOffset
	 */
	public int getzOffset() {
		return zOffset;
	}

}
