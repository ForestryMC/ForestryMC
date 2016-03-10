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
package forestry.lepidopterology.commands;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.arboriculture.PluginArboriculture;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SubCommand;
import forestry.lepidopterology.entities.EntityButterfly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandButterfly extends SubCommand {

	public CommandButterfly() {
		super("butterfly");
		addAlias("bfly");
		addChildCommand(new CommandButterflyKill());
	}

	public static class CommandButterflyKill extends SubCommand {

		public CommandButterflyKill() {
			super("kill");
			addAlias("killall");
			setPermLevel(PermLevel.ADMIN);
		}

		@Override
		public void processSubCommand(ICommandSender sender, String[] args) throws WrongUsageException {
			if (args.length > 1) {
				CommandHelpers.throwWrongUsage(sender, this);
			}

			World world = CommandHelpers.getWorld(sender, this, args, 0);
			if (world == null) {
				CommandHelpers.throwWrongUsage(sender, this);
			} else {
				for (Object entity : world.loadedEntityList) {
					if (entity instanceof EntityButterfly) {
						((EntityButterfly) entity).setDead();
					}
				}

				BlockPos pos = sender.getPosition();
				for (Block block : PluginArboriculture.blocks.logs) {
					for (IBlockState state : block.getBlockState().getValidStates()) {
						world.setBlockState(pos, state, 2 | 4);
						pos = pos.add(2, 0, 0);
					}
				}

				pos = sender.getPosition().add(0, 0, 2);
				for (Block block : PluginArboriculture.blocks.planks) {
					for (IBlockState state : block.getBlockState().getValidStates()) {
						world.setBlockState(pos, state, 2 | 4);
						pos = pos.add(2, 0, 0);
					}
				}

				pos = sender.getPosition().add(0, 0, 4);
				for (Block block : PluginArboriculture.blocks.slabs) {
					for (IBlockState state : block.getBlockState().getValidStates()) {
						world.setBlockState(pos, state, 2 | 4);
						pos = pos.add(2, 0, 0);
					}
				}

				pos = sender.getPosition().add(0, 0, 6);
				for (Block block : PluginArboriculture.blocks.slabsDouble) {
					for (IBlockState state : block.getBlockState().getValidStates()) {
						world.setBlockState(pos, state, 2 | 4);
						pos = pos.add(2, 0, 0);
					}
				}

				pos = sender.getPosition().add(0, 0, 8);
				for (Block block : PluginArboriculture.blocks.fences) {
					for (IBlockState state : block.getBlockState().getValidStates()) {
						world.setBlockState(pos, state, 2 | 4);
						pos = pos.add(2, 0, 0);
					}
				}

				pos = sender.getPosition().add(0, 0, 10);
				for (Block block : PluginArboriculture.blocks.stairs) {
					for (IBlockState state : block.getBlockState().getValidStates()) {
						world.setBlockState(pos, state, 2 | 4);
						pos = pos.add(2, 0, 0);
					}
				}
			}
		}

	}

}
