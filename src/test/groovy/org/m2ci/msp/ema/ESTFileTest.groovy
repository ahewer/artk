package org.m2ci.msp.ema

import org.ejml.simple.SimpleMatrix
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.m2ci.msp.ema.est.ESTParser

class ESTFileTest {

    File estBinaryFile
    File posBinaryFile

    File unpackTestFile(String resourceName) {
        def tmpFile = File.createTempFile(resourceName, '.tmp')
        tmpFile.deleteOnExit()
        def stream = getClass().getResourceAsStream(resourceName)
        assert stream?.available()
        tmpFile.withOutputStream {
            it << stream.bytes
        }
        return tmpFile
    }

    @BeforeEach
    void unpackTestFiles() {
        estBinaryFile = unpackTestFile('/esttrack.bin')
        posBinaryFile = unpackTestFile('/ag500.pos')
    }

    @Test
    void testLoadFromBinaryChannelNames() {
        def posFile = new AG500PosFile(posBinaryFile)
        def estFile = new ESTParser().parse(estBinaryFile.path)
        def actual = estFile.data.channelMap.keySet().collect()
        def expected = ['timeStamp'] + posFile.channelNames
        assert actual == expected
    }

    @Test
    void testLoadFromBinaryData() {
        def posFile = new AG500PosFile(posBinaryFile)
        def estFile = new ESTParser().parse(estBinaryFile.path)
        def actual = estFile.data.channelMap.Ch1.theta
        def ch1 = posFile.getChannelIndex('Ch1')
        def theta = 4
        def expected = posFile.data.extractMatrix(0, SimpleMatrix.END, ch1 + theta, ch1 + theta + 1).matrix.data
        assert actual == expected
    }
}
