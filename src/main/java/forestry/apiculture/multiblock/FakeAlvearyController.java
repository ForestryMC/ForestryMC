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

import java.io.IOException;
import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.FakeBeekeepingLogic;
import forestry.core.delegates.FakeAccessHandler;
import forestry.core.delegates.FakeErrorLogic;
import forestry.core.interfaces.IAccessHandler;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.utils.EnumAccess;

public class FakeAlvearyController implements IAlvearyController {
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
	public Iterable<IAlvearyComponent> getComponents() {
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
	public EnumTemperature getTemperature() {
		return EnumTemperature.NORMAL;
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.NORMAL;
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
	public float getExactTemperature() {
		return 0.5f;
	}

	@Override
	public float getExactHumidity() {
		return 0.5f;
	}

	@Override
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return FakeErrorLogic.instance;
	}

	@Override
	public World getWorld() {
		return null;
	}

	@Override
	public BlockPos getPos() {
		return null;
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {

	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {

	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {

	}

	@Override
	public BiomeGenBase getBiome() {
		return null;
	}

	@Override
	public IAccessHandler getAccessHandler() {
		return FakeAccessHandler.getInstance();
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public int getHealthScaled(int i) {
		return 0;
	}

	private static class FakeBeeHousingInventory implements IBeeHousingInventory {
		public static final FakeBeeHousingInventory instance = new FakeBeeHousingInventory();

		private FakeBeeHousingInventory() {

		}

		@Override
		public ItemStack getQueen() {
			return null;
		}

		@Override
		public ItemStack getDrone() {
			return null;
		}

		@Override
		public void setQueen(ItemStack itemstack) {

		}

		@Override
		public void setDrone(ItemStack itemstack) {

		}

		@Override
		public boolean addProduct(ItemStack product, boolean all) {
			return false;
		}
	}
}
