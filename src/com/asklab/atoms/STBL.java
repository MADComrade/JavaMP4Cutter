package com.asklab.atoms;

import com.asklab.Pair;
import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;

public class STBL extends Atom {
    private TRAK_TYPE m_trakType;
    private STSC m_stsc;
    private STSD m_stsd;
    private STSS m_stss;
    private STSZ m_stsz;
    private STCO m_stco;
    private STTS m_stts;
    private Long m_pos;

    public STBL(TRAK_TYPE type) {
        super(Atom.STBL_NAME, Atom.STBL_DIG_NAME);
        m_trakType = type;
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(STBL_NAME);
        if(m_trakType == TRAK_TYPE.VIDEO){
            m_stsc.writeAtom(stream);
            m_stts.writeAtom(stream);
            m_stsd.writeAtom(stream);
            m_stss.writeAtom(stream);
            m_stco.writeAtom(stream);
            m_stsz.writeAtom(stream);
        } else if(m_trakType == TRAK_TYPE.AUDIO){
            m_stsc.writeAtom(stream);
            m_stts.writeAtom(stream);
            m_stsd.writeAtom(stream);
            m_stco.writeAtom(stream);
            m_stsz.writeAtom(stream);
        }
    }

    @Override
    public void prepareData() {
        if(m_trakType == TRAK_TYPE.VIDEO){
            m_stss.prepareData();
            m_stsz.prepareData();
            m_stco.prepareData();
            m_stts.prepareData();
            Pair<Integer, Integer> dumpPos = m_stco.getOldOffset();
            dumpPos.setSecond(dumpPos.getSecond() + m_stsz.getEndChunkSize());
            m_singletonSettings.setEndOffsetVideo(dumpPos.getSecond());
        }else{
            m_stsz.prepareData();
            m_stco.prepareData();
            m_stts.prepareData();
            m_stsc.prepareData();
        }
    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {
        if(direction == DIRECT_RESIZE.INCREASED){                                                                       //если размер атома увеличился
            this.m_size +=size;                                                                                         //увеличение размера атома
        }else{                                                                                                           //если размер атома уменьшился
            this.m_size -=size;                                                                                         //уменьшение размера атома
        }
        m_callback.resizeAtom(size,direction);												// вызов изменения атома выше уровнем
    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        printAtomName(LV5);
        m_size = stream.readSizeAtom();
        m_pos = startPos+OFFSET_TITLE;
        String atomName;
        while (m_pos<(m_size+startPos)) {
            stream.setPos(m_pos);
            atomName =stream.getTitleAtom();
            buildAndParseAtom(atomName,stream,m_pos);
        }
        startPos += m_size;
    }

    void buildAndParseAtom(String atomName, StreamReader stream, Long startPos) throws IOException{
        switch (atomName) {
            case STCO_NAME:
                m_stco = new STCO(m_trakType);
                m_stco.setCallback(this);
                m_stco.parse(stream,startPos);
                m_pos += m_stco.size();
                break;
            case STSC_NAME:
                m_stsc = new STSC(m_trakType);
                m_stsc.setCallback(this);
                m_stsc.parse(stream,startPos);
                m_pos += m_stsc.size();
                break;
            case STSD_NAME:
                m_stsd = new STSD();
                m_stsd.setCallback(this);
                m_stsd.parse(stream,startPos);
                m_pos += m_stsd.size();
                break;
            case STSZ_NAME:
                m_stsz = new STSZ(m_trakType);
                m_stsz.setCallback(this);
                m_stsz.parse(stream,startPos);
                m_pos += m_stsz.size();
                break;
            case STSS_NAME:
                m_stss = new STSS(m_trakType);
                m_stss.setCallback(this);
                m_stss.parse(stream,startPos);
                m_pos += m_stss.size();
                break;
            case STTS_NAME:
                m_stts = new STTS(m_trakType);
                m_stts.setCallback(this);
                m_stts.parse(stream,startPos);
                m_pos += m_stts.size();
                break;
            default:
                break;
        }
    }
}
