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
package forestry.farming.logic.crops;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

public class CropBasicAgriCraft extends Crop {

	@Nullable
	private static Method growthStageMethod;
	@Nullable
	private static Method dropsMethod;
	private static boolean searchedMethod = false;
	private final IBlockState blockState;

	public CropBasicAgriCraft(World world, IBlockState blockState, BlockPos position) {
		super(world, position);
		this.blockState = blockState;
	}

	private void replant(World world, BlockPos pos, TileEntity tileEntity) {
		findMethods();
		if (growthStageMethod != null) {
			try {
				growthStageMethod.invoke(tileEntity, 0);
				PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
				NetworkUtil.sendNetworkPacket(packet, pos, world);
			} catch (InvocationTargetException | IllegalAccessException ignored) {
			}
		}
	}

	private void findMethods() {
		if (!searchedMethod) {
			Method growthStage = null;
			Method drops = null;
			try {
				Class tileClass = Class.forName("com.infinityraider.agricraft.tiles.TileEntityCrop");
				growthStage = ReflectionHelper.findMethod(tileClass, "setGrowthStage", null, int.class);
				drops = ReflectionHelper.findMethod(tileClass, "getDrops", null, Consumer.class, boolean.class, boolean.class, boolean.class);

			} catch (ReflectionHelper.UnableToFindMethodException | ClassNotFoundException e) {
			}
			growthStageMethod = growthStage;
			dropsMethod = drops;
			searchedMethod = true;
		}
	}

	private void addDrops(World world, BlockPos pos, TileEntity tileEntity, Consumer<ItemStack> addToList) {
		findMethods();
		if (dropsMethod != null) {
			try {
				dropsMethod.invoke(tileEntity, addToList, false, false, true);
			} catch (InvocationTargetException | IllegalAccessException ignored) {
			}
		}
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		NonNullList<ItemStack> harvest = NonNullList.create();
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null) {
			return harvest;
		}
		addDrops(world, pos, tileEntity, harvest::add);

		replant(world, pos, tileEntity);
		return harvest;
	}

	@Override
	public String toString() {
		return String.format("CropBasicAgriCraft [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
