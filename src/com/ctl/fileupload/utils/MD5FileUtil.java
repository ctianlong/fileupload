package com.ctl.fileupload.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
  
public class MD5FileUtil {
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    protected static MessageDigest messagedigest = null;
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("MD5FileUtil messagedigest初始化失败");
            System.out.println(e);
        }
    }

    //MappedByteBuffer无法释放，占资源，不推荐
//    public static String getFileMD5String(File file) throws IOException {
//        FileInputStream in = new FileInputStream(file);
//        FileChannel ch = in.getChannel();
//        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
//                file.length());
//        messagedigest.update(byteBuffer);
//        in.close();
//        return bufferToHex(messagedigest.digest());
//    }
    
    public static String getFileMD5String(File file) throws IOException {
    	FileInputStream fis = null;
		fis = new FileInputStream(file);
		byte[] buffer = new byte[102400];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            messagedigest.update(buffer, 0, length);
        }
		if(fis != null){
			fis.close();
		}
		return bufferToHex(messagedigest.digest());	
    }
    
    public static String getFileMD5String(InputStream in) throws IOException {
		byte[] buffer = new byte[102400];
		int length;
		while ((length = in.read(buffer)) != -1) {
		    messagedigest.update(buffer, 0, length);
		}
		return bufferToHex(messagedigest.digest());
    }
 
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }
 
    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }
 
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }
 
    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }
 
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
 
    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }
 
    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
 
        File big = new File("F:\\03.jpg");
        String md5 = getFileMD5String(big);
        
        //String md5 = getMD5String("hello");
 
        long end = System.currentTimeMillis();
        System.out.println("md5:" + md5);
        System.out.println("time:" + (end - begin) + "ms");
        System.out.println("time:" + ((end - begin) / 1000) + "s");
    }
 
}