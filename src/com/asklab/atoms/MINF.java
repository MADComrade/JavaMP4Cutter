package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;

public class MINF extends Atom {
    private TRAK_TYPE m_trakType;
    VMHD m_vmhd;
    SMHD m_smhd;
    DINF m_dinf;
    STBL m_stbl;

    public MINF(TRAK_TYPE type) {
        super(Atom.MINF_NAME, Atom.MINF_DIG_NAME);
        m_trakType = type;
        m_dinf = new DINF();
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(MINF_NAME);
        if(m_trakType == TRAK_TYPE.VIDEO){
            m_vmhd.writeAtom(stream);
        } else if(m_trakType == TRAK_TYPE.AUDIO){
            m_smhd.writeAtom(stream);
        }
        m_dinf.writeAtom(stream);
        m_stbl.writeAtom(stream); /// TODO
    }

    @Override
    public void prepareData() {
        m_stbl.prepareData();
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
        printAtomName(LV4);
        Long pos = startPos;
        m_size = stream.readSizeAtom();
        pos = startPos+OFFSET_TITLE;
        stream.setPos(pos);
        if(m_trakType == TRAK_TYPE.VIDEO){
            m_vmhd = new VMHD();
            m_vmhd.parse(stream,pos);
            pos +=m_vmhd.size();
        } else if(m_trakType == TRAK_TYPE.AUDIO){
            m_smhd = new SMHD();
            m_smhd.parse(stream,pos);
            pos +=m_smhd.size();
        }else{
            throw  new IOException("WTF???");
        }
        m_dinf.parse(stream,pos);
        pos +=m_dinf.size();
        m_stbl = new STBL(m_trakType);
        m_stbl.setCallback(this);
        m_stbl.parse(stream,pos);
        startPos += m_size;
    }
}
