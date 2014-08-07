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
package forestry.lepidopterology.entities;

import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.lepidopterology.entities.EntityButterfly.EnumButterflyState;

public class AIButterflyInteract extends AIButterflyBase {

	private ChunkCoordinates rest;

	private boolean canLayEgg = false;
	private boolean canPollinate = false;

	private boolean hasLayedEgg = false;
	private boolean hasPollinated = false;

	public AIButterflyInteract(EntityButterfly entity) {
		super(entity);
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if(entity.getState() != EnumButterflyState.RESTING)
			return false;

		rest = new ChunkCoordinates((int)entity.posX, ((int)Math.floor(entity.posY)) - 1, (int)entity.posZ);
		if(entity.worldObj.isAirBlock(rest.posX, rest.posY, rest.posZ))
			return false;

		canLayEgg = canLayEgg();
		canPollinate = canPollinate();

		return canLayEgg || canPollinate;
	}

	private boolean canLayEgg() {
		if(entity.cooldownEgg > 0)
			return false;

		if(entity.getButterfly().getMate() == null)
			return false;

		TileEntity tile = entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
		IButterflyNursery nursery = null;

		if(tile instanceof IButterflyNursery)
			nursery = (IButterflyNursery)tile;
		else {
			for (Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSpecimen.entrySet()) {

				if (!StackUtils.equals(entity.worldObj.getBlock(rest.posX, rest.posY, rest.posZ), entry.getKey()))
					continue;

				int meta = entity.worldObj.getBlockMetadata(rest.posX, rest.posY, rest.posZ);
				if (StackUtils.equals(Blocks.leaves, entry.getKey()))
					meta = meta & 3;
				if (entry.getKey().getItemDamage() != meta)
					continue;

				// We matched, replace the leaf block with ours and set the ersatz genome
				((ITreeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees")).setLeaves(entity.worldObj, entry.getValue(), null, rest.posX, rest.posY, rest.posZ);
				// Now let's pollinate
				nursery = (IButterflyNursery)entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			}
		}
		if(nursery == null)
			return false;

		return nursery.canNurse(entity.getButterfly());
	}

	private boolean canPollinate() {
		if(entity.cooldownPollination > 0)
			return false;

		IPollinatable pollinatable = Utils.getOrCreatePollinatable(null, entity.worldObj, rest.posX, rest.posY, rest.posZ);
		if(pollinatable == null)
			return false;

		if(!entity.getButterfly().getGenome().getFlowerProvider().isAcceptedPollinatable(entity.worldObj, pollinatable))
			return false;

		if(entity.getPollen() != null && !pollinatable.canMateWith(entity.getPollen()))
			return false;

		return true;
	}

	@Override
	public boolean continueExecuting() {
		return (canLayEgg && !hasLayedEgg) || (canPollinate && !hasPollinated);
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
		hasLayedEgg = false;
		hasPollinated = false;

		canLayEgg = false;
		canPollinate = false;

		rest = null;
	}

	@Override
	public void updateTask() {
		if(canLayEgg && !hasLayedEgg) {
			IButterflyNursery tile = (IButterflyNursery)entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			if(tile.canNurse(entity.getButterfly())) {
				tile.setCaterpillar(entity.getButterfly().spawnCaterpillar(tile));
				Proxies.log.finest("A butterfly '%s' laid an egg at %s/%s/%s.", entity.getButterfly().getIdent(), rest.posX, rest.posY, rest.posZ);
				if(entity.getRNG().nextFloat() < 1.0f / entity.getButterfly().getGenome().getFertility())
					entity.setHealth(0);
			}
			hasLayedEgg = true;
			entity.cooldownEgg = EntityButterfly.COOLDOWNS;
		}

		if(canPollinate && !hasPollinated) {
			IPollinatable pollinatable = (IPollinatable)entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			if(entity.getPollen() == null) {
				entity.setPollen(pollinatable.getPollen());
				//Proxies.log.finest("A butterfly '%s' grabbed a pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
			} else if(pollinatable.canMateWith(entity.getPollen())) {
				pollinatable.mateWith(entity.getPollen());
				//Proxies.log.finest("A butterfly '%s' unloaded pollen '%s' at %s/%s/%s.", entity.getButterfly().getIdent(), entity.getPollen().getIdent(), rest.posX, rest.posY, rest.posZ);
				entity.setPollen(null);
			}
			hasPollinated = true;
			entity.cooldownPollination = EntityButterfly.COOLDOWNS;
		}
	}

}
