/**
 * 
 * Copyright 2008 - 2010
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
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.component;

import loon.LObject;
import loon.LRelease;
import loon.LTexture;
import loon.LTextures;
import loon.action.ActionBind;
import loon.action.ArrowTo;
import loon.action.CircleTo;
import loon.action.FadeTo;
import loon.action.FireTo;
import loon.action.JumpTo;
import loon.action.MoveTo;
import loon.action.RotateTo;
import loon.action.ScaleTo;
import loon.action.map.Field2D;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

public class Actor extends LObject<Actor> implements Comparable<Actor>, ActionBind, XY, LRelease, BoxSize {

	private String flag = "Actor";

	private static int sequenceNumber = 0;

	private int noSequenceNumber;

	private int lastPaintSequenceNumber;

	boolean visible = true, drag = true, click = true;

	private ActorLayer gameLayer;

	private LTexture image;

	Object data;

	private RectBox boundingRect;

	private float[] xs = new float[4];

	private float[] ys = new float[4];

	private LTimer timer = new LTimer(0);

	private Animation animation;

	boolean isAnimation,mirror;

	LColor filterColor;

	ActorListener actorListener;

	float scaleX = 1, scaleY = 1;

	public Actor(Animation animation) {
		this(animation, 0, 0);
	}

	public Actor(Animation animation, int x, int y) {
		if (animation == null) {
			throw new RuntimeException("Animation is null !");
		}
		this.noSequenceNumber = sequenceNumber++;
		this.animation = animation;
		this.isAnimation = true;
		this._location.set(x, y);
		this.setImage(animation.getSpriteImage());
	}

	public Actor() {
		this((LTexture) null);
	}

	public Actor(LTexture image, int x, int y) {
		this.noSequenceNumber = sequenceNumber++;
		this._location.set(x, y);
		this.setImage(image);
	}

	public Actor(LTexture image) {
		this(image, 0, 0);
	}

	public Actor(String fileName, int x, int y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public Actor(String fileName) {
		this(fileName, 0, 0);
	}

	public void stopAnimation() {
		this.isAnimation = false;
	}

	public void startAnimation() {
		this.isAnimation = true;
	}

	protected void setSize(int w, int h) {
		if (boundingRect != null) {
			boundingRect.setBounds(_location.x, _location.y, w, h);
		} else {
			boundingRect = new RectBox(_location.x, _location.y, w, h);
		}
	}

	/**
	 * 移动当前角色到指定位置并返回MoveTo控制器(flag为true时八方向行走，否则为四方向)
	 * 
	 * @param x
	 * @param y
	 */
	public MoveTo moveTo(int x, int y) {
		failIfNotInLayer();
		return gameLayer.callMoveTo(this, x, y);
	}

	/**
	 * 移动当前角色到指定位置并返回MoveTo控制器(flag为true时八方向行走，否则为四方向)
	 * 
	 * @param x
	 * @param y
	 * @param flag
	 * @return
	 */
	public MoveTo moveTo(int x, int y, boolean flag) {
		failIfNotInLayer();
		return gameLayer.callMoveTo(this, x, y, flag);
	}

	/**
	 * 命令当前角色执行淡出操作
	 * 
	 * @return
	 */
	public FadeTo fadeOut() {
		failIfNotInLayer();
		return gameLayer.callFadeOutTo(this, 60);
	}

	/**
	 * 命令当前角色执行淡入操作
	 * 
	 * @return
	 */
	public FadeTo fadeIn() {
		failIfNotInLayer();
		return gameLayer.callFadeInTo(this, 60);
	}

	/**
	 * 以指定速度渐进式旋转当前角色到指定角度
	 * 
	 * @param rotate
	 * @param speed
	 * @return
	 */
	public RotateTo rotateTo(float rotate, float speed) {
		failIfNotInLayer();
		return gameLayer.callRotateTo(this, rotate, speed);
	}

	/**
	 * 渐进式旋转当前角色到指定角度
	 * 
	 * @param rotate
	 * @return
	 */
	public RotateTo rotateTo(float rotate) {
		return rotateTo(rotate, 2f);
	}

	/**
	 * 以指定加速度指定重力跳跃当前角色
	 * 
	 * @param jump
	 * @param g
	 * @return
	 */
	public JumpTo jumpTo(int jump, float g) {
		failIfNotInLayer();
		return gameLayer.callJumpTo(this, -jump, g);
	}

	/**
	 * 以指定加速度跳跃当前角色
	 * 
	 * @param jump
	 * @return
	 */
	public JumpTo jumpTo(int jump) {
		return jumpTo(jump, 0.3f);
	}

	/**
	 * 让指定角色根据指定半径以指定速度循环转动
	 * 
	 * @param radius
	 * @param velocity
	 * @return
	 */
	public CircleTo circleTo(int radius, int velocity) {
		failIfNotInLayer();
		return gameLayer.callCircleTo(this, radius, velocity);
	}

	/**
	 * 将当前角色作为子弹以指定速度向指定坐标发射
	 * 
	 * @param endX
	 * @param endY
	 * @param speed
	 * @return
	 */
	public FireTo fireTo(float endX, float endY, float speed) {
		failIfNotInLayer();
		return gameLayer.callFireTo(this, endX, endY, speed);
	}

	/**
	 * 将当前角色向指定坐标发射
	 * 
	 * @param endX
	 * @param endY
	 * @return
	 */
	public FireTo fireTo(float endX, float endY) {
		return fireTo(endX, endY, 10);
	}

	/**
	 * 让当前角色缩放指定大小
	 * 
	 * @param sx
	 * @param sy
	 * @return
	 */
	public ScaleTo scaleTo(float sx, float sy) {
		failIfNotInLayer();
		return gameLayer.callScaleTo(this, sx, sy);
	}

	/**
	 * 让当前角色缩放指定大小
	 * 
	 * @param sx
	 * @param sy
	 * @return
	 */
	public ScaleTo scaleTo(float s) {
		failIfNotInLayer();
		return gameLayer.callScaleTo(this, s, s);
	}

	/**
	 * 让指定角色做箭状发射(抛物线)
	 * 
	 * @param tx
	 * @param ty
	 * @return
	 */
	public ArrowTo arrowTo(float tx, float ty) {
		failIfNotInLayer();
		return gameLayer.callArrowTo(this, tx, ty);
	}

	/**
	 * 删除所有以当前Actor注册的动作事件
	 * 
	 */
	public void removeActionEvents() {
		failIfNotInLayer();
		removeActionEvents(this);
	}

	/**
	 * 缩放当前角色
	 * 
	 * @param scale
	 */
	public void setScale(final float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(final float sx, final float sy) {
		if (this.scaleX == sx && this.scaleY == sy) {
			return;
		}
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	/**
	 * 按下
	 * 
	 */
	public void downClick(int x, int y) {

	}

	/**
	 * 放开
	 * 
	 */
	public void upClick(int x, int y) {

	}

	/**
	 * 键盘按下
	 * 
	 */
	public void downKey() {

	}

	/**
	 * 键盘放开
	 * 
	 */
	public void upKey() {

	}

	/**
	 * 拖拽
	 * 
	 */
	public void drag(int x, int y) {

	}

	/**
	 * 动作处理(内部传参)
	 * 
	 */
	@Override
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			if (isAnimation) {
				if (animation != null) {
					animation.update(elapsedTime);
					setImage(animation.getSpriteImage());
				}
			}
			synchronized (LLayer.class) {
				action(elapsedTime);
			}
		}
	}

	/**
	 * 设定动作触发延迟时间
	 * 
	 * @param delay
	 */
	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	/**
	 * 返回动作触发延迟时间
	 * 
	 * @return
	 */
	public long getDelay() {
		return timer.getDelay();
	}

	/**
	 * 动作处理
	 * 
	 * @param elapsedTime
	 */
	public void action(long elapsedTime) {

	}

	@Override
	public int x() {
		return _location.x();
	}

	@Override
	public int y() {
		return _location.y();
	}

	@Override
	public float getX() {
		return _location.x;
	}

	@Override
	public float getY() {
		return _location.y;
	}

	@Override
	public float getRotation() {
		return this._rotation;
	}

	/**
	 * 决定当前对象旋转方向
	 * 
	 * @param _rotation
	 */
	@Override
	public void setRotation(float r) {
		if (r >= 360) {
			if (r < 720) {
				r -= 360;
			} else {
				r %= 360;
			}
		} else if (r < 0) {
			if (r >= -360) {
				r += 360;
			} else {
				r = 360 + r % 360;
			}
		}
		if (this._rotation != r) {
			this._rotation = r;
			this.boundingRect = null;
			this.sizeChanged();
		}
	}

	public int width() {
		if (image != null) {
			return (int) (image.getWidth() * scaleX);
		}
		return (int) (getRectBox().width * scaleX);
	}

	public int height() {
		if (image != null) {
			return (int) (image.getHeight() * scaleY);
		}
		return (int) (getRectBox().height * scaleY);
	}

	@Override
	public float getWidth() {
		return width();
	}

	@Override
	public float getHeight() {
		return height();
	}

	/**
	 * 根据旋转方向移动坐标
	 * 
	 * @param distance
	 */
	public void move(float distance) {
		float angle = MathUtils.toRadians(getRotation());
		int x = MathUtils.round(getX() + MathUtils.cos(angle) * distance);
		int y = MathUtils.round(getY() + MathUtils.sin(angle) * distance);
		setLocation(x, y);
	}

	@Override
	public void move(Vector2f v) {
		move(v.x, v.y);
	}

	public void move(int x, int y) {
		move(x, y);
	}

	@Override
	public void move(float x, float y) {
		setLocationDrag(_location.getX() + x, _location.getY() + y);
	}

	public void setX(int x) {
		this.setLocationDrag(x, y());
	}

	public void setY(int y) {
		this.setLocationDrag(x(), y);
	}

	@Override
	public void setX(float x) {
		this.setLocation(x, getY());
	}

	@Override
	public void setY(float y) {
		this.setLocation(getX(), y);
	}

	@Override
	public void setX(Integer x) {
		setX(x.intValue());
	}

	@Override
	public void setY(Integer y) {
		setY(y.intValue());
	}

	public void setLocation(int x, int y) {
		this.setLocationDrag(x, y);
	}

	@Override
	public void setLocation(float x, float y) {
		setLocationDrag(x, y);
	}

	private void setLocationDrag(float x, float y) {
		this.failIfNotInLayer();
		float oldX = _location.getX();
		float oldY = _location.getY();
		if (this.gameLayer.isBounded()) {
			_location.x = this.limitValue(x, this.gameLayer.getWidth()
					- getWidth());
			_location.y = this.limitValue(y, this.gameLayer.getHeight()
					- getHeight());
		} else {
			_location.x = x;
			_location.y = y;
		}
		if (_location.x != oldX || _location.y != oldY) {
			if (this.boundingRect != null) {
				float dx = (_location.getX() - oldX) * this.gameLayer.cellSize;
				float dy = (_location.getY() - oldY) * this.gameLayer.cellSize;
				this.boundingRect.setX(this.boundingRect.getX() + dx);
				this.boundingRect.setY(this.boundingRect.getY() + dy);
				for (int i = 0; i < 4; ++i) {
					this.xs[i] += dx;
					this.ys[i] += dy;
				}
			}
			this.locationChanged(oldX, oldY);
		}
	}

	private float limitValue(float v, float limit) {
		if (v < 0) {
			v = 0;
		}
		if (limit < v) {
			v = limit;
		}
		return v;
	}

	public ActorLayer getLLayer() {
		return this.gameLayer;
	}

	protected void addLayer(ActorLayer gameLayer) {
	}

	public LTexture getImage() {
		return this.image;
	}

	public void setImage(String filename) {
		this.setImage(LTextures.loadTexture(filename));
	}

	public void setImage(LTexture img) {
		if (img != null || this.image != null) {
			boolean sizeChanged = true;
			if (img != null && this.image != null
					&& img.getWidth() == this.image.getWidth()
					&& img.getHeight() == this.image.getHeight()) {
				sizeChanged = false;
			}
			if (image != null && image.getParent() == null
					&& image.isChildAllClose()) {
				if (image != null) {
					image.close();
					image = null;
				}
			}
			this.image = img;
			if (sizeChanged) {
				this.boundingRect = null;
				this.sizeChanged();
			}
		}
	}

	public void setLocationInPixels(float x, float y) {
		float xCell = this.gameLayer.toCellFloor(x);
		float yCell = this.gameLayer.toCellFloor(y);
		if (xCell != _location.x || yCell != _location.y) {
			this.setLocationDrag(xCell, yCell);
		}
	}

	void setLayer(ActorLayer gameLayer) {
		this.gameLayer = gameLayer;
	}

	void addLayer(float x, float y, ActorLayer gameLayer) {
		if (gameLayer.isBounded()) {
			x = this.limitValue(x, gameLayer.getWidth() - getWidth());
			y = this.limitValue(y, gameLayer.getHeight() - getHeight());
		}
		this.boundingRect = null;
		this.setLayer(gameLayer);
		this.setLocation(x, y);
	}

	/**
	 * 获得当前Actor碰撞盒
	 * 
	 * @return
	 */
	@Override
	public RectBox getRectBox() {
		RectBox tmp = getBoundingRect();
		if (tmp == null) {
			return getRect(_location.x, _location.y, getWidth() * scaleX,
					getHeight() * scaleY);
		}
		return getRect(_location.x, _location.y, tmp.width * scaleX, tmp.height
				* scaleY);
	}

	/**
	 * 获得当前Actor碰撞盒(内部使用)
	 * 
	 * @return
	 */
	RectBox getBoundingRect() {
		if (this.boundingRect == null) {
			this.calcBounds();
		}
		return this.boundingRect;
	}

	/**
	 * 绘图接口，用以绘制额外的图形到Actor
	 * 
	 * @param g
	 */
	public void draw(GLEx g) {

	}

	public boolean isConsumerDrawing = true;

	/**
	 * 矫正当前图像大小
	 * 
	 */
	private void calcBounds() {
		ActorLayer layer = this.getLLayer();
		if (layer != null) {
			int width;
			int height;
			int cellSize = layer.getCellSize();
			int minY = 0;
			if (this.image == null) {
				width = _location.x() * cellSize + cellSize;
				height = _location.y() * cellSize + cellSize;
				this.boundingRect = new RectBox(width, height, 0, 0);
				for (minY = 0; minY < 4; ++minY) {
					this.xs[minY] = width;
					this.ys[minY] = height;
				}
			} else {
				this.boundingRect = MathUtils.getBounds(_location.x,
						_location.y, this.image.getWidth(),
						this.image.getHeight(), _rotation);
			}
		}
	}

	public RectBox getRandLocation() {
		if (gameLayer != null) {
			return gameLayer.getRandomLayerLocation(this);
		}
		return null;
	}

	public void sendToFront() {
		if (gameLayer != null) {
			gameLayer.sendToFront(this);
		}
	}

	public void sendToBack() {
		if (gameLayer != null) {
			gameLayer.sendToBack(this);
		}
	}

	private float[] pos = new float[2];

	public float[] toPixels() {
		float size = gameLayer.cellSize / 2;
		pos[0] = _location.x * gameLayer.cellSize + size;
		pos[1] = _location.y * gameLayer.cellSize + size;
		return pos;
	}

	private void sizeChanged() {
		if (this.gameLayer != null) {
			this.gameLayer.updateObjectSize(this);
		}
	}

	private void locationChanged(float oldX, float oldY) {
		if (this.gameLayer != null) {
			this.gameLayer.updateObjectLocation(this, oldX, oldY);
		}
	}

	private void failIfNotInLayer() {
		if (this.gameLayer == null) {
			throw new IllegalStateException(
					"The actor has not been inserted into a Layer so it has no _location yet !");
		}
	}

	private static boolean checkOutside(float[] myX, float[] myY,
			float[] otherX, float[] otherY) {
		for (int v = 0; v < 4; ++v) {
			int v1 = v + 1 & 3;
			float edgeX = myX[v] - myX[v1];
			float edgeY = myY[v] - myY[v1];
			float reX = -edgeY;
			float reY = edgeX;
			if (reX != 0 || edgeX != 0) {
				for (int e = 0; e < 4; ++e) {
					float scalar = reX * (otherX[e] - myX[v1]) + reY
							* (otherY[e] - myY[v1]);
					if (scalar < 0) {
						continue;
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean intersects(Actor other) {
		int thisBounds1;
		if (this.image == null) {
			if (other.image != null) {
				thisBounds1 = this.gameLayer.getCellSize();
				return other.containsPoint(_location.x() * thisBounds1
						+ thisBounds1 / 2, _location.y() * thisBounds1
						+ thisBounds1 / 2);
			} else {
				return _location.x == other._location.x
						&& _location.y == other._location.y;
			}
		} else if (other.image == null) {
			thisBounds1 = this.gameLayer.getCellSize();
			return this.containsPoint(other._location.x() * thisBounds1
					+ thisBounds1 / 2, other._location.y() * thisBounds1
					+ thisBounds1 / 2);
		} else {
			RectBox thisBounds = this.getBoundingRect();
			RectBox otherBounds = other.getBoundingRect();
			if (this._rotation == 0 && other._rotation == 0) {
				return thisBounds.intersects(otherBounds);
			} else if (!thisBounds.intersects(otherBounds)) {
				return false;
			} else {
				float[] myX = this.xs;
				float[] myY = this.ys;
				float[] otherX = other.xs;
				float[] otherY = other.ys;
				return checkOutside(myX, myY, otherX, otherY) ? false
						: !checkOutside(otherX, otherY, myX, myY);
			}
		}
	}

	public TArray<Actor> getNeighbours(float distance, boolean diagonal,
			String flag) {
		this.failIfNotInLayer();
		return this.getLLayer().getNeighbours(this, distance, diagonal, flag);
	}

	public TArray<Actor> getCollisionObjects(float dx, float dy, String flag) {
		this.failIfNotInLayer();
		return this.gameLayer.getCollisionObjectsAt(_location.x + dx,
				_location.y + dy, flag);
	}

	public Actor getOnlyCollisionObject(float dx, float dy, String flag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyObjectAt(this, _location.x + dx,
				_location.y + dy, flag);
	}

	public TArray<Actor> getCollisionObjects(float radius, String flag) {
		this.failIfNotInLayer();
		TArray<Actor> inRange = this.gameLayer.getObjectsInRange(_location.x,
				_location.y, radius, flag);
		inRange.remove(this);
		return inRange;
	}

	public TArray<Actor> getCollisionObjects() {
		return getCollisionObjects(flag);
	}

	public TArray<Actor> getCollisionObjects(String flag) {
		this.failIfNotInLayer();
		TArray<Actor> list = this.gameLayer.getIntersectingObjects(this, flag);
		list.remove(this);
		return list;
	}

	public Actor getOnlyCollisionObject() {
		return getOnlyCollisionObject(flag);
	}

	public Actor getOnlyCollisionObject(String flag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyIntersectingObject(this, flag);
	}

	public Actor getOnlyCollisionObjectAt(float x, float y) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyCollisionObjectsAt(x, y);
	}

	public Actor getOnlyCollisionObjectAt(float x, float y, Object tag) {
		this.failIfNotInLayer();
		return this.gameLayer.getOnlyCollisionObjectsAt(x, y, tag);
	}

	public boolean containsPoint(float px, float py) {
		this.failIfNotInLayer();
		if (this.image == null) {
			return false;
		} else {
			if (this.boundingRect == null) {
				this.calcBounds();
			}
			if (this._rotation != 0 && this._rotation != 90
					&& this._rotation != 270) {
				for (int v = 0; v < 4; ++v) {
					int v1 = v + 1 & 3;
					float edgeX = this.xs[v] - this.xs[v1];
					float edgeY = this.ys[v] - this.ys[v1];
					float reX = -edgeY;
					if (reX != 0 || edgeX != 0) {
						float scalar = reX * (px - this.xs[v1]) + edgeX
								* (py - this.ys[v1]);
						if (scalar >= 0) {
							return false;
						}
					}
				}

				return true;
			} else {
				return px >= this.boundingRect.getX()
						&& px < this.boundingRect.getRight()
						&& py >= this.boundingRect.getY()
						&& py < this.boundingRect.getBottom();
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDrag() {
		return drag;
	}

	public void setDrag(boolean drag) {
		this.drag = drag;
	}

	public boolean isClick() {
		return click;
	}

	public void setClick(boolean click) {
		this.click = click;
	}

	final void setLastPaintSeqNum(int num) {
		this.lastPaintSequenceNumber = num;
	}

	final public int getSequenceNumber() {
		return this.noSequenceNumber;
	}

	final public int getLastPaintSeqNum() {
		return this.lastPaintSequenceNumber;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		if (animation == null) {
			throw new RuntimeException("Animation is null !");
		}
		this.animation = animation;
		this.isAnimation = true;
		this.setImage(animation.getSpriteImage());
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public void setAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}

	@Override
	public void close() {
		if (image != null) {
			image.close();
			image = null;
		}
		if (animation != null) {
			animation.close();
			animation = null;
		}
	}

	@Override
	public Field2D getField2D() {
		return gameLayer.getField2D();
	}

	@Override
	public boolean isBounded() {
		return gameLayer.isBounded();
	}

	@Override
	public boolean isContainer() {
		return gameLayer.isContainer();
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return gameLayer.contains(x, y, w, h);
	}

	@Override
	public float getContainerWidth() {
		return gameLayer.getWidth();
	}

	@Override
	public float getContainerHeight() {
		return gameLayer.getHeight();
	}
	
	public boolean isMirror(){
		return mirror;
	}
	
	public void setMirror(boolean m){
		this.mirror = m;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public void setWidth(float w) {
		this.scaleX = w / getWidth();
	}

	@Override
	public void setHeight(float h) {
		this.scaleY = h / getHeight();
	}

	@Override
	public int compareTo(Actor o) {
		return o.getLayer() - this.getLayer();
	}

}
