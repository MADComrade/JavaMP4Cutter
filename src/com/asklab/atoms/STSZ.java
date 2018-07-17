package com.asklab.atoms;

import com.asklab.Pair;
import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.util.ArrayList;

public class STSZ extends Atom {
    private TRAK_TYPE m_trakType;
    private ArrayList<Integer> m_chunkSize;
    private Integer m_verFlag;
    private Integer m_sampleSize=0;
    private Integer m_amountChunk=0;


    public STSZ(TRAK_TYPE type) {
        super(Atom.STSZ_NAME, Atom.STSZ_DIG_NAME);
        m_trakType = type;
    }

    public Integer getEndChunkSize() {
        return m_chunkSize.get(m_chunkSize.size()-1);
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(STSZ_NAME);
        stream.writeValueUINT32(m_verFlag);
        stream.writeValueUINT32(m_sampleSize);
        stream.writeValueUINT32(m_chunkSize.size());
        for(int i=0;i<m_chunkSize.size();i++){
            stream.writeValueUINT32(m_chunkSize.get(i));
        }
    }

    @Override
    public void prepareData() {
        if(m_trakType == TRAK_TYPE.VIDEO){
            Integer begTime = m_singletonSettings.getIDBeginChunkVideo();
            Integer endTime = m_singletonSettings.getEndTime();
            Integer delta = m_singletonSettings.getDelta();

            Integer endPos = (endTime*delta);
            Integer countResize = m_chunkSize.size();
            if(endPos != (m_chunkSize.size())){
                m_chunkSize.subList(endPos,m_chunkSize.size()).clear();
            }
            if(begTime != 0){
                m_chunkSize.subList(0,begTime).clear();
            }
            m_singletonSettings.setLastChunkVideoSize(m_chunkSize.get(m_chunkSize.size()-1));
            int resizeAmount =(countResize-m_chunkSize.size())*BYTE32;
            m_size -=resizeAmount;
            resizeAtom(resizeAmount,DIRECT_RESIZE.DECREASED);
        }else{
            Pair<Integer,Integer> idStartData = m_singletonSettings.getStartIdChunkAudio();
            Pair<Integer,Integer> idEndData = m_singletonSettings.getEndIdChunkAudio();
            idStartData.setFirst(idStartData.getFirst()*4-3);
            idEndData.setFirst(idEndData.getFirst()*4-3);
            Integer m_offsetStartSize=0;
            Integer m_offsetEndSize=0;
            if(idStartData.getSecond() != 0){
                for(int i = idStartData.getFirst(); i<idStartData.getFirst()+idStartData.getSecond();i++){
                    m_offsetStartSize += m_chunkSize.get(i);
                }
            }
            m_singletonSettings.setFirstChunkAudioSize(m_offsetStartSize);
            if(idEndData.getSecond() != 0){
                for(int i = idEndData.getFirst(); i<idEndData.getFirst()+idEndData.getSecond();i++){
                    m_offsetEndSize += m_chunkSize.get(i);
                }
            }
            m_singletonSettings.setLastChunkAudioSize(m_offsetEndSize);
            Integer countResize = m_chunkSize.size();// - endPos + startPos; ///????? endPos +-1
            if(idEndData.getFirst() != (m_chunkSize.size())){
                m_chunkSize.subList(idEndData.getFirst()+idEndData.getSecond(),(m_chunkSize.size())).clear();
                //m_chunkSize.erase(m_chunkSize.begin()+idEndData.first+idEndData.second,m_chunkSize.end());
            }
            if(idStartData.getFirst() != 0){
                m_chunkSize.subList(0,idStartData.getFirst()+idStartData.getSecond()-1).clear();
                //m_chunkSize.erase(m_chunkSize.begin(),m_chunkSize.begin()+idStartData.first+idStartData.second-1); ///????? startPos +-1
            }
            m_singletonSettings.setAmountChunkAudio(m_chunkSize.size());
            m_singletonSettings.setArrayChunkOffsetAudio(m_chunkSize);
            Integer resizeAmount = (countResize-m_chunkSize.size())*BYTE32;
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
        m_sampleSize = stream.readValueUINT32();
        m_amountChunk = stream.readValueUINT32();
        m_chunkSize = new ArrayList<Integer>();
        for(int i=0;i<m_amountChunk;i++){
            m_chunkSize.add(i,stream.readValueUINT32());
        }
        startPos +=m_size;
    }
}
