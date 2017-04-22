package forestry.mail;

import forestry.api.mail.IPostalState;
import forestry.core.utils.Translator;

public class ResponseNotMailable implements IPostalState {
	private final IPostalState state;

	public ResponseNotMailable(IPostalState state) {
		this.state = state;
	}

	@Override
	public boolean isOk() {
		return false;
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocalFormatted("for.chat.mail.response.not.mailable.format", state.getDescription());
	}
}
