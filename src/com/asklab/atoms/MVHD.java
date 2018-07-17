package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MVHD extends Atom {

    private ByteBuffer m_data=null;
    private Integer m_timeScale;
    private Integer m_duration;
    private Integer m_verFlag;
    private Integer m_creationTime;
    private Integer m_modificationTime;
    private Integer m_nextTrackID;

    public MVHD() {
        super(Atom.MVHD_NAME, Atom.MVHD_DIG_NAME);
        m_data = ByteBuffer.allocate(76);
    }


    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(MVHD_NAME);
        stream.writeValueUINT32(m_verFlag);
        stream.writeValueUINT32(m_creationTime);
        stream.writeValueUINT32(m_modificationTime);
        stream.writeValueUINT32(m_timeScale);
        stream.writeValueUINT32(m_duration);
        stream.writeBlock(m_data.array());
        stream.writeValueUINT32(m_nextTrackID);
    }

    @Override
    public void prepareData() {

    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {

    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        printAtomName(LV2);
        Long pos = startPos;
        stream.setPos(pos);
        m_size = stream.readSizeAtom();
        pos+=OFFSET_TITLE;
        stream.setPos(pos);
        m_verFlag = stream.readValueUINT32();
        m_creationTime = stream.readValueUINT32();
        m_modificationTime = stream.readValueUINT32();
        m_timeScale = stream.readValueUINT32();
        m_singletonSettings.setTimeScaleVideo(m_timeScale);
        m_duration = stream.readValueUINT32();
        stream.readBlock(m_data);
        m_nextTrackID = stream.readValueUINT32();
        startPos += m_size;
    }

    public void setNewDuration(Integer time){
        m_duration = time * m_timeScale;
    }
}
