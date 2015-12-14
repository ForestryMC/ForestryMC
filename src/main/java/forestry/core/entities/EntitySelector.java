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
package forestry.core.entities;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public abstract class EntitySelector<T extends Entity> implements IEntitySelector {
	private final Class<T> entityClass;

	protected EntitySelector(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	@Override
	public final boolean isEntityApplicable(Entity entity) {
		T castEntity = entityClass.cast(entity);
		return isEntityApplicableTyped(castEntity);
	}

	protected abstract boolean isEntityApplicableTyped(T entity);
}
