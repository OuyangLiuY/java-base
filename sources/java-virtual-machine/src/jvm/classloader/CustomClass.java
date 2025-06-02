package jvm.classloader;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class CustomClass extends ClassLoader {


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        File file = new File("E:/workspace/java-base/jvm/target/classes/com/ouyangliuy/jvm/", name.replace(".", "/") + ".class");
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = fis.read()) != 0) {
                baos.write(b);
            }
            byte[] bytes = baos.toByteArray();
            baos.close();
            fis.close();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.loadClass(name);
    }


}
