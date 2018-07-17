package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;

public class TRAK extends Atom {
    private  TRAK_TYPE m_typeTrak;
    private TKHD m_tkhd;														// атом нижнего уровня TKHD
    private MDIA m_mdia;														// атом нижнего уровня MDIA

    public TRAK() {
        super(Atom.TRAK_NAME, Atom.TRAK_DIG_NAME);
        m_tkhd = new TKHD();
        m_mdia = new MDIA();
        m_mdia.setCallback(this);
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));												// запись размера в BigEndian
        stream.writeTitleAtom(TRAK_NAME);												// запись имени атома
        m_tkhd.writeAtom(stream);														// запись атома TKHD
        m_mdia.writeAtom(stream);
    }

    @Override
    public void prepareData() {
        m_tkhd.setNewDuration(m_singletonSettings.getNewDuration());						// установка новой длительности видео в TKHD
        m_mdia.prepareData();																// обработка MDIA
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
        printAtomName(LV2);																	// вывод отступа и имени атома
        Long pos;               															// установка текущей позиции
        m_size = stream.readSizeAtom();														// чтение размера атома
        pos = startPos+OFFSET_TITLE;														// изменение позиции на следующий атома
        stream.setPos(pos);																	// установка позиции в поток чтения
        m_tkhd.parse(stream,pos);															// парсинг TKHD
        pos += m_tkhd.m_size;
        m_mdia.parse(stream,pos);															// парсинг MDIA
        m_typeTrak = m_mdia.getTrakType();													// установка типа трека (видео или аудио)
        startPos += m_size;																	// установка позиции для следующего трека (если он есть)
    }
}
