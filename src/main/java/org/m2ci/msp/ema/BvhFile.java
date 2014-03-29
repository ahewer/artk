package org.m2ci.msp.ema;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import org.ejml.simple.SimpleMatrix;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class BvhFile extends TextFile {

	protected int numberOfChannels;
	float samplingFrequency;

	public BvhFile(SimpleMatrix data) {
		super(data);
		numberOfChannels = data.numCols() / getNumberOfFieldsPerChannel();
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public static int getNumberOfFieldsPerChannel() {
		return Fields.values().length;
	}

	public ArrayList<String> getFieldNames() {
		ArrayList<String> names = Lists.newArrayListWithCapacity(getNumberOfFieldsPerChannel());
		for (Fields field : Fields.values()) {
			names.add(field.toString());
		}
		return names;
	}

	public int getNumberOfFrames() {
		return data.numRows();
	}

	// fluent setters

	public BvhFile withSamplingFrequency(float newSamplingFrequency) {
		this.samplingFrequency = newSamplingFrequency;
		return this;
	}

	public BvhFile withChannelNames(ArrayList<String> newChannelNames) {
		if (channelNames == null || channelNames.isEmpty()) {
			channelNames = newChannelNames;
			return this;
		}
		if (newChannelNames.size() != getNumberOfChannels()) {
			throw new IllegalArgumentException(String.format("Expected %d channel names, but got %d", getNumberOfChannels(),
					newChannelNames.size()));
		}
		Collections.copy(channelNames, newChannelNames);
		return this;
	}

	// output

	@Override
	protected void writeHeader(Writer bvh) throws IOException {
		bvh.append("HIERARCHY\n");
		for (int c = 0; c < getNumberOfChannels(); c++) {
			bvh.append(String.format("ROOT %s\n", channelNames.get(c))).append("{\n");
			bvh.append(String.format("\tOFFSET\t%.0f\t%.0f\t%.0f\n", 0f, 0f, 0f));
			bvh.append(String.format("\tCHANNELS %d ", getNumberOfFieldsPerChannel()));
			Joiner.on(" ").appendTo(bvh, getFieldNames()).append("\n");
			bvh.append("\tEnd Site\n").append("\t{\n");
			bvh.append(String.format("\t\tOFFSET\t%.0f\t%.0f\t%.0f\n", 0f, 0f, -1f)).append("\t}\n").append("}\n");
		}
		bvh.append("MOTION\n").append(String.format("Frames:\t%d\n", getNumberOfFrames()));
		bvh.append(String.format("Frame Time:\t%f\n", 1 / samplingFrequency));
	};

	private enum Fields {
		XPOSITION {
			public String toString() {
				return "XPosition";
			}
		},
		YPOSITION {
			public String toString() {
				return "YPosition";
			}
		},
		ZPOSITION {
			public String toString() {
				return "ZPosition";
			}
		},
		XROTATION {
			public String toString() {
				return "XRotation";
			}
		},
		YROTATION {
			public String toString() {
				return "YRotation";
			}
		},
		ZROTATION {
			public String toString() {
				return "ZRotation";
			}
		}
	}
}