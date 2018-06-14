package org.m2ci.msp.ema;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.EjmlUnitTests;
import org.ejml.ops.MatrixIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class AG500PosFileTest {

    static final int NUMBER_OF_CHANNELS = 12;

    private static File file;

    private AG500PosFile posFile;

    @BeforeAll
    public static void oneTimeSetup() throws URISyntaxException {
        URI resource = Resources.getResource("ag500.pos").toURI();
        file = new File(resource);
    }

    @BeforeEach
    public void setUp() throws IOException {
        posFile = new AG500PosFile(file);
    }

    @Test
    public void testNumberOfChannels() {
        assertThat(posFile.getNumberOfChannels()).isEqualTo(NUMBER_OF_CHANNELS);
    }

    @Test
    public void testNumberOfFields() {
        assertThat(posFile.getNumberOfFieldsPerFrame()).isEqualTo(7 * NUMBER_OF_CHANNELS);
    }

    @Test
    public void testFrameFieldNames() throws URISyntaxException, IOException {
        ArrayList<String> frameFieldNames = posFile.getFrameFieldNames();
        File txtFile = new File(Resources.getResource("ag500.txt").toURI());
        String headerLine = Files.asCharSource(txtFile, Charsets.US_ASCII).readFirstLine();
        ArrayList<String> headerFields = Lists.newArrayList(headerLine.split("\t"));
        assertThat(frameFieldNames).isEqualTo(headerFields);
    }

    @Test
    public void testSetChannelNames() {
        ArrayList<String> newChannelNames = Lists.newArrayListWithCapacity(NUMBER_OF_CHANNELS);
        for (int c = 1; c <= NUMBER_OF_CHANNELS; c++) {
            String newName = String.format("Ch%d", c);
            newChannelNames.add(newName);
        }
        ArrayList<String> defaultChannelNames = posFile.getChannelNames();
        ArrayList<String> manuallyAssignedDefaultChannelNames = posFile.withChannelNames(newChannelNames).getChannelNames();
        assertThat(manuallyAssignedDefaultChannelNames).isEqualTo(defaultChannelNames);
    }

    @Test
    public void testExtractChannel() throws URISyntaxException, IOException {
        URI resource = Resources.getResource("ag500ch03.csv").toURI();
        DenseMatrix64F channel3 = MatrixIO.loadCSV(resource.getPath());
        DenseMatrix64F extractedChannel = posFile.extractChannel(2).data.getMatrix();
        EjmlUnitTests.assertEquals(extractedChannel, channel3, 0.01);
    }

    @Test
    public void testData() {
        int dataCols = posFile.data.numCols();
        assertThat(dataCols).isEqualTo(84);
        int dataRows = posFile.data.numRows();
        assertThat(dataRows).isEqualTo(10);
        double firstValue = posFile.data.get(0, 0);
        assertThat(firstValue).isEqualTo(1);
        double lastValue = posFile.data.get(9, 83);
        assertThat(lastValue).isEqualTo(840);
    }

    @Test
    public void testSaveTxt() throws IOException, URISyntaxException {
        File tmpFile = File.createTempFile("expected", ".txt");
        posFile.asText().withPrecision(2).writeTo(tmpFile);
        URI resource = Resources.getResource("ag500.txt").toURI();
        File txtFile = new File(resource);
        assertThat(tmpFile).hasSameContentAs(txtFile);
    }

    @Test
    public void testSaveBvh() throws IOException, URISyntaxException {
        File tmpFile = File.createTempFile("expected", ".bvh");
        posFile.asBvh().withPrecision(1).writeTo(tmpFile);
        URI resource = Resources.getResource("ag500.bvh").toURI();
        File bvhFile = new File(resource);
        assertThat(tmpFile).hasSameContentAs(bvhFile);
    }

    @Test
    public void testSavePos() throws IOException, URISyntaxException {
        File tmpFile = File.createTempFile("expected", ".pos");
        posFile.writeTo(tmpFile);
        URI resource = Resources.getResource("ag500.pos").toURI();
        File posFile = new File(resource);
        byte[] expected = Files.asByteSource(posFile).read();
        byte[] actual = Files.asByteSource(tmpFile).read();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testTimeExtraction() {
        double xmin = 0.004;
        double xmax = 0.02;
        AG500PosFile segment = (AG500PosFile) posFile.extractTimeRange(xmin, xmax);
        assertThat(segment.getFirstSampleTime()).isGreaterThanOrEqualTo(xmin);
        assertThat(segment.getLastSampleTime()).isLessThanOrEqualTo(xmax);
    }
}
