package org.m2ci.msp.ema;

import java.util.ArrayList;

import org.ejml.data.MatrixIterator;
import org.ejml.simple.SimpleMatrix;

import com.google.common.collect.Lists;

public abstract class EmaFile {

	SimpleMatrix data;
	protected int numberOfChannels;
	protected ArrayList<String> channelNames;

	SimpleMatrix times;
	double timeOffset = 0;
	double samplingFrequency = 1;

	// channels

	protected void initChannelNames() {
		ArrayList<String> names = Lists.newArrayListWithCapacity(getNumberOfChannels());
		for (int c = 1; c <= getNumberOfChannels(); c++) {
			names.add("Ch" + c);
		}
		setChannelNames(names);
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public ArrayList<String> getChannelNames() {
		return channelNames;
	}

	protected int getChannelIndex(String channelName) {
		int channelIndex = getChannelNames().indexOf(channelName);
		if (channelIndex < 0) {
			throw new IllegalArgumentException(String.format("No channel named %s can be found.", channelName));
		}
		return channelIndex;
	}

	public void setChannelNames(ArrayList<String> newChannelNames) {
		if (newChannelNames.size() != getNumberOfChannels()) {
			throw new IllegalArgumentException(String.format("Expected %d channel names, but got %d", getNumberOfChannels(),
					newChannelNames.size()));
		}
		channelNames = Lists.newArrayList(newChannelNames);
	}

	public int getNumberOfFrames() {
		return data.numRows();
	}

	abstract public void setData(SimpleMatrix newData);

	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(double newSamplingFrequency) {
		samplingFrequency = newSamplingFrequency;
	}

	public double getTimeOffset() {
		return timeOffset;
	}

	public void setTimeOffset(double newTimeOffset) {
		timeOffset = newTimeOffset;
	}

	protected void updateTimes() {
		int numFrames = getNumberOfFrames();
		times = new SimpleMatrix(numFrames, 1);
		MatrixIterator iterator = times.iterator(true, 0, 0, numFrames - 1, 0);
		double time = 0.5 / getSamplingFrequency() + getTimeOffset();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.set(time);
			time += 1 / getSamplingFrequency();
		}
	}
}
