package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;

public class MDHD extends Atom {
    private Integer m_timeScale;
    private Integer m_duration;
    private Integer m_verFlag;
    private Integer m_language;
    private Integer m_creationTime;
    private Integer m_modificationTime;
    private TRAK_TYPE m_typeTrak;

    public MDHD() {
        super(Atom.MDHD_NAME, Atom.MDHD_DIG_NAME);
    }

    void setTrakType(TRAK_TYPE type){
        m_typeTrak = type;
        if(type == TRAK_TYPE.AUDIO){
            m_singletonSettings.setTimeScaleAudio(m_timeScale);
        }
    }

    public void setNewDuration(Integer time){
        m_duration = time * m_timeScale;
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(MDHD_NAME);
        stream.writeValueUINT32(m_verFlag);
        stream.writeValueUINT32(m_creationTime);
        stream.writeValueUINT32(m_modificationTime);
        stream.writeValueUINT32(m_timeScale);
        stream.writeValueUINT32(m_duration);
        stream.writeValueUINT32(m_language);
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
        pos = startPos+OFFSET_TITLE;
        stream.setPos(pos);
        m_verFlag = stream.readValueUINT32();
        m_creationTime = stream.readValueUINT32();
        m_modificationTime = stream.readValueUINT32();
        m_timeScale = stream.readValueUINT32();
        m_duration = stream.readValueUINT32();
        m_language = stream.readValueUINT32();
        startPos += m_size;
    }
}
