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
package forestry.core.inventory;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlyzer;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.errors.EnumErrorCode;
import forestry.core.gui.IHintSource;
import forestry.core.utils.GeneticsUtil;

public class ItemInventoryAlyzer extends ItemInventory implements IErrorSource, IHintSource {
	public static final int SLOT_SPECIMEN = 0;
	public static final int SLOT_ANALYZE_1 = 1;
	public static final int SLOT_ANALYZE_2 = 2;
	public static final int SLOT_ANALYZE_3 = 3;
	public static final int SLOT_ANALYZE_4 = 4;
	public static final int SLOT_ANALYZE_5 = 6;
	public static final int SLOT_ENERGY = 5;

	public IAlyzer alyzer;
	
	public ItemInventoryAlyzer(EntityPlayer player, ItemStack itemstack, IAlyzer alyzer) {
		super(player, 7, itemstack);
		
		this.alyzer = alyzer;
	}
	
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		ISpeciesRoot speciesRoot;
		if(!hasSpecimen() && slotIndex == SLOT_SPECIMEN){
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(itemStack);
			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(ersatz);
		}else{
			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(getSpecimen());
		}
		if(speciesRoot != null){
			return speciesRoot.getAlyzer().canSlotAccept(this, slotIndex, itemStack);
		}
		return alyzer.canSlotAccept(this, slotIndex, itemStack);
	}
	
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(!hasSpecimen() && slotIndex == SLOT_SPECIMEN){
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(itemStack);
			if(ersatz != null){
				super.setInventorySlotContents(slotIndex, ersatz);
				return;
			}
		}
		super.setInventorySlotContents(slotIndex, itemStack);
	}
	
	@Override
	public void onSlotClick(int slotIndex, EntityPlayer player) {
		ISpeciesRoot speciesRoot;
		if(hasSpecimen() && AlleleManager.alleleRegistry.getSpeciesRoot(getSpecimen()) == null && slotIndex == SLOT_SPECIMEN){
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(getStackInSlot(slotIndex));
			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(ersatz);
		}else{
			speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(getSpecimen());
		}
		if(speciesRoot != null){
			speciesRoot.getAlyzer().onSlotClick(this, slotIndex, player);
		}else{
			alyzer.onSlotClick(this, slotIndex, player);
		}
	}

	@Override
	public final ImmutableSet<IErrorState> getErrorStates() {
		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		if (!hasSpecimen()) {
			errorStates.add(EnumErrorCode.NO_SPECIMEN);
		}else{
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(getSpecimen());
			if (speciesRoot != null && !speciesRoot.getAlyzer().isAlyzingFuel(getStackInSlot(SLOT_ENERGY))) {
				errorStates.add(EnumErrorCode.NO_HONEY);
			}
		}

		return errorStates.build();
	}
	
	public ItemStack getSpecimen() {
		for (int i = SLOT_SPECIMEN; i <= SLOT_ANALYZE_5; i++) {
			if (i == SLOT_ENERGY) {
				continue;
			}

			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null) {
				return itemStack;
			}
		}
		return null;
	}

	public boolean hasSpecimen() {
		return getSpecimen() != null;
	}
	
	/* IHintSource */
	@Override
	public List<String> getHints() {
		if(hasSpecimen()){
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(getSpecimen());
			IAlyzer alyzer = speciesRoot.getAlyzer();
			if(alyzer instanceof IHintSource){
				return ((IHintSource)alyzer).getHints();
			}
		}
		return Collections.emptyList();
	}
}
