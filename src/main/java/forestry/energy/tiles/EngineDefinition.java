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
package forestry.energy.tiles;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.MachineDefinition;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;

public class EngineDefinition extends MachineDefinition {

	public EngineDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		super(meta, teIdent, teClass, renderer, recipes);
	}

	@Override
	public boolean isSolidOnSide(IBlockAccess world, int x, int y, int z, int side) {
		TileEngine tile = TileUtil.getTile(world, x, y, z, TileEngine.class);
		if (tile == null) {
			return false;
		}

		return tile.getOrientation().getOpposite().ordinal() == side;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEngine tile = TileUtil.getTile(world, x, y, z, TileEngine.class);
		if (tile == null) {
			return false;
		}

		tile.rotateEngine();
		return true;
	}
}
