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
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.core.config.Config;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.ITitled;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.rectangular.PartPosition;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.network.IStreamableGui;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileAlvearyPlain extends TileAlveary implements IBeeHousing, IClimatised, IHintSource, IStreamableGui, ITitled, ITriggerProvider {

	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);

		// set alveary entrance block meta
		if (getPartPosition() == PartPosition.Frame) {
			if (worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) == null) {
				this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, TileAlveary.ENTRANCE_META, 2);
			}
		}
	}

	@Override
	public void onMachineBroken() {
		super.onMachineBroken();

		// set alveary entrance block meta back to normal
		this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, TileAlveary.PLAIN_META, 2);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getAlvearyController().getInternalInventory();
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player) {
		if (isConnected()) {
			player.openGui(ForestryAPI.instance, GuiId.AlvearyGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getAlvearyController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getAlvearyController().readGuiData(data);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "alveary.0.name";
	}

	/* IHintSource */
	@Override
	public boolean hasHints() {
		return Config.hints.get("apiary").length > 0;
	}

	@Override
	public String[] getHints() {
		return Config.hints.get("apiary");
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		return null;
	}

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		Collection<ITriggerExternal> res = new ArrayList<ITriggerExternal>();
		res.add(ApicultureTriggers.missingQueen);
		res.add(ApicultureTriggers.missingDrone);
		return res;
	}

	/* IHousing */
	@Override
	public World getWorld() {
		return getAlvearyController().getWorld();
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return getAlvearyController().getCoordinates();
	}

	@Override
	public BiomeGenBase getBiome() {
		return getAlvearyController().getBiome();
	}

	/* IBeeHousing */
	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return getAlvearyController().getBeeModifiers();
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return getAlvearyController().getBeeListeners();
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return getAlvearyController().getBeeInventory();
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return getAlvearyController().getBeekeepingLogic();
	}

	/* IClimatised */
	@Override
	public EnumTemperature getTemperature() {
		return getAlvearyController().getTemperature();
	}

	@Override
	public EnumHumidity getHumidity() {
		return getAlvearyController().getHumidity();
	}

	@Override
	public int getBlockLightValue() {
		return getAlvearyController().getBlockLightValue();
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return getAlvearyController().canBlockSeeTheSky();
	}

	@Override
	public float getExactTemperature() {
		return getAlvearyController().getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return getAlvearyController().getExactHumidity();
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return getAlvearyController().getErrorLogic();
	}

}
