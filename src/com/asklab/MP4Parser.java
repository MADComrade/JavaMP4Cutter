package com.asklab;

import com.asklab.atoms.FTYP;
import com.asklab.atoms.MOOV;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.asklab.atoms.Atom.*;

public class MP4Parser {
    private MOOV m_moov;
    private FTYP m_ftyp;
    private Pair<Integer, Integer> m_mdatInfo;


    public void parse(StreamReader stream) throws IOException {
        Long startPos=0L;                                                               //задана беззнаковая переменная startPos=0(???), 4 байтная
        String titleName = stream.getTitleAtom();                                           //получение типа(названия) атома
        if(!titleName.equals(FTYP_NAME)){                                                   //если название атома не совпало с ftyp
            throw new IOException("Don't find FTYP atom");                                                                      //то исключение
        }
        m_ftyp = new FTYP();                                                       //создание умного указателя unique_ptr на атом FTYP ???
        m_ftyp.parse(stream, startPos);                                                    //вызов виртуальной функции парсинга атома ftyp в файле ftyp.срр ???
        startPos = (long)m_ftyp.size();
        Long offset = 0L;                                                                 //задана беззнаковая переменная offset=0(???), 4 байтная
        Long siZeFile = stream.getFileSize();
        do{
            if(startPos >= siZeFile){
                throw new IOException("Don't find MOOV atom");
            }
            startPos += offset;                                                             //перенос начальной позиции в файле на величину offset
            stream.setPos(startPos);                                                        //установка указателя файла на новую позицию startPos
            offset = stream.readSizeAtom();                                                 //присваивание offset размера сhunk атома
            titleName = stream.getTitleAtom();                                              //присваивание titleName имени атома
        }while(!titleName.equals(MOOV_NAME));                                               //все это выполняется пока titleName не будет совпадать с названием атома moov
        m_moov = new MOOV();                                                       //создание умного указателя unique_ptr на атом MOOV ???
        m_moov.parse(stream,startPos);                                                     //вызов виртуальной функции парсинга атома moov в файле moov.срр ???
    }


    public void prepareData(Integer begTime, Integer endTime){
        m_mdatInfo = new Pair<Integer, Integer>(0,0);
        m_mdatInfo = m_moov.prepareAllData(begTime,endTime);
    }

    public void writeFilePart(StreamReader inStream, StreamWriter outStream) throws IOException {
        m_ftyp.writeAtom(outStream);                                                       //записать в новый MP4 файл данные атома ftyp
        Integer mdatSize = (m_mdatInfo.getSecond() - m_mdatInfo.getFirst())+OFFSET_TITLE;            //вычисление размера атома mdat ???
        Integer buf = m_mdatInfo.getSecond() - m_mdatInfo.getFirst();                                //вычисление размера блока данных атома mdat без учета его блока size и блока name ???
        outStream.writeValueUINT32(mdatSize);                                            //написание в новом файле MP4 размера атома mdat не Little, а BigEndian
        outStream.writeTitleAtom(MDAT_NAME);                                                 //записать в новом файле MP4 имя атома MDAT_NAM

        Integer BlockSize = 4096000;
        Integer blockBytes = (mdatSize - OFFSET_TITLE)/BlockSize;
        Integer blockEndBytes = (mdatSize - OFFSET_TITLE)%BlockSize;
        Integer startPos = m_mdatInfo.getFirst();
        Integer endPos = m_mdatInfo.getFirst()+BlockSize;

        inStream.setPos(startPos);
        ByteBuffer temp;// = ByteBuffer.allocate(BlockSize);                                          //создать вектор dataValue размером BlockSize и заполнить все выделенные ячейки памяти нулями
        for (int i=0;i<blockBytes;i++){
            temp = ByteBuffer.allocate(BlockSize);
            inStream.readBlock(temp);                       //чтение в исходном файле MP4 по адресу dataV[ 0 ] данных длиной dataV.size(), начиная с позиции исходного MP4 файла startPos ???
            outStream.writeBlock(temp.array());  //запись в новый файл MP4 данных длиной dataV.size() по адресу dataV[ 0 ] ???
            startPos = endPos;
            endPos +=BlockSize;
        }

        if(blockEndBytes !=0){
            temp = ByteBuffer.allocate(blockEndBytes);
            inStream.readBlock(temp);
            outStream.writeBlock(temp.array());
        }
        m_moov.writeAtom(outStream);
    }
}
