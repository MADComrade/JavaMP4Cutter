package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DINF extends Atom {
    private ByteBuffer m_data=null;

    public DINF() {
        super(Atom.DINF_NAME, Atom.DINF_DIG_NAME);
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeBlock(m_data.array());
    }

    @Override
    public void prepareData() {

    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {

    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        printAtomName(Atom.LV5);
        m_size = stream.readSizeAtom();
        m_data = ByteBuffer.allocate(Math.toIntExact(m_size));
        stream.readBlock(m_data);
        startPos += m_size;
    }
}
