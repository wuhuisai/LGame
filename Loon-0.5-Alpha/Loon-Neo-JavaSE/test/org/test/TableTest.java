package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.component.table.LTable;
import loon.component.table.ListItem;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class TableTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}
	
	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		TArray<ListItem> list = new TArray<ListItem>();

		ListItem item = new ListItem();
		item.name = "表格1";
		item.list.add("ffffff");
		item.list.add("gggggggg");
		item.list.add("hhhhhhhhh");
		list.add(item);

		ListItem item2 = new ListItem();
		item2.name = "表格2";
		item2.list.add("zzzzzz");
		item2.list.add("kkkkkkkk");
		item2.list.add("xxxxxxxxx");
		list.add(item2);
		LTable table = new LTable(LFont.getDefaultFont(), 60, 60, 300, 300);
		table.setData(list, 100);
		add(table);
		
		add(MultiScreenTest.getBackButton(this));

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.width = 640;
		setting.height = 480;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new TableTest();
			}
		});
	}
}