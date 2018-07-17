package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;

public class MDIA extends Atom {
    private MDHD m_mdhd;
    private HDLR m_hdlr;
    private MINF m_minf;

    public MDIA() {
        super(Atom.MDIA_NAME, Atom.MDIA_DIG_NAME);
        m_mdhd = new MDHD();
        m_hdlr = new HDLR();
    }

    public  TRAK_TYPE getTrakType(){
        return  m_hdlr.getTrakType(); // hard
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));                                                 //запись размера в BigEndian
        stream.writeTitleAtom(MDIA_NAME);                                                    //запись названия атома MDIA
        m_mdhd.writeAtom(stream);                                                          //запись атома mdhd
        m_hdlr.writeAtom(stream);                                                          //запись атома hdlr
        m_minf.writeAtom(stream);                                                          //запись атома minf
    }

    @Override
    public void prepareData() {
        m_mdhd.setNewDuration(m_singletonSettings.getNewDuration());                       //установка новой длительности видео в mdhd
        m_minf.prepareData();                                                              //обработка minf
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
        printAtomName(LV3);                                                                 //вывод названия атома с определненным выравниванием(lv)
        Long pos;                                                            //присваивание переменной pos значения текущей начальной позиции в файле startPos
        m_size = stream.readSizeAtom();                                                     //размер чанка атома в памяти(???)
        pos = startPos+OFFSET_TITLE;                                                        //присваивание переменной pos значения (текущая начальная позиция в файле startPos + 8 байт(размер и заголовок атома))
        stream.setPos(pos);                                                                 //установка указателя файла на новую позицию pos
        m_mdhd.parse(stream,pos);                                                          //вызов виртуальной функции парсинга атома mdhd в файле mdhd.срр ???
        pos +=m_mdhd.size();
        m_hdlr.parse(stream,pos);                                                          //вызов виртуальной функции парсинга атома hdlr в файле hdlr.срр ???
        m_minf = new MINF(m_hdlr.getTrakType());                                  //создание умного указателя unique_ptr на атом MINF, но с указанием какого именно: soun или vid
        m_minf.setCallback(this);                                                          //создание возврата в minf
        pos +=m_hdlr.size();
        m_mdhd.setTrakType(getTrakType());                                                 //вызов виртуальной функции setTrakType в mdhd.срр (???)
        m_minf.parse(stream,pos);                                                          //вызов виртуальной функции парсинга атома minf в файле minf.срр ???
        startPos += m_size;                                                                 //значение начальной позиции startPos увеличиваем на размер чанка атома m_size
    }
}
