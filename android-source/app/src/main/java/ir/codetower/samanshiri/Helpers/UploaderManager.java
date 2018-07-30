package ir.codetower.samanshiri.Helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;


public class UploaderManager {
        public static String uploadUrl;
        public static String source;
        public static HashMap<String,String> params;
        public OnProgress onProgress;
        public int toUser = -1;
        public static int timeOutCount=5;
        private DataOutputStream dataOutputStream;
        private String lineEnd = "\r\n";
        private String twoHyphens = "--";
        private String boundary = "*****";


        public String upload() {
            final String[] result = {""};
            final File sourceFile = new File(source);
            if (!sourceFile.isFile()) {

            } else {
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection connection;
                            int bytesAvailable, bufferSize;
                            byte[] buffer;
                            FileInputStream fileInputStream = new FileInputStream(sourceFile);
                            URL url = new URL(uploadUrl);
                            Log.w("url", url.toString());

                            connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setUseCaches(false);
                            connection.setChunkedStreamingMode(1024);
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Connection", "Keep-alive");
                            connection.setRequestProperty("Cache-Control", "no-cache");
                            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                            dataOutputStream = new DataOutputStream(connection.getOutputStream());

                            addFormFiled("sh2", Configurations.sh2);
                            addFormFiled("sh1", App.prefManager.getPreference("sh1"));
                            addFormFiled("username",App.prefManager.getPreference("username"));
                            addFormFiled("password",App.prefManager.getPreference("password"));
                            for (Map.Entry<String,String> item: params.entrySet()) {
                                addFormFiled(item.getKey(),item.getValue());
                            }

                            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file_param\";filename=\"" + sourceFile.getName() + "\"" + lineEnd);
                            dataOutputStream.writeBytes(lineEnd);

                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, 1024);
                            buffer = new byte[bufferSize];
                            long filesize = sourceFile.length();
                            int percent = 0;
                            while (fileInputStream.read(buffer, 0, bufferSize) > 0) {
                                dataOutputStream.write(buffer, 0, bufferSize);

                                bytesAvailable = fileInputStream.available();
                                percent = 100 - (int) (100 * (double) bytesAvailable / (double) filesize);
                                if (onProgress != null) {
                                    onProgress.onProgress(percent);
                                }
                                bufferSize = Math.min(bytesAvailable, 1024);
                            }

                            dataOutputStream.writeBytes(lineEnd);
                            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            fileInputStream.close();
                            dataOutputStream.flush();
                            dataOutputStream.close();

                            connection.connect();
                            InputStream inputStream = connection.getInputStream();
                            result[0] = convertInputStreamToString(inputStream);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }catch (EOFException e){
                            result[0]=retryUpload();
                            e.printStackTrace();
                        }
                        catch (SocketException e) {
                            result[0]=retryUpload();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });

                thread.start();

                while (thread.isAlive()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.w("result",result[0]);
            return result[0];
        }
        public void addFormFiled(String fieldName, String value) throws IOException {
            dataOutputStream.writeBytes(twoHyphens+boundary+lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""+fieldName+"\"" + lineEnd);
            dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" +lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(value+lineEnd);
            dataOutputStream.flush();
        }
        private String retryUpload() {
            final String[] result = {""};
            if(timeOutCount>0) {
                timeOutCount--;

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                Thread tempThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UploaderManager uploadManager = new UploaderManager();
                        uploadManager.toUser = toUser;
                        uploadManager.source = source;
                        uploadManager.onProgress = onProgress;
                        result[0] =uploadManager.upload();
                    }
                });

                tempThread.start();
                while (tempThread.isAlive()) ;

            }
            return result[0];
        }

        public static String convertInputStreamToString(InputStream inputStream) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        public interface OnProgress {
            public void onProgress(int percent);
        }
}
