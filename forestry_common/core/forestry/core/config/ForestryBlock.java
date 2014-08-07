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
package forestry.core.config;

import net.minecraft.block.Block;

import forestry.core.gadgets.BlockBase;

/**
 * Allows direct access to Forestry's blocks. Will be populated during BaseMod.load().
 * 
 * All of this stuff is metadata sensitive which is not reflected here!
 * 
 * Make sure to only reference it in modsLoaded() or later.
 * 
 * @author SirSengir
 * 
 */
public class ForestryBlock {

	/**
	 * 0 - Humus 1 - Bog Earth
	 */
	public static Block soil;
	/**
	 * 0 - Apatite Ore 1 - Copper Ore 2 - Tin Ore
	 */
	public static Block resources;
	/**
	 * 0 - Legacy 1 - Forest Hive 2 - Meadows Hive
	 */
	public static Block beehives;

	public static Block mushroom;
	public static Block candle;
	public static Block stump;
	public static Block glass;

	public static Block planks1;
	public static Block planks2;
	public static Block slabs1;
	public static Block slabs2;
	public static Block slabs3;
	public static Block slabs4;
	public static Block log1;
	public static Block log2;
	public static Block log3;
	public static Block log4;
	public static Block log5;
	public static Block log6;
	public static Block log7;
	public static Block log8;

	public static Block fences1;
	public static Block fences2;
	public static Block stairs;

	public static Block saplingGE;
	public static Block leaves;
	public static Block pods;

	public static BlockBase arboriculture;

	public static Block alveary;
	public static Block farm;

	public static BlockBase core;
	public static BlockBase apiculture;
	public static BlockBase mail;

	public static BlockBase engine;
	public static BlockBase factoryTESR;
	public static BlockBase factoryPlain;

	public static BlockBase lepidopterology;
}
