package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;

/*
 * This class is a rotatable polygon.
 */

@SuppressWarnings("serial")
public class RotaPoly extends Polygon {

	//RotaPoly stands for Rotatable Polygon.
	
	private Tank body;
	
	public RotaPoly(Tank body_, int[] xpoints_, int[] ypoints_) {
		body = body_;
		xpoints = xpoints_;
		ypoints = ypoints_;
		npoints = xpoints.length;
	}
	
	public void recenterPoly() {
		
	}

	public void draw(Graphics2D g, Color color) {
		// We do fancy translation stuff.
		g.setColor(color);
		double[] tempX = new double[npoints+1];
		double[] tempY = new double[npoints+1];
		double sin = Math.sin(body.getRot());
		double cos = Math.cos(body.getRot());
		for (int i = 0; i < npoints; i++) {
			tempX[i] = cos * (double) xpoints[i] - sin * (double) ypoints[i] + body.getX();
			tempY[i] = sin * (double) xpoints[i] + cos * (double) ypoints[i] + body.getY();
		}
		tempX[npoints] = tempX[0];
		tempY[npoints] = tempY[0];
		
		for (int i = 0; i < npoints; i++) {
			g.draw(new Line2D.Double(tempX[i], tempY[i], tempX[i+1], tempY[i+1]));
		}
	}
}
