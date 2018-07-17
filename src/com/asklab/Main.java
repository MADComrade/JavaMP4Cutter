package com.asklab;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.asklab.Endians.*;
import com.asklab.StreamReader;

public class Main {

    public static void main(String[] args) {
        StreamReader reader = null;
        try {
            reader = new StreamReader("D:/file_03_05_2015_23-27-38.mp4");
            MP4Parser parser = new MP4Parser();
            parser.parse(reader);
            parser.prepareData(15,45);
            StreamWriter writer = new StreamWriter("D:/newfile.mp4");
            parser.writeFilePart(reader,writer);
            //long sizeAtom = reader.readSizeAtom();
            //String title = reader.getTitleAtom();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
