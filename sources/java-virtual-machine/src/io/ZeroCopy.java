package io;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class ZeroCopy {

    public static void main(String[] args) {

        mmap("这个是一个零拷贝");
    }

    public static void mmap(String str) {
        File file = new File("D:\\abc.txt");
        try {
            FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
            MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_WRITE, 0, file.length());
            map.put(str.getBytes(StandardCharsets.UTF_8));
            fc.position(file.length());
            map.clear();
            fc.write(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
