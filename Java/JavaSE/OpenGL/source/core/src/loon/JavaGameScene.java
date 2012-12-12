package loon;

import loon.action.ActionControl;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LImage;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LSTRFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LProcess;
import loon.core.timer.LTimerContext;
import loon.core.timer.SystemTimer;
import loon.utils.MathUtils;
import loon.utils.ScreenUtils;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * 
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email javachenpeng@yahoo.com
 * @version 0.1.1
 */
public class JavaGameScene {

	public static class LSetting {

		public int width = LSystem.MAX_SCREEN_WIDTH;

		public int height = LSystem.MAX_SCREEN_HEIGHT;

		public int fps = LSystem.DEFAULT_MAX_FPS;

		public String title;

		public boolean showFPS;

		public boolean showMemory;

		public boolean showLogo;

	}

	private static Class<?> getType(Object o) {
		if (o instanceof Integer) {
			return Integer.TYPE;
		} else if (o instanceof Float) {
			return Float.TYPE;
		} else if (o instanceof Double) {
			return Double.TYPE;
		} else if (o instanceof Long) {
			return Long.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Boolean) {
			return Boolean.TYPE;
		} else {
			return o.getClass();
		}
	}

	public static void register(LSetting setting,
			Class<? extends Screen> clazz, Object... args) {
		JavaGameScene game = new JavaGameScene(setting.title, setting.width,
				setting.height);
		game.setShowFPS(setting.showFPS);
		game.setShowMemory(setting.showMemory);
		game.setShowLogo(setting.showLogo);
		game.setFPS(setting.fps);
		if (clazz != null) {
			if (args != null) {
				try {
					final int funs = args.length;
					if (funs == 0) {
						game.setScreen(clazz.newInstance());
						game.showScreen();
					} else {
						Class<?>[] functions = new Class<?>[funs];
						for (int i = 0; i < funs; i++) {
							functions[i] = getType(args[i]);
						}
						java.lang.reflect.Constructor<?> constructor = Class
								.forName(clazz.getName()).getConstructor(
										functions);
						Object o = constructor.newInstance(args);
						if (o != null && (o instanceof Screen)) {
							game.setScreen((Screen) o);
							game.showScreen();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private LSTRFont fpsFont;

	public static enum GLMode {

		Default, VBO;

		private String text;

		private GLMode() {
			text = "GLMode : " + name();
		}

		public String toString() {
			return text;
		};
	}

	private long maxFrames = LSystem.DEFAULT_MAX_FPS, frameRate;

	private OpenGLThread mainLoop;

	private boolean isRunning, isFull, isFPS, isMemory;

	private DisplayMode displayMode;

	private int width, height, drawPriority;

	private GLEx gl;

	private GLMode glMode = GLMode.Default;

	private String windowTitle;

	private RectBox bounds = new RectBox();

	private boolean fullscreen = true;

	private boolean clear = true;

	private LTexture logo;

	private static int updateWidth = 0, updateHeight = 0;

	private static boolean updateScreen = false;

	public static void updateSize(int w, int h) {
		JavaGameScene.updateWidth = w;
		JavaGameScene.updateHeight = h;
		JavaGameScene.updateScreen = true;
	}

	public JavaGameScene() {
		this(null, LSystem.MAX_SCREEN_WIDTH, LSystem.MAX_SCREEN_HEIGHT);
	}

	public JavaGameScene(String titleName, int width, int height) {
		if (width < 1 || height < 1) {
			throw new RuntimeException("Width and Height must be positive !");
		}
		if (LSystem.isWindows()) {
			System.setProperty("sun.java2d.translaccel", "true");
			System.setProperty("sun.java2d.ddforcevram", "true");
		} else if (LSystem.isMacOS()) {
			System.setProperty("apple.awt.showGrowBox", "false");
			System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
			System.setProperty("apple.awt.graphics.EnableLazyDrawing", "true");
			System.setProperty(
					"apple.awt.window.position.forceSafeUserPositioning",
					"true");
			System.setProperty("apple.awt.window.position.forceSafeCreation",
					"true");
			System.setProperty("com.apple.hwaccel", "true");
			System.setProperty("com.apple.forcehwaccel", "true");
			System.setProperty("com.apple.macos.smallTabs", "true");
			System.setProperty("com.apple.macos.use-file-dialog-packages",
					"true");
		} else {
			System.setProperty("sun.java2d.opengl", "true");
		}
		if (LSystem.screenRect == null) {
			LSystem.screenRect = new RectBox(0, 0, width, height);
		} else {
			LSystem.screenRect.setBounds(0, 0, width, height);
		}
		LSystem.screenProcess = new LProcess(this, width, height);

		setFPS(LSystem.DEFAULT_MAX_FPS);
		setTitle(titleName);
		setSize(width, height);
	}

	public void setTitle(String titleName) {
		this.windowTitle = titleName;
	}

	public void setGLMode(GLMode renderMode) {
		this.glMode = renderMode;
	}

	public GLEx getGraphics() {
		return gl;
	}

	public void setScreen(Screen screen) {
		LSystem.screenProcess.setScreen(screen);
	}

	public int getHeight() {
		return bounds.height;
	}

	public int getWidth() {
		return bounds.width;
	}

	protected void setBounds(int x, int y, int width, int height) {
		bounds.setBounds(x, y, width, height);
	}

	public RectBox getBounds() {
		return bounds;
	}

	public void setViewPort(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
		gl.setViewPort(x, y, width, height);
	}

	public void setViewPort(RectBox vPort) {
		setViewPort((int) vPort.x, (int) vPort.y, vPort.width, vPort.height);
	}

	public RectBox getViewPort() {
		return gl.getViewPort();
	}

	final private class OpenGLThread extends Thread {

		private long before, lastTimeMicros, currTimeMicros, goalTimeMicros,
				elapsedTimeMicros, remainderMicros, elapsedTime, frameCount,
				frames;

		public OpenGLThread() {
			isRunning = true;
			setName("OpenGLThread");
		}

		/**
		 * 显示游戏logo
		 */
		private void showLogo() {
			int number = 0;
			try {
				long elapsed;
				int cx = 0, cy = 0;
				double delay;
					if (logo == null) {
						logo = LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
								+ "logo.png", Format.BILINEAR);
					}
				cx = (int) (getWidth() * LSystem.scaleWidth) / 2
						- logo.getWidth() / 2;
				cy = (int) (getHeight() * LSystem.scaleHeight) / 2
						- logo.getHeight() / 2;
				float alpha = 0.0f;
				boolean firstTime = true;
				elapsed = innerClock();
				while (alpha < 1.0f) {
					gl.drawClear();
					gl.setAlpha(alpha);
					gl.drawTexture(logo, cx, cy);
					if (firstTime) {
						firstTime = false;
					}
					elapsed = innerClock();
					delay = 0.00065 * elapsed;
					if (delay > 0.22) {
						delay = 0.22 + (delay / 6);
					}
					alpha += delay;
					Display.update();
				}
				while (number < 3000) {
					number += innerClock();
					Display.update();
				}
				alpha = 1.0f;
				while (alpha > 0.0f) {
					gl.drawClear();
					gl.setAlpha(alpha);
					gl.drawTexture(logo, cx, cy);
					elapsed = innerClock();
					delay = 0.00055 * elapsed;
					if (delay > 0.15) {
						delay = 0.15 + ((delay - 0.04) / 2);
					}
					alpha -= delay;
					Display.update();
				}
				gl.setAlpha(1.0f);
				gl.setColor(LColor.white);
			} catch (Throwable e) {
			} finally {
				logo.dispose();
				logo = null;
				gl.setBlendMode(GL.MODE_NORMAL);
				LSystem.isLogo = false;
			}
		}

		private long innerClock() {
			long now = System.currentTimeMillis();
			long ret = now - before;
			before = now;
			return ret;
		}

		/**
		 * 游戏窗体主循环
		 */
		public void run() {
			createScreen();
			if (LSystem.isLogo) {
				showLogo();
			}
			final LTimerContext timerContext = new LTimerContext();
			final SystemTimer timer = LSystem.getSystemTimer();
			final LProcess process = LSystem.screenProcess;
			Thread currentThread = Thread.currentThread();
			process.begin();
			{
				for (; isRunning && !Display.isCloseRequested()
						&& mainLoop == currentThread;) {
					if (!Display.isActive()) {
						LSystem.isPaused = true;
						pause(500);
						LSystem.gc(1000, 1);
						lastTimeMicros = timer.getTimeMicros();
						elapsedTime = 0;
						remainderMicros = 0;
						Display.update();
						continue;
					} else {
						LSystem.isPaused = false;
					}
					synchronized (gl) {

						if (!process.next()) {
							continue;
						}

						process.load();

						process.calls();

						goalTimeMicros = lastTimeMicros + 1000000L / maxFrames;
						currTimeMicros = timer.sleepTimeMicros(goalTimeMicros);
						elapsedTimeMicros = currTimeMicros - lastTimeMicros
								+ remainderMicros;
						elapsedTime = MathUtils.max(0,
								(elapsedTimeMicros / 1000));
						remainderMicros = elapsedTimeMicros - elapsedTime
								* 1000;
						lastTimeMicros = currTimeMicros;
						timerContext.millisSleepTime = remainderMicros;
						timerContext.timeSinceLastUpdate = elapsedTime;

						process.runTimer(timerContext);

						ActionControl.update(elapsedTime);

						if (LSystem.AUTO_REPAINT) {

							int repaintMode = process.getRepaintMode();
							switch (repaintMode) {
							case Screen.SCREEN_BITMAP_REPAINT:
								gl.reset(clear);
								if (process.getX() == 0 && process.getY() == 0) {
									gl.drawTexture(process.getBackground(), 0,
											0);
								} else {
									gl.drawTexture(process.getBackground(),
											process.getX(), process.getY());
								}
								break;
							case Screen.SCREEN_COLOR_REPAINT:
								LColor c = process.getColor();
								if (c != null) {
									gl.drawClear(c);
								}
								break;
							case Screen.SCREEN_CANVAS_REPAINT:
								gl.reset(clear);
								break;
							case Screen.SCREEN_NOT_REPAINT:
								gl.reset(clear);
								break;
							default:
								gl.reset(clear);
								if (process.getX() == 0 && process.getY() == 0) {
									gl.drawTexture(
											process.getBackground(),
											repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode),
											repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode));
								} else {
									gl.drawTexture(
											process.getBackground(),
											process.getX()
													+ repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode),
											process.getY()
													+ repaintMode
													/ 2
													- LSystem.random
															.nextInt(repaintMode));
								}
								break;
							}
							gl.resetFont();

							process.draw(gl);

							process.drawable(elapsedTime);

							if (isFPS) {
								tickFrames();
								fpsFont.drawString("FPS:" + frameRate, 5, 5, 0,
										LColor.white);
							}
							if (isMemory) {
								Runtime runtime = Runtime.getRuntime();
								long totalMemory = runtime.totalMemory();
								long currentMemory = totalMemory
										- runtime.freeMemory();
								String memory = ((float) ((currentMemory * 10) >> 20) / 10)
										+ " of "
										+ ((float) ((totalMemory * 10) >> 20) / 10)
										+ " MB";
								fpsFont.drawString("MEMORY:" + memory, 5, 25,
										0, LColor.white);
							}

							process.drawEmulator(gl);

							process.unload();

						}

					}
					// 刷新游戏画面
					Display.update();

					// 此版将F12设定为全屏
					if (Keyboard.isKeyDown(Keyboard.KEY_F12) && !isFull) {
						isFull = true;
						updateFullScreen();
					} else if (!Keyboard.isKeyDown(Keyboard.KEY_F12)) {
						if (updateScreen) {
							updateFullScreen(updateWidth, updateHeight, true);
							updateScreen = false;
						} else {
							isFull = false;
						}
					}
				}
			}
			process.end();
			destoryView();
		}

		private final void pause(long sleep) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException ex) {
			}
		}

		private void tickFrames() {
			long time = System.currentTimeMillis();
			if (time - frameCount > 1000L) {
				frameRate = Math.min(maxFrames, frames);
				frames = 0;
				frameCount = time;
			}
			frames++;
		}
	}

