package ir.codetower.samanshiri.Helpers;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import ir.codetower.samanshiri.App;

/**
 * Created by Mr-R00t on 8/6/2017.
 */

public class Util {
    public static Bitmap resizeBitmap(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        double res = ((double) height / (double) width * (double) newWidth);
        int newHeight = (int) res;

        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
    }
    public static Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int newWidth=800;
        double res = ((double) height / (double) width * (double) newWidth);
        int newHeight = (int) res;
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
    }
    public static File writeToFile(Bitmap bitmap, File targetFile, int quality, boolean change) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayOutputStream bosT = new ByteArrayOutputStream();
        if(change){
            bitmap=getResizedBitmap(bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        }
        else{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        }
        App.makeDirectories();
        Bitmap tBitmap=resizeBitmap(bitmap,200);
        tBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bosT);

        byte[] bitmapdata = bos.toByteArray();
        byte[] bitmapTdata = bosT.toByteArray();
        targetFile.getParentFile().mkdirs();
        FileOutputStream fosT=new FileOutputStream(targetFile.getParentFile()+"/"+targetFile.getName()+"-thumb");
        FileOutputStream fos = new FileOutputStream(targetFile);
        fos.write(bitmapdata);
        fosT.write(bitmapTdata);
        fos.flush();
        fosT.flush();
        fos.close();
        fosT.close();
        return targetFile;
    }
    public static String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[1024];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }
    public static String getBase64String(String text){
        return Base64.encodeToString(text.getBytes(), Base64.NO_WRAP);
    }
    public static String formatDuration(long duration) {
        int seconds = (int) (duration / 1000);
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(Locale.ENGLISH, "%02d", seconds);
    }

}
