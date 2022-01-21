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

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import net.minecraftforge.common.util.FakePlayer;

import forestry.core.config.Constants;
import forestry.core.tiles.TemperatureState;
import forestry.core.utils.DamageSourceForestry;
import forestry.energy.features.EnergyTiles;

public class TileEngineClockwork extends TileEngine {

	private final static float WIND_EXHAUSTION = 0.05f;
	private final static float WIND_TENSION_BASE = 0.5f;
	private final static int WIND_DELAY = 10;

	private static final int ENGINE_CLOCKWORK_HEAT_MAX = 300000;
	private static final int ENGINE_CLOCKWORK_ENERGY_PER_CYCLE = 2;
	private static final float ENGINE_CLOCKWORK_WIND_MAX = 8f;

	private static final DamageSourceForestry damageSourceEngineClockwork = new DamageSourceForestry("engine.clockwork");

	private float tension = 0.0f;
	private short delay = 0;

	public TileEngineClockwork() {
		super(EnergyTiles.CLOCKWORK_ENGINE.tileType(), "", ENGINE_CLOCKWORK_HEAT_MAX, 10000);
	}

	@Override
	public void openGui(ServerPlayer player, BlockPos pos) {
		if (player instanceof FakePlayer) {
			return;
		}

		if (tension <= 0) {
			tension = WIND_TENSION_BASE;
		} else if (tension < ENGINE_CLOCKWORK_WIND_MAX + WIND_TENSION_BASE) {
			tension += (ENGINE_CLOCKWORK_WIND_MAX + WIND_TENSION_BASE - tension) / (ENGINE_CLOCKWORK_WIND_MAX + WIND_TENSION_BASE) * WIND_TENSION_BASE;
		} else {
			return;
		}

		player.causeFoodExhaustion(WIND_EXHAUSTION);
		if (tension > ENGINE_CLOCKWORK_WIND_MAX + 0.1 * WIND_TENSION_BASE) {
			player.hurt(damageSourceEngineClockwork, 6);
		}
		tension = Math.min(tension, ENGINE_CLOCKWORK_WIND_MAX + WIND_TENSION_BASE);
		delay = WIND_DELAY;
		setNeedsNetworkUpdate();
	}

	/* LOADING & SAVING */
	@Override
	public void load(BlockState state, CompoundTag compoundNBT) {
		super.load(state, compoundNBT);
		tension = compoundNBT.getFloat("Wound");
	}


	@Override
	public CompoundTag save(CompoundTag compoundNBT) {
		compoundNBT = super.save(compoundNBT);
		compoundNBT.putFloat("Wound", tension);
		return compoundNBT;
	}

	@Override
	public boolean isRedstoneActivated() {
		return true;
	}

	@Override
	public int dissipateHeat() {
		return 0;
	}

	@Override
	public int generateHeat() {
		return 0;
	}

	@Override
	public boolean mayBurn() {
		return true;
	}

	@Override
	public void burn() {

		heat = (int) (tension * 10000);

		if (delay > 0) {
			delay--;
			return;
		}

		if (!isBurning()) {
			return;
		}

		if (tension > 0.01f) {
			tension *= 0.9995f;
		} else {
			tension = 0;
		}
		energyManager.generateEnergy(ENGINE_CLOCKWORK_ENERGY_PER_CYCLE * (int) tension);
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
	}

	@Override
	protected boolean isBurning() {
		return tension > 0;
	}

	@Override
	public TemperatureState getTemperatureState() {
		TemperatureState state = TemperatureState.getState(heat / 10000, ENGINE_CLOCKWORK_WIND_MAX);
		if (state == TemperatureState.MELTING) {
			state = TemperatureState.OVERHEATING;
		}
		return state;
	}

	@Override
	public float getPistonSpeed() {
		if (delay > 0) {
			return 0;
		}

		float fromClockwork = tension / ENGINE_CLOCKWORK_WIND_MAX * Constants.ENGINE_PISTON_SPEED_MAX;

		fromClockwork = Math.round(fromClockwork * 100f) / 100f;

		return fromClockwork;
	}

	@Override
	@Nullable
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return null;
	}
}
