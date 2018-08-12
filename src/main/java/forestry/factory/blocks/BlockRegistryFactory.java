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
package forestry.factory.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockNBT;
import forestry.factory.MachineUIDs;
import forestry.factory.ModuleFactory;

public class BlockRegistryFactory extends BlockRegistry {
	public final BlockFactoryTESR bottler;
	public final BlockFactoryTESR carpenter;
	public final BlockFactoryTESR centrifuge;
	public final BlockFactoryTESR fermenter;
	public final BlockFactoryTESR moistener;
	public final BlockFactoryTESR squeezer;
	public final BlockFactoryTESR still;
	public final BlockFactoryTESR rainmaker;

	public final BlockFactoryPlain fabricator;
	public final BlockFactoryPlain raintank;

	public BlockRegistryFactory() {
		bottler = new BlockFactoryTESR(BlockTypeFactoryTesr.BOTTLER);
		if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
			registerBlock(bottler, new ItemBlockForestry<>(bottler), MachineUIDs.BOTTLER);
		}

		carpenter = new BlockFactoryTESR(BlockTypeFactoryTesr.CARPENTER);
		if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
			registerBlock(carpenter, new ItemBlockForestry<>(carpenter), MachineUIDs.CARPENTER);
		}

		centrifuge = new BlockFactoryTESR(BlockTypeFactoryTesr.CENTRIFUGE);
		if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
			registerBlock(centrifuge, new ItemBlockForestry<>(centrifuge), MachineUIDs.CENTRIFUGE);
		}

		fermenter = new BlockFactoryTESR(BlockTypeFactoryTesr.FERMENTER);
		if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
			registerBlock(fermenter, new ItemBlockForestry<>(fermenter), MachineUIDs.FERMENTER);
		}

		moistener = new BlockFactoryTESR(BlockTypeFactoryTesr.MOISTENER);
		if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
			registerBlock(moistener, new ItemBlockForestry<>(moistener), MachineUIDs.MOISTENER);
		}

		squeezer = new BlockFactoryTESR(BlockTypeFactoryTesr.SQUEEZER);
		if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
			registerBlock(squeezer, new ItemBlockForestry<>(squeezer), MachineUIDs.SQUEEZER);
		}

		still = new BlockFactoryTESR(BlockTypeFactoryTesr.STILL);
		if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
			registerBlock(still, new ItemBlockForestry<>(still), MachineUIDs.STILL);
		}

		rainmaker = new BlockFactoryTESR(BlockTypeFactoryTesr.RAINMAKER);
		if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
			registerBlock(rainmaker, new ItemBlockForestry<>(rainmaker), MachineUIDs.RAINMAKER);
		}

		fabricator = new BlockFactoryPlain(BlockTypeFactoryPlain.FABRICATOR);
		if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
			registerBlock(fabricator, new ItemBlockNBT(fabricator), MachineUIDs.FABRICATOR);
		}

		raintank = new BlockFactoryPlain(BlockTypeFactoryPlain.RAINTANK);
		if (ModuleFactory.machineEnabled(MachineUIDs.RAINTANK)) {
			registerBlock(raintank, new ItemBlockNBT(raintank), MachineUIDs.RAINTANK);
		}
	}
}
