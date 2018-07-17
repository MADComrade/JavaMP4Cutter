package com.asklab;

import java.io.*;
import com.asklab.Endians.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static com.asklab.Endians.intToBytes;

public class StreamWriter {
    private File m_file=null;
    private OutputStream m_stream=null;
    private Long m_beginpos = Long.valueOf(0);
    private Long m_currentpos = 0L;
    private String m_filePath;

    public StreamWriter(String path) throws FileNotFoundException {
        //m_file = new File(path);
        m_stream = new FileOutputStream(path);// Files.newByteChannel(m_file.toPath(),StandardOpenOption.READ);
        m_filePath = path;
    }

    private void write(byte[] b)throws IOException{
        m_stream.write(b);
    }

    public void writeValueUINT32(Integer val)throws IOException{
        byte[] b = intToBytes(val);
        write(b);
    }

    public void writeTitleAtom(String val)throws IOException{
        byte[] b = new byte[4];
        System.arraycopy(val.getBytes(),0, b, 0, 4);
        write(b);
    }

    public void writeBlock(byte[] b)throws IOException{
        write(b);
    }

}