	public void destoryView() {
		synchronized (this) {
			if (LSystem.screenProcess != null) {
				LSystem.screenProcess.onDestroy();
			}
			if (gl != null) {
				gl.dispose();
			}
			ActionControl.getInstance().stopAll();
			LSystem.destroy();
			LSystem.gc();
			close();
			notifyAll();
		}
	}

	public void showScreen() {
		if (!isRunning) {
			isRunning = true;
			if (mainLoop == null) {
				this.mainLoop = new OpenGLThread();
				this.drawPriority = Thread.NORM_PRIORITY;
				this.mainLoop.setPriority(drawPriority);
				this.mainLoop.start();
			}
		}
	}

	private void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	private void createScreen() {
		try {
			DisplayMode[] ds;
			ds = Display.getAvailableDisplayModes();
			for (int i = 0; i < ds.length; i++) {
				if (ds[i].getWidth() == width && ds[i].getHeight() == height
						&& ds[i].getBitsPerPixel() == 32) {
					displayMode = ds[i];
					break;
				}
			}
			if (displayMode == null) {
				displayMode = new DisplayMode(width, height);
			}

			setIcon(LSystem.FRAMEWORK_IMG_NAME + "icon.png");

			Display.setDisplayMode(displayMode);
			Display.setTitle(windowTitle);
			int samples = 0;
			try {
				Display.create(new PixelFormat(8, 8, 0, samples));
			} catch (Exception e) {
				Display.destroy();
				try {
					Display.create(new PixelFormat(8, 8, 0));
				} catch (Exception ex) {
					Display.destroy();
					try {
						Display.create(new PixelFormat());
					} catch (Exception exc) {
						if (exc.getMessage().contains(
								"Pixel format not accelerated"))
							throw new RuntimeException(
									"not supported by the OpenGL driver.", exc);
					}
				}
			}

			updateScreen();

			boolean support = GLEx.checkVBO();

			if (glMode == GLMode.VBO) {
				if (support) {
					GLEx.setVbo(true);
				} else {
					GLEx.setVbo(false);
					setGLMode(GLMode.Default);
				}
			} else {
				GLEx.setVbo(false);
				setGLMode(GLMode.Default);
			}

			this.gl = new GLEx(width, height);
			this.setViewPort(getBounds());
			this.gl.update();

		} catch (LWJGLException ex) {
			ex.printStackTrace();
		}
	}

