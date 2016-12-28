package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;
import loon.font.LFont;

public class TableSkin {

	private IFont font;

	private LTexture backgroundTexture;

	private LTexture headerTexture;

	private LColor fontColor;

	public final static TableSkin def() {
		return new TableSkin();
	}

	public TableSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(7),
				DefUI.getDefaultTextures(4));
	}

	public TableSkin(IFont font, LColor fontColor, LTexture header,
			LTexture background) {
		this.font = font;
		this.fontColor = fontColor;
		this.headerTexture = header;
		this.backgroundTexture = background;
	}

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackgroundTexture(LTexture background) {
		this.backgroundTexture = background;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public LTexture getHeaderTexture() {
		return headerTexture;
	}

	public void setHeaderTexture(LTexture headerTexture) {
		this.headerTexture = headerTexture;
	}

}