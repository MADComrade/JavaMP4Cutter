package com.asklab.atoms;

import com.asklab.StreamReader;
import com.asklab.StreamWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TKHD extends Atom {

    private ByteBuffer m_data;
    private Integer m_duration;
    private Integer  m_verFlag;
    private Integer  m_reserved=0; // не трогать
    private Integer  m_creationTime;
    private Integer  m_modificationTime;
    private Integer  m_trackID;

    public TKHD() {
        super(Atom.TKHD_NAME, Atom.TKHD_DIG_NAME);
        m_data = ByteBuffer.allocate(60);
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));                                                                              //запись размера в новый файл MP4 в BigEndian
        stream.writeTitleAtom(TKHD_NAME);                                                                                 //запись имени атома TKHD
        stream.writeValueUINT32(m_verFlag);                                                                                   //запись значения m_verFlag
        stream.writeValueUINT32(m_creationTime);                                                                              //запись времени создания
        stream.writeValueUINT32(m_modificationTime);                                                                          //запись времени изменения
        stream.writeValueUINT32(m_trackID);                                                                           //запись значения m_trackID в BigEndian
        stream.writeValueUINT32(m_reserved);                                                                          //запись значения m_reserved в BigEndian
        stream.writeValueUINT32(m_duration);                                                                          //запись значения m_duration в BigEndian
        stream.writeBlock(m_data.array());                                                                        //запись в новый файл MP4 данных длиной m_data.size() по адресу m_data[ 0 ]

    }

    @Override
    public void prepareData() {

    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {

    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        printAtomName(LV3);                                                                                              //вывод названия атома с определненным выравниванием(lv)
        Long pos = startPos;                                                                                         //присваивание переменной pos значения текущей начальной позиции в файле startPos
        stream.setPos(pos);                                                                                              //установка указателя файла на новую позицию pos
        m_size = stream.readSizeAtom();                                                                                  //размер чанка атома в памяти(???)
        pos+=OFFSET_TITLE;                                                                                               //увеличение значения pos на 8 байт (размер и заголовок атома)
        stream.setPos(pos);                                                                                              //установка указателя файла на новую позицию pos
        m_verFlag = stream.readValueUINT32();                                                                                 //считываются в m_verFlag 4 байта после размера и заголовка атома
        m_creationTime = stream.readValueUINT32();                                                                            //в m_creationTime считываются следующие 4 байта после уже прочитанных в m_verFlag (считываются следующие 4 байта автоматически, потому что здесь не написано функций для перевода указателя в определенную позицию setPos(pos), и получается, что указатель сам смещается вместе с прочитанными 4-мя байтами)
        m_modificationTime = stream.readValueUINT32();                                                                        //считывается следующая порция 4-х байт в m_modificationTime
        m_trackID = stream.readValueUINT32();                                                                        //считываются следующие 4 байта в переменную m_trackID в формате BigEndian
        stream.readValueUINT32();                                                                                    //просто считываются еще следующие 4 байта (и указатель вместе с ними, как и в предыдущих считываниях байтов)
        m_duration = stream.readValueUINT32();                                                                       //считываются следующие 4 байта в переменную m_duration в формате BigEndian
        stream.readBlock(m_data);                                                                         //чтение m_data.size байт из файла m_file и их размещение по адресу, начиная с m_data[0] и т.д.
        startPos += m_size;                                                                                              //значение начальной позиции startPos увеличиваем на размер чанка атома m_size
    }

    public void setNewDuration(Integer time){
        m_duration = time * m_singletonSettings.getTimeScaleVideo();                                                     //для универсальности передавать time scale
    }
}
