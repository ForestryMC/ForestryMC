/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.core.network.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class CropFruit extends Crop {

	IFruitFamily family;

	public CropFruit(World world, Vect position, IFruitFamily family) {
		super(world, position);
		this.family = family;
	}

	@Override
	protected boolean isCrop(Vect pos) {
		TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);
		if (!(tile instanceof IFruitBearer))
			return false;
		IFruitBearer bearer = (IFruitBearer) tile;
		if (!bearer.hasFruit())
			return false;
		if (bearer.getRipeness() < 0.9f)
			return false;

		return true;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);
		if (!(tile instanceof IFruitBearer))
			return new ArrayList<ItemStack>();

		Proxies.common.sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.LEAF, world, pos.x, pos.y, pos.z,
				world.getBlock(pos.x, pos.y, pos.z), 0);
		return ((IFruitBearer) tile).pickFruit(null);
	}

}
