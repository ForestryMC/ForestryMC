/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Needs to be implemented by TileEntities that want to be part of an charcoal pile.
 * The sub-interfaces can be implemented to alter the operation of the charcoal pile.
 * They are automatically detected and handled by the charcoal pile when they join its structure.
 */
public interface ICharcoalPileComponent<T extends IMultiblockLogicCharcoalPile> extends IMultiblockComponent {
	
	@Override
	T getMultiblockLogic();

	IAlleleTreeSpecies getTreeSpecies();

	void setTreeSpecies(@Nonnull IAlleleTreeSpecies treeSpecies);
	
	@Override
	@Nonnull
	GameProfile getOwner();
	
	void setOwner(@Nonnull GameProfile owner);
	
	EnumPileType getPileType();
	
	@SideOnly(Side.CLIENT)
	IAlleleTreeSpecies getNextWoodPile();

}