package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.util.ArrayList;

public class STTS extends Atom {
    private TRAK_TYPE m_trakType;
    private ArrayList<SttsData> m_data;
    private Integer m_verFlag;
    private Integer m_amount = 0;

    public STTS(TRAK_TYPE type) {
        super(Atom.STTS_NAME, Atom.STTS_DIG_NAME);
        m_trakType = type;
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));													// запись размера атома в BigEndian
        stream.writeTitleAtom(STTS_NAME);													// запись названия атома
        stream.writeValueUINT32(m_verFlag);														// запись Version и Flags
        stream.writeValueUINT32(m_amount);												// запись кол-ва массива данных STTS
        for (int i = 0; i < m_amount; i++) {												// запись массива данных STTS
            stream.writeValueUINT32(m_data.get(i).m_sampleCount);							// запись Sample count
            stream.writeValueUINT32(m_data.get(i).m_sampleDelta);							// запись Sample delta
        }
    }

    @Override
    public void prepareData() {
        if (m_trakType == TRAK_TYPE.VIDEO) {												// если тип ВИДЕО
            Integer begTime = m_singletonSettings.getIDBeginChunkVideo();					// сохранение времени начала обработки
            Integer endTime = m_singletonSettings.getEndTime();							// сохранение времени конца обработки
            Integer delta = m_singletonSettings.getDelta();								// сохранение дельты ключевого кадра
            for (int i = 0; i < m_amount; i++) {											// изменение массива данных STTS
                SttsData temp = m_data.get(i);
                temp.m_sampleCount = (delta * endTime) + delta - begTime;				// изменение Sample count
                m_data.set(i,temp);
            }
        }
        else {																				// если тип АУДИО
            for (int i = 0; i < m_amount; i++) {
                SttsData temp = m_data.get(i);
                temp.m_sampleCount = m_singletonSettings.getAmountChunkAudio();				// изменение Sample count
                m_data.set(i,temp);
            }
        }
    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {
        callback().resizeAtom(size,direction);
    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        printAtomName(LV6);																	// вывод отступа и имени атома
        m_size = stream.readSizeAtom();														// чтение размера атома
        Long pos = startPos + OFFSET_TITLE;												// изменение позиции на следующий атома
        stream.setPos(pos);																	// установка позиции в поток чтения
        m_verFlag = stream.readValueUINT32();													// чтение Version и Flags
        m_amount = stream.readValueUINT32();											// чтение кол-ва данных STTS
        m_data= new ArrayList<SttsData>(m_amount);															// изменение размера массива данных STTS
        for (int i = 0; i < m_amount; i++) {// чтение массива данных STTS
            SttsData temp = new SttsData();
            temp.setSampleCount(stream.readValueUINT32());
            temp.setSampleDelta(stream.readValueUINT32());
            m_data.add(i,temp);
        }
        if (m_trakType == TRAK_TYPE.VIDEO) {												// если тип трека видео
            m_singletonSettings.setDeltaVideo(m_data.get(0).getSampleDelta());						// сохранение дельту видео
        }
        else {																				// если тип трека аудио
            m_singletonSettings.setDeltaAudio(m_data.get(0).getSampleDelta());						// сохранение дельту аудио
        }
        startPos += m_size;
    }
}
