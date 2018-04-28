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
package forestry.core.tiles;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import forestry.core.utils.Translator;

public class EscritoireTextSource {

	private enum Notes {
		level1, level2, level3, level4, success, failure, empty
	}

	private static final ListMultimap<Notes, String> researchNotes;
	private static final Random random = new Random();

	static {
		researchNotes = ArrayListMultimap.create();
		EnumSet<Notes> multipleTranslationNoteLevels = EnumSet.of(Notes.level1, Notes.level2, Notes.level3, Notes.level4, Notes.success, Notes.failure);
		for (Notes notesLevel : multipleTranslationNoteLevels) {
			for (int i = 1; i <= 10; i++) {
				String key = "for.gui.escritoire.notes." + notesLevel + '.' + i;
				String note = Translator.translateToLocal(key);
				if (key.equals(note)) {
					break;
				} else {
					researchNotes.put(notesLevel, note);
				}
			}
		}
		researchNotes.put(Notes.empty, Translator.translateToLocal("for.gui.escritoire.instructions"));
	}

	@Nullable
	private String researchNote;
	@Nullable
	private Notes lastNoteLevel;

	public String getText(EscritoireGame escritoireGame) {
		Notes noteLevel = getNoteLevel(escritoireGame);
		if (lastNoteLevel != noteLevel || researchNote == null) {
			researchNote = getRandomNote(noteLevel);
			lastNoteLevel = noteLevel;
		}

		return researchNote;
	}

	private static String getRandomNote(Notes level) {
		List<String> candidates = researchNotes.get(level);
		int index = random.nextInt(candidates.size());
		return candidates.get(index);
	}

	public static Notes getNoteLevel(EscritoireGame game) {
		EscritoireGame.Status status = game.getStatus();
		switch (status) {
			case PLAYING: {
				int bounty = game.getBountyLevel();
				if (bounty >= EscritoireGame.BOUNTY_MAX) {
					return Notes.level1;
				} else if (bounty > EscritoireGame.BOUNTY_MAX / 2) {
					return Notes.level2;
				} else if (bounty > EscritoireGame.BOUNTY_MAX / 4) {
					return Notes.level3;
				} else {
					return Notes.level4;
				}
			}
			case FAILURE:
				return Notes.failure;
			case SUCCESS:
				return Notes.success;
			case EMPTY:
				return Notes.empty;
		}
		return null;
	}
}
