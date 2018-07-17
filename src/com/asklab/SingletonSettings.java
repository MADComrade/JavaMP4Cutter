package com.asklab;

import java.util.ArrayList;

public class SingletonSettings {

    private Integer getOffsetAudioChunk(double fractPos){
        if ((fractPos >= 0)&&(fractPos < 0.25f)) {
            return 0;
        }
        else if ((fractPos >= 0.25f)&&(fractPos < 0.5f)) {
            return 1;
        }
        else if ((fractPos >= 0.5f)&&(fractPos < 0.75f)) {
            return 2;
        }
        else {
            return 3;
        }
    }										// вычисление смещения чанка аудио
    //----------------------------------------------------------------------------------//
    //----------------------------приватные атрибуты------------------------------------//
    //----------------------------------------------------------------------------------//
    private static volatile SingletonSettings instance;
    private Integer m_beginTime=0;														// время начала обработки
    private Integer	m_endTime=0;															// время конца обработки

    private Integer	m_newDuration=0;														// продолжительность выходного видео

    private Integer	m_timeScaleVideo=0;													// масштабирование видео
    private Integer	m_timeScaleAudio=0;													// масштабирование аудио

    private Integer	m_deltaAudio=0;														// дельта аудио
    private Integer	m_deltaVideo=0;														// дельта видео
    private Integer	m_delta=0;															// дельта ключевого кадра видео

    private Integer	m_beginOffsetAudio=0;												// начальный сдвиг аудио
    private Integer	m_endOffsetAudio=0;													// конечный сдвиг аудио

    private Integer	m_beginOffsetVideo=0;												// начальный сдвиг видео
    private Integer	m_endOffsetVideo=0;													// конечный сдвиг видео

    private Integer    m_beginOffsetFile=0;													// сдвиг отнсительно начала файла для вырезания
    private Integer    m_sizeCut=0;															// размер выходного видео

    private Integer	m_idBeginChunkVideo=0;												// ID чанка видео на начало обработки
    private Integer	m_idEndChunkVideo=0;													// ID чанка видео на конец обработки
    private Integer	m_idBegChunkWithIFrame=0;											// ID чанка ключевого кадра видео на начало обработки
    private Integer	m_idEndChunkWithIFrame=0;											// ID чанка ключевого кадра видео на конец обработки

    private Integer	m_idBeginChunkAudio=0;												// ID чанка аудио на начало обработки
    private Integer	m_idEndChunkAudio=0;													// ID чанка аудио на конец обработки

    private Integer	m_firstChunkAudioSize=0;												// размер первого чанка аудио
    private Integer	m_lastChunkAudioSize=0;												// размер последнего чанка аудио
    private Integer	m_lastChunkVideoSize=0;												// размер первого чанка видео

    private Integer	m_amountChunkAudio=0;												// кол-во чанков аудио
    private Integer	m_amountChunkVideo=0;												// кол-во чанков видео

    private Integer m_offsetStartAudioPos=0;												// сдвиг позиции начала аудио
    private Integer m_offsetEndtAudioPos=0;													// сдвиг позиции конца аудио

    ArrayList<Integer> m_chunkOffsetAudio;								// массив сдвигов аудио из stco
    ArrayList<Integer> m_chunkOffsetVideo;							// массив сдвигов видео из stco



    private SingletonSettings(){}
    public static SingletonSettings getInstance(){
        if (instance == null) {
            synchronized (SingletonSettings.class) {
                if (instance == null) {
                    instance = new SingletonSettings();
                }
            }
        }
        return instance;
    }


    public void setDeltaAudio(Integer delta){
        m_deltaAudio = delta;
    }													// установка дельты времени аудио
    public void setDeltaVideo(Integer delta){
        m_deltaVideo = delta;
    }													// установка дельты времени видео
    public void setDelta(Integer delta){
        m_delta = delta;
    }														// установка дельты времени для ключевых кадров видео

