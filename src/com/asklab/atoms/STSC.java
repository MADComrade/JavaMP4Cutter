package com.asklab.atoms;

import com.asklab.Pair;
import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.util.ArrayList;

public class STSC extends Atom {
    private TRAK_TYPE m_trakType;
    private ArrayList<StscData> m_data;
    private Integer m_verFlag;
    private Integer m_amount=0;

    public STSC(TRAK_TYPE type) {
        super(Atom.STSC_NAME, Atom.STSC_DIG_NAME);
        m_trakType = type;
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(STSC_NAME);
        stream.writeValueUINT32(m_verFlag);
        stream.writeValueUINT32(m_amount);
        for(int i=0;i<m_amount;i++){
            stream.writeValueUINT32(m_data.get(i).m_firstChunk);
            stream.writeValueUINT32(m_data.get(i).m_samplesPerChunk);
            stream.writeValueUINT32(m_data.get(i).m_samplesIndex);
        }
    }

    @Override
    public void prepareData() {
        if(m_trakType == TRAK_TYPE.VIDEO){
        }else{
            Pair<Integer,Integer> idEndData = m_singletonSettings.getEndIdChunkAudio();
            Pair<Integer,Integer> idStartData = m_singletonSettings.getStartIdChunkAudio();
            Integer size = m_data.size();
            if(idStartData.getSecond() !=0){
                if(size<2) {
                    StscData tst = new StscData();
                    m_data.get(0).m_firstChunk = 1;
                    m_data.get(0).m_samplesPerChunk = 4 - idStartData.getSecond();//idEndData.second;
                    m_data.get(0).m_samplesIndex = 1;
                    tst.m_firstChunk = m_data.get(0).m_firstChunk + 1;
                    tst.m_samplesPerChunk = 4;
                    tst.m_samplesIndex = 1;
                    m_data.add(m_amount, tst);
                    m_amount++;
                }else{
                    m_data.get(0).m_firstChunk = 1;
                    m_data.get(0).m_samplesPerChunk = 4 - idStartData.getSecond();//idEndData.second;
                    m_data.get(0).m_samplesIndex = 1;
                    m_data.get(1).m_firstChunk = 2;
                    m_data.get(1).m_samplesPerChunk = 4;//idEndData.second;
                    m_data.get(1).m_samplesIndex = 1;
                }
            }
            if(idEndData.getSecond()>0)
            {

                StscData oldData = new StscData();
                oldData.m_firstChunk = (m_singletonSettings.getIDEndChunkAudio()-m_singletonSettings.getIDBeginChunkAudio())+1;
                oldData.m_samplesPerChunk = 2;// idEndData.second;//idEndData.second;
                oldData.m_samplesIndex = 1;
                m_data.add(m_amount,oldData);
                m_amount++;
            }

            Integer resize = size - m_data.size();
            if(resize == 0)
                return;
            Integer atomResize = (Math.abs(resize)*3*4);
            if(resize<0){
                m_size += atomResize;
                resizeAtom(atomResize,DIRECT_RESIZE.INCREASED);
            }else{
                m_size -=atomResize;
                resizeAtom(atomResize,DIRECT_RESIZE.DECREASED);
            }
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
        m_amount = stream.readValueUINT32();
        m_data = new ArrayList<StscData>(m_amount);
        for(int i=0;i<m_amount;i++){
            StscData temp = new StscData();
            temp.setFirstChunk(stream.readValueUINT32());
            temp.setSamplesPerChunk(stream.readValueUINT32());
            temp.setSamplesIndex(stream.readValueUINT32());
            m_data.add(i,temp);
        }
        startPos +=m_size;
    }
}
