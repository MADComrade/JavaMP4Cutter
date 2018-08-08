package com.asklab.atoms;

import com.asklab.Pair;
import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class STCO extends Atom {
    private TRAK_TYPE m_trakType;
    private ArrayList<Integer> m_chunkOffset;
    private Integer m_verFlag;
    private Integer m_amountChunk=0;
    private Integer m_startCutOffset=0;
    private Integer m_endCutOffset=0;

    public STCO(TRAK_TYPE type) {
        super(Atom.STCO_NAME, Atom.STCO_DIG_NAME);
        m_trakType = type;
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(STCO_NAME);
        stream.writeValueUINT32(m_verFlag);
        stream.writeValueUINT32(m_chunkOffset.size());
        for(int i=0;i<m_chunkOffset.size();i++){
            stream.writeValueUINT32(m_chunkOffset.get(i));
        }
    }

    @Override
    public void prepareData() {
        if(m_trakType == TRAK_TYPE.VIDEO){
            Integer begTime = m_singletonSettings.getIDBeginChunkVideo();
            Integer endTime = m_singletonSettings.getEndTime();
            Integer delta = m_singletonSettings.getDelta();
            m_singletonSettings.setArrayChunkOffsetVideo(m_chunkOffset);
            Integer endPos = (endTime*delta);
            Integer countResize = m_chunkOffset.size() - endPos + begTime;

            m_startCutOffset = m_chunkOffset.get(begTime);
            m_singletonSettings.setBeginOffsetVideo(m_startCutOffset);
            m_endCutOffset = m_chunkOffset.get(endPos-1);
            m_singletonSettings.setEndOffsetVideo(m_endCutOffset);
            if(endPos < (m_chunkOffset.size())){
                m_chunkOffset.subList(endPos,(m_chunkOffset.size())).clear();
                //m_chunkOffset.erase(m_chunkOffset.begin()+endPos,m_chunkOffset.end());
            }
            if(begTime > 0){
                m_chunkOffset.subList(0,begTime).clear();
                //m_chunkOffset.erase(m_chunkOffset.begin(),m_chunkOffset.begin()+begTime);
            }
            Integer resizeAmount = countResize*BYTE32; // ?
            m_size -=resizeAmount;
            resizeAtom(resizeAmount,DIRECT_RESIZE.DECREASED);
        }
        else{

            Pair<Integer,Integer> idStartData = m_singletonSettings.getStartIdChunkAudio();
            Pair<Integer,Integer> idEndData = m_singletonSettings.getEndIdChunkAudio();
            Integer countResize = m_chunkOffset.size();
            ArrayList<Integer> stcoVideoOffset = m_singletonSettings.getArrayChunkOffsetVideo();

            if(idEndData.getFirst() < (m_chunkOffset.size())){
                m_chunkOffset.subList(idEndData.getFirst(),(m_chunkOffset.size())).clear();
                //m_chunkOffset.erase(m_chunkOffset.begin()+idEndData.first,m_chunkOffset.end());
            }

            if(idStartData.getFirst() > 0){
                m_chunkOffset.subList(0,idStartData.getFirst()-1).clear();
                //m_chunkOffset.erase(m_chunkOffset.begin(),m_chunkOffset.begin()+idStartData.first-1);
            }

            m_singletonSettings.setBeginOffsetAudio(m_chunkOffset.get(0));
            m_singletonSettings.setEndOffsetAudio(m_chunkOffset.get(m_chunkOffset.size()-1));
            Integer oldChunkOffset = m_chunkOffset.get(0);
            Integer tempOffset=0;
            Integer newChunkOffset = oldChunkOffset;// = m_chunkOffset[0]+sing.getFirstChunkAudioSize();
            Integer oldVideoOffset = stcoVideoOffset.get(0);

            if(oldChunkOffset<stcoVideoOffset.get(0)){
                m_singletonSettings.setBeginOffsetFile(oldChunkOffset);
                ///soun
                m_chunkOffset.set(0,40);
                for(Integer i=1;i<m_chunkOffset.size();i++){
                    tempOffset = m_chunkOffset.get(i);
                    m_chunkOffset.set(i,(m_chunkOffset.get(i)-newChunkOffset)+m_chunkOffset.get(i-1));
                    //m_chunkOffset[i] =(m_chunkOffset[i] - newChunkOffset)+m_chunkOffset[i-1];
                    newChunkOffset = tempOffset;
                }

                ///vide
                Integer oldChunkOffsetVideo = stcoVideoOffset.get(0);
                stcoVideoOffset.set(0,(stcoVideoOffset.get(0)-oldChunkOffset)+40);

                for(Integer i=1;i<stcoVideoOffset.size();i++){
                    tempOffset = stcoVideoOffset.get(i);
                    stcoVideoOffset.set(i,(stcoVideoOffset.get(i) - oldChunkOffsetVideo)+stcoVideoOffset.get(i-1));
                    //stcoVideoOffset[i] =(stcoVideoOffset[i] - oldChunkOffsetVideo)+stcoVideoOffset[i-1];
                    oldChunkOffsetVideo = tempOffset;
                }

                if(idStartData.getSecond() !=0){
                    m_chunkOffset.set(0, m_chunkOffset.get(0)+m_singletonSettings.getFirstChunkAudioSize());
                }

                Integer videoEndPart = m_singletonSettings.getOffsetVideo().getSecond() + m_singletonSettings.getLastChunkVideoSize();
                Integer audioEndPart = m_singletonSettings.getOffsetAudio().getSecond() + m_singletonSettings.getLastChunkAudioSize();
                if(videoEndPart>audioEndPart){
                    m_singletonSettings.setSizeCut(videoEndPart);
                }else{
                    m_singletonSettings.setSizeCut(audioEndPart);
                }
            }else{
                ///////////////////////////////////////////////////////////////////////////////////////////
                ///vide
                ///
                m_singletonSettings.setBeginOffsetFile(stcoVideoOffset.get(0));

                Integer oldChunkOffsetVideo = stcoVideoOffset.get(0);
                stcoVideoOffset.set(0,40);
                //stcoVideoOffset[0] = 40;

                for(Integer i=1;i<stcoVideoOffset.size();i++){
                    tempOffset = stcoVideoOffset.get(i);
                    stcoVideoOffset.set(i,(stcoVideoOffset.get(i) - oldChunkOffsetVideo)+stcoVideoOffset.get(i-1));
                    //stcoVideoOffset[i] =(stcoVideoOffset[i] - oldChunkOffsetVideo)+stcoVideoOffset[i-1];
                    oldChunkOffsetVideo = tempOffset;
                }
                ///soun
                m_chunkOffset.set(0,(m_chunkOffset.get(0)-oldVideoOffset)+40);
                for(Integer i=1;i<m_chunkOffset.size();i++){
                    tempOffset = m_chunkOffset.get(i);
                    m_chunkOffset.set(i,(m_chunkOffset.get(i)-newChunkOffset)+m_chunkOffset.get(i-1));
                    //m_chunkOffset[i] =(m_chunkOffset[i] - newChunkOffset)+m_chunkOffset[i-1];
                    newChunkOffset = tempOffset;
                }

                if(idStartData.getSecond() !=0){
                    m_chunkOffset.set(0,m_chunkOffset.get(0)+m_singletonSettings.getFirstChunkAudioSize());
                    //m_chunkOffset[0] +=m_singletonSettings.getFirstChunkAudioSize();
                }

                Integer videoEndPart = m_singletonSettings.getOffsetVideo().getSecond() + m_singletonSettings.getLastChunkVideoSize();
                Integer audioEndPart = m_singletonSettings.getOffsetAudio().getSecond() + m_singletonSettings.getLastChunkAudioSize();
                if(videoEndPart>audioEndPart){
                    m_singletonSettings.setSizeCut(videoEndPart);
                }else{
                    m_singletonSettings.setSizeCut(audioEndPart);
                }
            }
            Integer resizeAmount = (countResize-m_chunkOffset.size())*BYTE32;
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
        m_chunkOffset = new ArrayList<Integer>();
        ByteBuffer data = ByteBuffer.allocate(Math.toIntExact(m_amountChunk*4));
        stream.readBlock(data);
        IntBuffer intBuf =
                ByteBuffer.wrap(data.array())
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        for(int i=0;i<m_amountChunk;i++){
            m_chunkOffset.add(i,array[i]);
        }
    }

    Pair<Integer,Integer> getOldOffset(){
        return new Pair<>(m_startCutOffset,m_endCutOffset);
    }
}
