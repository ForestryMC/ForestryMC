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

import javax.annotation.Nullable;
import java.util.Collections;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.climate.IClimateListener;
import forestry.apiculture.FakeBeekeepingLogic;
import forestry.apiculture.tiles.FakeBeeHousingInventory;
import forestry.core.climate.FakeClimateListener;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.FakeMultiblockController;

public class FakeAlvearyController extends FakeMultiblockController implements IAlvearyControllerInternal {
	public static final FakeAlvearyController instance = new FakeAlvearyController();

	private FakeAlvearyController() {

	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.emptyList();
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.emptyList();
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return FakeBeeHousingInventory.instance;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return FakeBeekeepingLogic.instance;
	}

	@Override
	public int getBlockLightValue() {
		return 0;
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return false;
	}

	@Override
	public boolean isRaining() {
		return false;
	}

	@Override
	@Nullable
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public BlockPos getCoordinates() {
		return BlockPos.ZERO;
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return new Vec3(0, 0, 0);
	}

	@Override
	public Biome getBiome() {
		return ForgeRegistries.BIOMES.getValue(Biomes.PLAINS.getRegistryName());
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public IClimateListener getClimateListener() {
		return FakeClimateListener.INSTANCE;
	}

	@Override
	public int getHealthScaled(int i) {
		return 0;
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.alveary.type";
	}
}
