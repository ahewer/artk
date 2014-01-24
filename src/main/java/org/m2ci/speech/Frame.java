package org.m2ci.speech;

import cern.colt.matrix.tfloat.FloatMatrix1D;
import cern.colt.matrix.tfloat.impl.DenseFloatMatrix1D;
import cern.colt.matrix.tobject.ObjectMatrix1D;

public class Frame extends EmaData {
	private FloatMatrix1D samples;

	public Frame(ObjectMatrix1D names, FloatMatrix1D samples) {
		this.names = names;
		this.samples = samples;
	}

	public Frame(int frameIndex, ObjectMatrix1D names, FloatMatrix1D samples) {
		this(names, samples);
	}

	public String toString() {
		String string = String.format("Name: %s\nSamples: %s", names, samples);
		return string;
	}

	public float getX() {
		return samples.get(0);
	}

	public void setX(float x) {
		samples.set(0, x);
	}

	public float getY() {
		return samples.get(1);
	}

	public void setY(float y) {
		samples.set(1, y);
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

	public void setPhi(float phi) {
		samples.set(3, phi);
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

	public void setTheta(float theta) {
		samples.set(4, theta);
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

	/**
	 * Get the RMS error
	 * 
	 * @return
	 */
	public float getRMS() {
		float rms = samples.get(5);
		return rms;
	}

	public Frame difference(Frame other) {
		assert this.names.equals(other.names);

		float x = other.getX() - this.getX();
		float y = other.getY() - this.getY();
		float z = other.getZ() - this.getZ();
		float phi = other.getPhi() - this.getPhi();
		float theta = other.getTheta() - this.getTheta();

		float[] values = new float[] { x, y, z, phi, theta };
		DenseFloatMatrix1D matrix = new DenseFloatMatrix1D(values);

		Frame frame = new Frame(names, matrix);
		return frame;
	}

}
