package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.util.ArrayList;

public class STSS extends Atom {
    private TRAK_TYPE m_trakType;
    private ArrayList<Integer> m_offsetIFrame;
    private Integer m_verFlag;
    private Integer m_startCutPos=0;
    private Integer m_amountChunk=0;
    private Integer m_deltaIFrame=0;

    public STSS(TRAK_TYPE type) {
        super(Atom.STSZ_NAME, Atom.STSZ_DIG_NAME);
        m_trakType = type;
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(STSS_NAME);
        stream.writeValueUINT32(m_verFlag);
        stream.writeValueUINT32(m_offsetIFrame.size());
        for(int i=0;i<m_offsetIFrame.size();i++){
            stream.writeValueUINT32(m_offsetIFrame.get(i));
        }
    }

    @Override
    public void prepareData() {
        Integer begTime = m_singletonSettings.getBeginTime();
        Integer endTime = m_singletonSettings.getEndTime();
        if(m_trakType == TRAK_TYPE.VIDEO){
            m_deltaIFrame = m_singletonSettings.getDelta();
            Integer start = begTime*m_deltaIFrame;
            Integer finish = (endTime*m_deltaIFrame);
            Integer startPos=0, endPos=0;
            Integer countResize=0;
            for(Integer i=0;i<m_offsetIFrame.size();i++){
                if(start<m_offsetIFrame.get(i)){
                    startPos = i;
                    break;
                }
                if(start ==m_offsetIFrame.get(i)){
                    startPos = i+1;
                    break;
                }
            }

            for(Integer i=startPos;i<m_offsetIFrame.size();i++){
                if(finish<=m_offsetIFrame.get(i)){
                    endPos = i;
                    break;
                }
            }

            m_startCutPos = m_offsetIFrame.get(startPos)-1;//-1;
            m_singletonSettings.setIDBeginChunkVideo(m_startCutPos);
            countResize = m_offsetIFrame.size();
            if(endPos != (m_offsetIFrame.size())){
                m_offsetIFrame.subList(Math.abs(endPos-startPos),m_offsetIFrame.size()).clear();
               // m_offsetIFrame.erase(m_offsetIFrame.begin()+(std::abs(static_cast<long>(endPos-startPos))),m_offsetIFrame.end());
            }

            Integer resizeAmount = (countResize-m_offsetIFrame.size())*BYTE32;
            m_size -=resizeAmount;
            resizeAtom(resizeAmount,DIRECT_RESIZE.DECREASED);

        }
    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {
        callback().resizeAtom(size,direction);
    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        printAtomName(LV6);
        m_size = stream.readSizeAtom();
        Long pos = startPos+OFFSET_TITLE;
        stream.setPos(pos);
        m_verFlag = stream.readValueUINT32();
        m_amountChunk = stream.readValueUINT32();
        m_offsetIFrame = new ArrayList<Integer>();
        for(int i=0;i<m_amountChunk;i++){
            m_offsetIFrame.add(i,stream.readValueUINT32());
        }
        m_singletonSettings.setDelta(m_singletonSettings.getDeltaVideo()+1);
        startPos +=m_size;
    }
}
