package com.asklab.atoms;

import com.asklab.Pair;
import com.asklab.StreamReader;
import com.asklab.StreamWriter;
import com.asklab.interfaces.ParserInterface;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.asklab.atoms.Atom.MVHD_NAME;

public class MOOV extends Atom {
    private MVHD m_mvhd;
    private TRAK m_videoTrak;
    private TRAK m_audioTrak;
    private Map m_parseMap;

    public MOOV() {
        super(Atom.MOOV_NAME, Atom.MOOV_DIG_NAME);
        m_mvhd = new MVHD();
        m_videoTrak = new TRAK();
        m_audioTrak = new TRAK();
        m_videoTrak.setCallback(this);
        m_audioTrak.setCallback(this);
        m_parseMap = new HashMap<String, ParserInterface>();
        m_parseMap.put(MVHD_NAME,(ParserInterface)m_mvhd);
        m_parseMap.put(VIDE,(ParserInterface)m_videoTrak);
        m_parseMap.put(SOUN,(ParserInterface)m_audioTrak);
    }

    @Override
    public void writeAtom(StreamWriter stream) throws IOException {
        stream.writeValueUINT32(Math.toIntExact(m_size));
        stream.writeTitleAtom(MOOV_NAME);
        m_mvhd.writeAtom(stream);
        m_videoTrak.writeAtom(stream);
        m_audioTrak.writeAtom(stream);
    }

    @Override
    public void prepareData() {

    }

    public Pair<Integer,Integer> prepareAllData(Integer begTime, Integer endTime){
        m_singletonSettings.setPeriodTime(begTime,endTime);
        m_mvhd.setNewDuration(m_singletonSettings.getNewDuration());
        m_videoTrak.prepareData();
        m_audioTrak.prepareData();
        return new Pair<>(m_singletonSettings.getBeginOffsetFile(), m_singletonSettings.getSizeCut());
    }

    @Override
    public void resizeAtom(Integer size, DIRECT_RESIZE direction) {
        if(direction == DIRECT_RESIZE.INCREASED){                                                                       //если размер атома увеличился
            this.m_size +=size;                                                                                         //увеличение размера атома
        }else{                                                                                                           //если размер атома уменьшился
            this.m_size -=size;                                                                                         //уменьшение размера атома
        }
    }

    @Override
    public void parse(StreamReader stream, Long startPos) throws IOException {
        if(stream.getTitleAtom().equals(MOOV_NAME)){                                                                            //если название атома совпадает с moov
            printAtomName(LV1);                                                                                          //вывод названия атома с определненным выравниванием(lv)
            m_offset = startPos;                                                                                         //присваивание переменной m_offset значения текущей начальной позиции в файле startPos
            m_size = stream.readSizeAtom();                                                                              //размер чанка атома в памяти(?)
            Long foundPos = startPos+OFFSET_TITLE;                                                                   //присваивание переменной foundPos значения позиции в файле, указывающей на данные атома(потому что к переменной текущей начальной позиции файла startPos прибавилось значение 8 байт(размера size и размера названия атома))
            Long atomSize=0L;                                                                                        //задана беззнаковая переменная atomSize=0(???), 4 байтная
            String titleName;
            while (foundPos<(m_size+startPos)) {                                                                         //пока значение позиции в файле foundPos, указывающей на данные атома, меньше, чем (размер чанка атома в памяти + текущая начальная позиция в атоме startPos)
                stream.setPos(foundPos);                                                                                 //перемещение указателя файла на значение foundPos
                atomSize = stream.readSizeAtom();                                                                        //размер чанка атома в памяти
                titleName =stream.getTitleAtom();                                                                        //получение заголовка атома
                if(titleName.equals(TRAK_NAME)){                                                                                //если заголовок атома совпадает с TRAK
                    stream.setPos(foundPos+156);                                                                         //установить значение позиции в файле foundPos+156 (156, т.к. размер атомов фиксированный, и получается переход в атом hdlr и чтение значения, что именно у нас: видео или аудио) ???
                    titleName = stream.getDataInAtom();                                                                  //чтение 4 байт типа в атоме hdlr (в значении Handler type: soun или vide, т.е. аудио или видео) ???
                    stream.setPos(foundPos);                                                                             //снова устанавливается значение позиции в файле foundPos
                    System.out.println("=======\n" + titleName + "\n=======");                                   //вывод типа файла: аудио или видео
                }                                                              //в контейнере map "ключ-значение" m_parseMap ищем по ключу titleName его значение
                if(m_parseMap.containsKey(titleName)){                                                                          //проверка, не указывает ли search на конец контейнера map(то есть что ключа titleName нет в контейнере, как, соответственно, и его значения)
                    ParserInterface atomParse = (ParserInterface)m_parseMap.get(titleName);
                    atomParse.parse(stream, foundPos);                                                   //если искомый ключ и его значение есть в контейнере map, то обращаемся к значению ключа и распарсиваем его(например, если ключ vide, то его значением в контейнере map будет m_videoTrak(как указано в строке m_parseMap[VIDE]=dynamic_cast<ParserInterface*>(m_videoTrak.get());), и соответственно по вызову m_videoTrak.get() в заголовочном файде moov.h по указателю unique_ptr приходим к атому TRAK и парсим уже его)
                    switch (titleName){
                        case MVHD_NAME:
                            foundPos +=m_mvhd.size();
                            break;
                        case VIDE:
                            foundPos +=m_videoTrak.size();
                            break;
                        case SOUN:
                            foundPos +=m_audioTrak.size();
                            break;
                    }
                }else{
                    foundPos += atomSize;                                                                                //если искомого ключа и его значения нету в контейнере map, то к позиции атома foundPos прибавляем размер чанка атома atomSize
                }
            }
        }else{
            throw new IOException("Don't find MOOV atom");                                                                                              //иначе выход с кодом ошибки
        }
        startPos += m_size;
    }

    public void writeAudioAndVideo(StreamReader outStream){ //Writer

    }
}
