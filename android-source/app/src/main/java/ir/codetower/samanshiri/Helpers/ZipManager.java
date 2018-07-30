package ir.codetower.samanshiri.Helpers;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipManager {
    private int BUFFER=1024;

    public void zip(HashMap<String,String> _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[1024];



            for (Map.Entry<String,String> item : _files.entrySet()) {

                FileInputStream fi = new FileInputStream(item.getValue());
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry;
                if(!item.getKey().contains(".")) {
                    entry = new ZipEntry(item.getKey() + ".jpg");
                }
                else{
                    entry = new ZipEntry(item.getKey());
                }
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