	public void setIcon(String path) {
		setIcon(new LImage(path));
	}

	public void setIcon(LImage icon) {
		Display.setIcon(new java.nio.ByteBuffer[] { (java.nio.ByteBuffer) icon
				.getByteBuffer() });
	}

	public boolean isClosed() {
		if (displayMode != null) {
			return Display.isCloseRequested();
		}
		return true;
	}

	public void close() {
		if (displayMode != null) {
			Mouse.destroy();
			Keyboard.destroy();
			Display.destroy();
		}
		LSystem.exit();
	}

	public boolean isActive() {
		if (displayMode != null) {
			return Display.isActive();
		}
		return false;
	}

	public int getScreenWidth() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	public int getScreenHeight() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	public void updateScreen() {
		DisplayMode dm = Display.getDisplayMode();
		int w = dm.getWidth();
		int h = dm.getHeight();
		LSystem.scaleWidth = ((float) w) / width;
		LSystem.scaleHeight = ((float) h) / height;
		this.setBounds(0, 0, w, h);
	}

	public void updateFullScreen() {
		updateFullScreen(getScreenWidth(), getScreenHeight(), true);
	}

	public void updateFullScreen(int w, int h) {
		updateFullScreen(w, h, false);
	}

	private void updateFullScreen(int w, int h, boolean limit) {
		this.fullscreen = !fullscreen;
		if (!fullscreen) {
			try {
				if (limit) {
					if (Display.isFullscreen()) {
						return;
					}
					java.awt.DisplayMode useDisplayMode = ScreenUtils
							.searchFullScreenModeDisplay(w, h);
					if (useDisplayMode == null) {
						return;
					}
				}
				DisplayMode d = new DisplayMode(w, h);
				if (gl != null && !gl.isClose()) {
					gl.setViewPort(0, 0, w, h);
				}
				Display.setDisplayModeAndFullscreen(d);
			} catch (Exception e) {
			}
		} else {
			try {
				if (gl != null && !gl.isClose()) {
					gl.setViewPort(0, 0, displayMode.getWidth(),
							displayMode.getHeight());
				}
				Display.setDisplayMode(displayMode);
			} catch (Exception e) {
			}
		}
		updateScreen();
	}