    public Integer getDeltaAudio(){
        return m_deltaAudio;
    }														// выдача дельты времени аудио
    public Integer getDeltaVideo(){
        return m_deltaVideo;
    }														// выдача дельты времени видео
    public Integer getDelta(){
        return m_delta;
    }															// выдача дельты времени для ключевых кадров видео

    public void setPeriodTime(Integer begTime, Integer endTime){
        m_beginTime = begTime;																// установка начала обработки
        m_endTime = endTime;																// установка конца обработки
        m_newDuration = endTime - begTime;													// вычисление продолжительности обработанного видео
        m_idBeginChunkVideo = m_beginTime * m_deltaVideo;									// вычисление чанка видео на начало обработки
        m_idEndChunkVideo = m_endTime * m_deltaVideo;										// вычисление чанка видео на конец обработки
        m_idBegChunkWithIFrame = m_beginTime * m_delta;										// вычисление ключевого чанка на начало обработки
        m_idEndChunkWithIFrame = m_endTime * m_delta;										// вычисление ключевого чанка на конец обработки
        double fractStartPos = ((begTime *(float)(m_timeScaleAudio)) / m_deltaAudio) / 4.0;
        double fractEndPos = ((endTime * (float)(m_timeScaleAudio)) / m_deltaAudio) / 4.0;
        m_idBeginChunkAudio = (int)(fractStartPos) + 2;
        m_idEndChunkAudio = (int)(fractEndPos) + 2;
        m_offsetStartAudioPos = getOffsetAudioChunk(fractStartPos - (int)(fractStartPos));
        m_offsetEndtAudioPos = getOffsetAudioChunk(fractEndPos - (int)(fractEndPos));
    }								// установка промежутка времени обработки

    public void setTimeScaleAudio(Integer timeScale){
        m_timeScaleAudio = timeScale;
    }											// установка коэффициента масштабирования аудио
    public void setTimeScaleVideo(Integer timeScale){
        m_timeScaleVideo = timeScale;
    }											// установка коэффициента масштабирования видео

    public Integer getBeginTime(){
        return m_beginTime;
    }															// выдача времени начала обработки
    public Integer getEndTime(){
        return m_endTime;
    }																// выдача времени окончания обработки

    public Integer getNewDuration(){
        return m_newDuration;
    }												// выдача продолжительности обработанного видео

    public Integer getTimeScaleAudio(){
        return m_timeScaleAudio;
    }														// выдача коэффициента масштабирования аудио
    public Integer getTimeScaleVideo(){
        return m_timeScaleVideo;
    }														// выдача коэффициента масштабирования видео

    public void setBeginOffsetAudio(Integer offset){
        m_beginOffsetAudio = offset;
    }											// установка начального сдвига аудио
    public void setEndOffsetAudio(Integer offset){
        m_endOffsetAudio = offset;
    }										// установка конечного сдвига аудио

    public void setBeginOffsetVideo(Integer offset){
        m_beginOffsetVideo = offset;
    }											// установка начального сдвига видео
    public void setEndOffsetVideo(Integer offset){
        m_endOffsetVideo = offset;
    }											// установка конечного сдвига видео

    public Pair<Integer, Integer> getOffsetAudio(){
        return new Pair<Integer, Integer>(m_beginOffsetAudio, m_endOffsetAudio);
    }										// выдача начального и конечного сдвига аудио
    public Pair<Integer, Integer> getOffsetVideo(){
        return new Pair<Integer, Integer>(m_beginOffsetVideo, m_endOffsetVideo);
    }										// выдача начального и конечного сдвига видео

    public Pair<Integer, Integer> getStartIdChunkAudio(){
        return new Pair<Integer, Integer>(m_idBeginChunkAudio, m_offsetStartAudioPos);
    }								// выдача начального ID и сдвига аудио

    public Pair<Integer, Integer> getEndIdChunkAudio(){
        return new Pair<Integer, Integer>(m_idEndChunkAudio, m_offsetEndtAudioPos);
    }									// выдача конечного ID и сдвига аудио

