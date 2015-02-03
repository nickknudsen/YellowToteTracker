package com.alexkafer;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ToteAngle {
	
	final static double CAMERA_FIELD_OF_VIEW = 68.5d;
	final static int HUE = 87;
	final static int SATU = 112;
	final static int VALU = 147;
	final static int TOLER = 27;
	
	public static MatOfPoint getNearestTote(Mat original) {
		
		Mat image = new Mat();
		Imgproc.cvtColor(original, image, Imgproc.COLOR_RGB2HSV);
		
		Core.inRange(image, 
				new Scalar(Math.max(HUE-TOLER, 0), Math.max(SATU-TOLER, 0), Math.max(VALU-TOLER, 0)),
				new Scalar(Math.min(HUE+TOLER, 179), Math.min(SATU, 255), Math.min(VALU, 255)),
				image);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		MatOfPoint largest = null;
		for (MatOfPoint contour : contours)
			if(largest == null || contour.width() * contour.height() > largest.width() * largest.height())
				largest = contour;
		
		return largest;
	}

	public static double getAngleOfTote(Mat image, MatOfPoint tote) {
		
		Rect yellowTote = Imgproc.boundingRect(tote);
		Point midTote =  new Point((yellowTote.tl().x + yellowTote.br().x) / 2, (yellowTote.tl().y + yellowTote.br().y)/2);
		
		double angularDifference = ((midTote.x - image.width() / 2)/yellowTote.width)*CAMERA_FIELD_OF_VIEW;
		
		return angularDifference;
	}

}
