package com.asklab.atoms;

import com.asklab.Pair;
import com.asklab.SingletonSettings;
import com.asklab.StreamReader;
import com.asklab.StreamWriter;
import com.asklab.interfaces.CallbackInterface;
import com.asklab.interfaces.ParserInterface;

import java.io.IOException;

public abstract class Atom implements CallbackInterface, ParserInterface {

    //Класс atom.h----------------

    //тип данных был string
    public final static String MOOV_NAME = "moov";
    public final static String MVHD_NAME = "mvhd";
    public final static String TRAK_NAME = "trak";
    public final static String TKHD_NAME = "tkhd";
    public final static String MDIA_NAME = "mdia";
    public final static String MDHD_NAME = "mdhd";
    public final static String HDLR_NAME = "hdlr";
    public final static String MINF_NAME = "minf";
    public final static String VMHD_NAME = "vmhd";
    public final static String SMHD_NAME = "smhd";
    public final static String DINF_NAME = "dinf";
    public final static String STBL_NAME = "stbl";
    public final static String STSC_NAME = "stsc";
    public final static String STTS_NAME = "stts";
    public final static String STSD_NAME = "stsd";
    public final static String STSS_NAME = "stss";
    public final static String STCO_NAME = "stco";
    public final static String STSZ_NAME = "stsz";
    public final static String FTYP_NAME = "ftyp";
    public final static String MDAT_NAME = "mdat";

    //тип данных был uint32_t
    public final static Integer MOOV_DIG_NAME = 0x6D6F6F76;
    public final static Integer MVHD_DIG_NAME = 0X6D766864;
    public final static Integer TRAK_DIG_NAME = 0X7472616B;
    public final static Integer TKHD_DIG_NAME = 0X746B6864;
    public final static Integer MDIA_DIG_NAME = 0X6D646961;
    public final static Integer MDHD_DIG_NAME = 0X6D646864;
    public final static Integer HDLR_DIG_NAME = 0X68646C72;
    public final static Integer MINF_DIG_NAME = 0X6D696E66;
    public final static Integer VMHD_DIG_NAME = 0X766D6864;
    public final static Integer SMHD_DIG_NAME = 0X736D6864;
    public final static Integer DINF_DIG_NAME = 0X64696E66;
    public final static Integer STBL_DIG_NAME = 0X7374626C;
    public final static Integer STSC_DIG_NAME = 0X73747363;
    public final static Integer STTS_DIG_NAME = 0X73747473;
    public final static Integer STSD_DIG_NAME = 0X73747364;
    public final static Integer STSS_DIG_NAME = 0X73747373;
    public final static Integer STCO_DIG_NAME = 0X7374636F;
    public final static Integer STSZ_DIG_NAME = 0X7374737A;
    public final static Integer FTYP_DIG_NAME = 0X66747970;
    public final static Integer MDAT_DIG_NAME = 0X6D646174;

    //тип данных был uint32_t
    public final static Integer OFFSET_TITLE = 8;
    public final static Integer BYTE32 = 4;

    //тип данных был int
    public final static Integer LV1 = 0;
    public final static Integer LV2 = 10;
    public final static Integer LV3 = 15;
    public final static Integer LV4 = 20;
    public final static Integer LV5 = 25;
    public final static Integer LV6 = 30;

    //тип данных был String
    public final static String VIDE = "vide";
    public final static String SOUN = "soun";
    public final static String HInteger = "hInteger";

    //public ?
    public enum TRAK_TYPE{
        VIDEO,
        AUDIO,
        HINT
    }

    public class StscData{
        public Integer getFirstChunk() {
            return m_firstChunk;
        }

        public void setFirstChunk(Integer m_firstChunk) {
            this.m_firstChunk = m_firstChunk;
        }

        public Integer getSamplesPerChunk() {
            return m_samplesPerChunk;
        }

        public void setSamplesPerChunk(Integer m_samplesPerChunk) {
            this.m_samplesPerChunk = m_samplesPerChunk;
        }

        public Integer getSamplesIndex() {
            return m_samplesIndex;
        }

        public void setSamplesIndex(Integer m_samplesIndex) {
            this.m_samplesIndex = m_samplesIndex;
        }

        public Integer m_firstChunk = 0;
        public Integer m_samplesPerChunk = 0;
        public Integer m_samplesIndex = 0;
    }

    public class SttsData {
        public Integer getSampleCount() {
            return m_sampleCount;
        }

        public void setSampleCount(Integer m_sampleCount) {
            this.m_sampleCount = m_sampleCount;
        }

        public Integer getSampleDelta() {
            return m_sampleDelta;
        }

        public void setSampleDelta(Integer m_sampleDelta) {
            this.m_sampleDelta = m_sampleDelta;
        }

        public Integer m_sampleCount = 0;
        public Integer m_sampleDelta = 0;
    }

    protected CallbackInterface m_callback = null;
    protected Long m_offset = 0L;
    protected Long m_size = 0L;
    protected Integer m_atomDigitName= 0;
    protected String m_atomTextName;
    protected SingletonSettings m_singletonSettings = SingletonSettings.getInstance();

    //Класс atom.h закончен, если не учитывать методы----------------


    //Класс atom.cpp>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // uInteger32_t atomDigitName
    public Atom(String atomTextName, Integer atomDigitName ){
        setAtomTextName(atomTextName);
        setAtomDigitName(atomDigitName);
    }

    //uint32_t Atom::offset() const
    public Long offset(){
        return m_offset;
    }

    //const uint32_t &offset
    public void setOffset(Long offset)
    {
        m_offset = offset;
    }

    //uint32_t Atom::size() const
    public Long size(){
        return m_size;
    }

    //uint32_t Atom::atomDigitName() const
    public Integer atomDigitName() {
        return m_atomDigitName;
    }

    //std::string Atom::atomTextName() const
    public String atomTextName() {
        return m_atomTextName;
    }

    //void Atom::writeAtom(StreamWriter &stream)
    //насчет InputStream  - хз
    public abstract void writeAtom(StreamWriter stream) throws IOException;

    //void Atom::setCallback(CallbackInterface *callback)
    public void setCallback(CallbackInterface callback){
        m_callback = callback;
    }

    public abstract void prepareData ();

    //void Atom::printAtomName(int lv)
    protected void printAtomName(int lv)
    {
        String perem = "";
        for (int i = 0; i < lv; i++) {
            perem = perem + " ";
        }
        System.out.println(perem + atomTextName());
    }

    //CallbackInterface *Atom::callback() const
    protected CallbackInterface callback() {
        return m_callback;
    }

    //void Atom::setAtomTextName(const std::string &atomTextName)
    protected void setAtomTextName(String atomTextName){
        m_atomTextName = atomTextName;
    }

    //void Atom::setAtomDigitName(const uint32_t &atomDigitName)
    protected void setAtomDigitName(Integer atomDigitName){
        m_atomDigitName = atomDigitName;
    }

    //void Atom::resizeAtom(uint32_t size, DIRECT_RESIZE direction)
    public abstract void resizeAtom(Integer size,DIRECT_RESIZE direction);

    public abstract void parse(StreamReader stream, Long startPos)throws IOException;
}
