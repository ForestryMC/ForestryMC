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
package forestry.core.utils.datastructures;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @param <T>
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RevolvingList<T> {

	private final Deque<T> list = new LinkedList<>();

	public RevolvingList(Collection<? extends T> collection) {
		Preconditions.checkArgument(!collection.isEmpty());
		for (T object : collection) {
			Preconditions.checkNotNull(object);
			list.add(object);
		}
	}

	public void rotateLeft() {
		list.addFirst(list.removeLast());
	}

	public void rotateRight() {
		list.addLast(list.removeFirst());
	}

	public T getCurrent() {
		return list.getFirst();
	}

	public void setCurrent(T e) {
		if (!list.contains(e)) {
			return;
		}

		while (!getCurrent().equals(e)) {
			rotateRight();
		}
	}

}
