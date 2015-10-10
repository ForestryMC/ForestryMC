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

import com.google.common.collect.ForwardingCollection;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @param <T>
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RevolvingList<T> extends ForwardingCollection<T> {

	private final Deque<T> list = new LinkedList<>();

	public RevolvingList() {
	}

	public RevolvingList(Collection<? extends T> collection) {
		list.addAll(collection);
	}

	@Override
	protected Collection<T> delegate() {
		return list;
	}

	public void rotateLeft() {
		if (list.isEmpty()) {
			return;
		}
		list.addFirst(list.removeLast());
	}

	public void rotateRight() {
		if (list.isEmpty()) {
			return;
		}
		list.addLast(list.removeFirst());
	}

	public T getCurrent() {
		if (list.isEmpty()) {
			return null;
		}
		return list.getFirst();
	}

	public void setCurrent(T e) {
		if (!contains(e)) {
			return;
		}

		if (e == null) {
			while (getCurrent() != null) {
				rotateRight();
			}
		} else {
			while (getCurrent() == null || !getCurrent().equals(e)) {
				rotateRight();
			}
		}
	}

}