    public void setArrayChunkOffsetAudio( ArrayList<Integer> data){
        m_chunkOffsetAudio = data;
    }				// установка массива сдвигов чанков аудио
    public void setArrayChunkOffsetVideo( ArrayList<Integer> data){
        m_chunkOffsetVideo = data;
    }				// установка массива сдвигов чанков видео

    public ArrayList<Integer>  getArrayChunkOffsetAudio(){
        return m_chunkOffsetAudio;
    }									// выдача массива сдвигов чанков аудио
    public ArrayList<Integer>  getArrayChunkOffsetVideo(){
        return m_chunkOffsetVideo;
    }									// выдача массива сдвигов чанков видео

    public void setIDBeginChunkAudio(Integer idChunk){
        m_idBeginChunkAudio = idChunk;
    }										// установка начального ID чанка аудио
    public void setIDEndChunkAudio(Integer idChunk){
        m_idEndChunkAudio = idChunk;
    }											// установка конечного ID чанка аудио

    public void setIDBeginChunkVideo(Integer idChunk){
        m_idBeginChunkVideo = idChunk;
    }										// установка начального ID чанка видео
    public void setIDEndChunkVideo(Integer idChunk){
        m_idEndChunkVideo = idChunk;
    }											// установка конечного ID чанка видео

    public Integer getIDBeginChunkAudio(){
        return m_idBeginChunkAudio;
    }													// выдача начального ID чанка аудио
    public Integer getIDEndChunkAudio(){
        return m_idEndChunkAudio;
    }														// выдача конечного ID чанка аудио

    public Integer getIDBeginChunkVideo(){
        return m_idBeginChunkVideo;
    }													// выдача начального ID чанка видео
    public Integer getIDEndChunkVideo(){
        return m_idEndChunkVideo;
    }														// выдача конечного ID чанка видео

    public Integer getIdBegChunkWithIFrame(){
        return m_idBegChunkWithIFrame;
    }											// установка ID последнего чанка с ключевым кадром

    public Integer getIdEndChunkWithIFrame(){
        return m_idEndChunkWithIFrame;
    }											// выдача ID последнего чанка с ключевым кадром видео

    public Integer getFirstChunkAudioSize(){
        return m_firstChunkAudioSize;
    }											// выдача размера первого чанка аудио
    public void setFirstChunkAudioSize(Integer  firstChunkAudioSize){
         m_firstChunkAudioSize = firstChunkAudioSize;
    }					// установка размера первого чанка аудио

    public Integer getLastChunkAudioSize(){
        return m_lastChunkAudioSize;
    }												// выдача размера последнего чанка аудио

    public void setLastChunkAudioSize(Integer  lastChunkAudioSize){
        m_lastChunkAudioSize = lastChunkAudioSize;
    }						// установка размера последнего чанка аудио

    public Integer getAmountChunkAudio(){
        return m_amountChunkAudio;// выдача кол-ва чанков аудио
    }
    public void setAmountChunkAudio(Integer  amountChunkAudio){
        m_amountChunkAudio = amountChunkAudio;// установка кол-ва чанков аудио
    }

    public Integer getAmountChunkVideo(){
        return m_amountChunkVideo;// выдача кол-ва чанков видео
    }
    public void setAmountChunkVideo(Integer  amountChunkVideo){
        m_amountChunkVideo = amountChunkVideo;// установка кол-ва чанков видео
    }

    public Integer getLastChunkVideoSize(){
        return m_lastChunkVideoSize;// выдача размера последнего чанка видео
    }
    public void setLastChunkVideoSize( Integer  lastChunkVideoSize){
        m_lastChunkVideoSize = lastChunkVideoSize;// установка размера последнего чанка видео
    }

    public Integer getBeginOffsetFile(){
        return m_beginOffsetFile;// установка размера вырезаемого видео
    }
    public void setBeginOffsetFile( Integer beginOffsetFile){
        m_beginOffsetFile = beginOffsetFile;//
    }

    public Integer getSizeCut(){
        return m_sizeCut; // выдача размера вырезаемого видео
    }
    public void setSizeCut( Integer sizeCut){
        m_sizeCut = sizeCut;// установка размера вырезаемого видео
    }
}
