package com.asklab;

import java.nio.ByteBuffer;

public class Endians {

    static  int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    static byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    static Integer swapEndians(Integer i){
        return  Integer.reverseBytes(i);
    }

    static Integer toBigEndian(Integer i){
        if(isBigEndian()){
            return i;
        }
        else
            return Integer.reverseBytes(i);
    }

    static Integer toLittleEndian(Integer i){
        if(!isBigEndian()){
            return i;
        }
        else
            return Integer.reverseBytes(i);
    }

    static boolean isBigEndian(){
        Integer a = 1;
        byte[] c = intToBytes(a);
        return  (c[0]==1);
    }

}
