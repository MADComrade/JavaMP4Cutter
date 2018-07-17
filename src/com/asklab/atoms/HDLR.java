package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HDLR extends Atom {
    private ByteBuffer m_data;
    private TRAK_TYPE m_trakType;

    public HDLR() {
        super(Atom.HDLR_NAME, Atom.HDLR_DIG_NAME);
    }

    public TRAK_TYPE getTrakType(){
        return m_trakType;
    }

    void isTypeTrak(String type){
        if(type.equals(VIDE)){
            m_trakType = TRAK_TYPE.VIDEO;
        }else if(type.equals(SOUN)){
            m_trakType = TRAK_TYPE.AUDIO;
        }else{
            m_trakType = TRAK_TYPE.HINT;
        }
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
        printAtomName(LV4);
        Long pos = startPos;
        m_size = stream.readSizeAtom();
        m_data = ByteBuffer.allocate(Math.toIntExact(m_size));
        stream.readBlock(m_data);
        byte[] tempArray = new byte[4];
        for(int i = 16, j=0; i<20;i++,j++){
            tempArray[j] = m_data.array()[i];
        }
        isTypeTrak(new String(tempArray));
        startPos += m_size;
    }
}
