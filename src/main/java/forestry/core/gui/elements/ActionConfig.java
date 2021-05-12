package forestry.core.gui.elements;

import java.util.EnumMap;

public class ActionConfig {
	public static ActionConfig self() {
		return selfBuilder().create();
	}

	public static ActionConfig all() {
		return allBuilder().create();
	}

	public static Builder selfBuilder() {
		return new Builder(ActionOrigin.SELF);
	}

	public static Builder allBuilder() {
		return new Builder(ActionOrigin.ALL);
	}

	private final EnumMap<ActionType, ActionOrigin> actionOrigins;

	public ActionConfig(EnumMap<ActionType, ActionOrigin> actionOrigins) {
		this.actionOrigins = actionOrigins;
	}

	public ActionOrigin get(ActionType type) {
		return actionOrigins.get(type);
	}

	public boolean has(ActionType type, ActionOrigin origin) {
		return get(type) == origin;
	}

	public static class Builder {

		private final EnumMap<ActionType, ActionOrigin> permissions;

		public Builder(ActionOrigin origin) {
			permissions = new EnumMap<>(ActionType.class);
			for (ActionType event : ActionType.values()) {
				permissions.put(event, origin);
			}
		}

		public Builder self(ActionType... events) {
			return set(ActionOrigin.SELF, events);
		}

		public Builder all(ActionType... events) {
			return set(ActionOrigin.ALL, events);
		}

		public Builder set(ActionOrigin origin, ActionType... events) {
			for (ActionType event : events) {
				permissions.put(event, origin);
			}
			return this;
		}

		public ActionConfig create() {
			return new ActionConfig(permissions);
		}
	}
}
