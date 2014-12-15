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

import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.utils.StackUtils;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class AIButterflyMate extends AIButterflyInteract {

	public AIButterflyMate(EntityButterfly entity) {
		super(entity);
	}

	/**
	 * Should lay egg?
	 * @return
	 */
	@Override
	protected boolean canInteract() {
		if (entity.cooldownEgg > 0)
			return false;

		if (entity.getButterfly().getMate() == null)
			return false;

		TileEntity tile = entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
		IButterflyNursery nursery = null;

		if (tile instanceof IButterflyNursery)
			nursery = (IButterflyNursery) tile;
		else
			for (Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSpecimen.entrySet()) {

				if (!StackUtils.equals(entity.worldObj.getBlock(rest.posX, rest.posY, rest.posZ), entry.getKey()))
					continue;

				int meta = entity.worldObj.getBlockMetadata(rest.posX, rest.posY, rest.posZ);
				if (StackUtils.equals(Blocks.leaves, entry.getKey()))
					meta = meta & 3;
				if (entry.getKey().getItemDamage() != meta)
					continue;

				// We matched, replace the leaf block with ours and set the ersatz genome
				((ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees")).setLeaves(entity.worldObj, entry.getValue(), null, rest.posX, rest.posY, rest.posZ);
				// Now let's pollinate
				nursery = (IButterflyNursery) entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			}
		if (nursery == null)
			return false;

		return nursery.canNurse(entity.getButterfly());
	}

	@Override
	public void updateTask() {
		if (continueExecuting()) {
			IButterflyNursery tile = (IButterflyNursery) entity.worldObj.getTileEntity(rest.posX, rest.posY, rest.posZ);
			if (tile.canNurse(entity.getButterfly())) {
				tile.setCaterpillar(entity.getButterfly().spawnCaterpillar(tile));
//				Proxies.log.finest("A butterfly '%s' laid an egg at %s/%s/%s.", entity.getButterfly().getIdent(), rest.posX, rest.posY, rest.posZ);
				if (entity.getRNG().nextFloat() < 1.0f / entity.getButterfly().getGenome().getFertility())
					entity.setHealth(0);
			}
			setHasInteracted();
			entity.cooldownEgg = EntityButterfly.COOLDOWNS;
		}
	}

}