	public LTexture getLogo() {
		return logo;
	}

	public void setLogo(LTexture img) {
		logo = img;
	}

	public void setLogo(String path) {
		setLogo(LTextures.loadTexture(path));
	}

	public void setShowLogo(boolean showLogo) {
		LSystem.isLogo = showLogo;
	}

	private final String pFontString = " MEORYFPSB0123456789:.of";

	public void setShowFPS(boolean showFps) {
		this.isFPS = showFps;
		if (showFps && fpsFont == null) {
			this.fpsFont = new LSTRFont(LFont.getDefaultFont(), pFontString);
		}
	}

	public void setShowMemory(boolean showMemory) {
		this.isMemory = showMemory;
		if (showMemory && fpsFont == null) {
			this.fpsFont = new LSTRFont(LFont.getDefaultFont(), pFontString);
		}
	}

	public void setClearFrame(boolean clearFrame) {
		this.clear = clearFrame;
	}

	public void setFPS(long frames) {
		this.maxFrames = frames;
	}

	public long getMaxFPS() {
		return this.maxFrames;
	}

	public long getCurrentFPS() {
		return this.frameRate;
	}

	public float getScalex() {
		return LSystem.scaleWidth;
	}

	public float getScaley() {
		return LSystem.scaleHeight;
	}

}
