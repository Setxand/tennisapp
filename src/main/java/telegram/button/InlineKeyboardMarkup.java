package telegram.button;

import com.fasterxml.jackson.annotation.JsonProperty;
import telegram.Markup;

import java.util.List;

public class InlineKeyboardMarkup implements Markup {
	@JsonProperty("inline_keyboard")
	private List<List<Button>> inlineKeyBoard;
	@JsonProperty("one_time_keyboard")
	public Boolean onwTimeKeyboard = true;
	public InlineKeyboardMarkup() {
	}

	public InlineKeyboardMarkup(List<List<Button>> inlineKeyBoard) {
		this.inlineKeyBoard = inlineKeyBoard;
	}

	public List<List<Button>> getInlineKeyBoard() {
		return inlineKeyBoard;
	}
}
