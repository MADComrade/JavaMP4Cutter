package com.asklab.interfaces;

import com.asklab.StreamReader;

import java.io.IOException;


public interface ParserInterface {
    public void parse(StreamReader stream, Long startPos)throws IOException;
}
