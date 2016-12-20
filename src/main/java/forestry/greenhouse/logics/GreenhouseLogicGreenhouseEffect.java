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
package forestry.greenhouse.logics;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.greenhouse.DefaultGreenhouseLogic;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.IGreenhouseClimaLogic;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.utils.CamouflageUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class GreenhouseLogicGreenhouseEffect extends DefaultGreenhouseLogic implements IGreenhouseClimaLogic {

	private float lightTransmittance;
	private int workTimer;

	public GreenhouseLogicGreenhouseEffect(IGreenhouseController controller) {
		super(controller, "GreenhouseEffect");

	}

	@Override
	public void work() {
		/*if (controller == null || !controller.isAssembled()) {
			return;
		}
		if (controller.getWorldObj().isDaytime()) {
			if (workTimer++ > 20) {
				controller.addTemperatureChange(lightTransmittance / 100, 0F, 2.5F);
				workTimer = 0;
			}
		}*/
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("workTimer", workTimer);
		nbt.setFloat("lightTransmittance", lightTransmittance);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		workTimer = nbt.getInteger("workTimer");
		lightTransmittance = nbt.getFloat("lightTransmittance");
	}

	@Override
	public void onEvent(EnumGreenhouseEventType type, Object event) {
		if (type == EnumGreenhouseEventType.CAMOUFLAGE) {
			if (!controller.isAssembled()) {
				return;
			}
			float lightTransmittance = 0F;
			int i = 0;

			World world = controller.getWorldObj();
			for (IMultiblockComponent component : controller.getComponents()) {
				if (component instanceof ICamouflagedTile) {
					ICamouflagedTile block = (ICamouflagedTile) component;
					if (block.getCamouflageType().equals(CamouflageManager.GLASS)) {
						if (world.canBlockSeeSky(component.getCoordinates())) {
							ItemStack camouflageStack = CamouflageUtil.getCamouflageBlock(world, component.getCoordinates());
							ICamouflageItemHandler handler = CamouflageManager.camouflageAccess.getHandlerFromItem(camouflageStack);
							if (handler != null) {
								float camouflageLightTransmittance = handler.getLightTransmittance(camouflageStack, controller);
								if (camouflageLightTransmittance < 1 && camouflageLightTransmittance > 0) {
									lightTransmittance = lightTransmittance + camouflageLightTransmittance;
									i++;
								}
							}
						}
					}
				}
			}
			if (i != 0) {
				this.lightTransmittance = lightTransmittance / i;
			}
		}
	}

}
