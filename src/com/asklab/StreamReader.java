package com.asklab;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class StreamReader {

    private File m_file=null;
    private SeekableByteChannel m_stream=null;
    private Long m_beginpos = Long.valueOf(0);
    private Long m_currentpos = 0L;
    private String m_filePath;

    public StreamReader(String path) throws IOException {
        m_file = new File(path);
        m_stream = Files.newByteChannel(m_file.toPath(),StandardOpenOption.READ);
        m_filePath = path;
    }

    public StreamReader(SeekableByteChannel stream){
        m_stream = stream;
    }

    private int read(ByteBuffer b)throws IOException{
        int temp = m_stream.read(b);
        return temp;
    }

    public String getTitleAtom() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        m_stream.position(m_currentpos+4);
        read(buf);
        m_stream.position(m_currentpos);
        return new String(buf.array());
    }

    public String getDataInAtom()throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(4);
        read(buf);
        return new String(buf.array());
    }

    public long readSizeAtom()throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(4);
        int temp = read(buf);
        m_stream.position(m_currentpos);
        return Integer.toUnsignedLong(buf.getInt(0));
    }

    public void setPos(long pos)throws IOException{
        m_currentpos = pos;
        m_stream.position(pos);
    }

    public long getPos(){
        return m_currentpos;
    }

    public Integer readValueUINT32()throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(4);
        int temp = read(buf);
        m_currentpos +=temp;
        return buf.getInt(0);
    }

    public long getFileSize(){
        return  m_file.length();
    }

    public void readBlock(ByteBuffer buf) throws IOException{
        int temp = read(buf);
        m_currentpos +=temp;
    }



}
