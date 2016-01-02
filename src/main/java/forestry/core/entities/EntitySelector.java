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

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;

public abstract class EntitySelector<T extends Entity> implements Predicate<T> {
	private final Class<T> entityClass;

	protected EntitySelector(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}
	
	@Override
	public boolean apply(Entity entity) {
		if(entity == null)
			return false;
		T castEntity = entityClass.cast(entity);
		return isEntityApplicableTyped(castEntity);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	protected abstract boolean isEntityApplicableTyped(T entity);
}
