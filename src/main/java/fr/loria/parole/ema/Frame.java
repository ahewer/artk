package fr.loria.parole.ema;

import cern.colt.matrix.tfloat.FloatMatrix1D;
import cern.colt.matrix.tobject.ObjectMatrix1D;

public class Frame extends EmaData {
	private FloatMatrix1D samples;
	private int index;

	public Frame(ObjectMatrix1D names, FloatMatrix1D samples) {
		this.names = names;
		this.samples = samples;
	}

	public Frame(int frameIndex, ObjectMatrix1D names, FloatMatrix1D samples) {
		this(names, samples);
		this.index = frameIndex;
	}

	public String toString() {
		String string = String.format("Name: %s\nSamples: %s", names, samples);
		return string;
	}

	public float getX() {
		return samples.get(0);
	}

	public float getY() {
		return samples.get(1);
	}

	public float getZ() {
		return samples.get(2);
	}

	/**
	 * Get the horizontal angle (yaw) in degrees
	 * 
	 * @return
	 */
	public float getPhi() {
		return samples.get(3);
	}

	/**
	 * Get the horizontal angle (yaw) in radians
	 * 
	 * @return
	 */
	public float getPhiRadians() {
		float rads = (float) Math.toRadians(getPhi());
		return rads;
	}

	/**
	 * Get the vertical angle (pitch) in degrees
	 * 
	 * @return
	 */
	public float getTheta() {
		return samples.get(4);
	}

	/**
	 * Get the vertical angle (pitch) in radians
	 * 
	 * @return
	 */
	public float getThetaRadians() {
		float rads = (float) Math.toRadians(getTheta());
		return rads;
	}

}
