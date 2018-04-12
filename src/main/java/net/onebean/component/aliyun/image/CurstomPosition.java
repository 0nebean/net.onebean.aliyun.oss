package net.onebean.component.aliyun.image;

import net.coobird.thumbnailator.geometry.Position;

import java.awt.*;

public class CurstomPosition implements Position {
	
	public CurstomPosition(){}
	public CurstomPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}


	@Override
	public Point calculate(int enclosingWidth, int enclosingHeight, int width,
                           int height, int insetLeft, int insetRight, int insetTop,
                           int insetBottom) {
		return new Point(this.x, this.y);
	}

}
