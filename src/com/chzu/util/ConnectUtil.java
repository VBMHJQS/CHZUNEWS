package com.chzu.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectUtil {
	/**
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) throws Exception  
    {  
        StringBuffer sb = new StringBuffer();  
        try  
        {  
            URL url = new URL(urlStr);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setRequestMethod("GET");  
            conn.setConnectTimeout(5000);  
            conn.setDoInput(true);  
            conn.setDoOutput(true);  
  
            if (conn.getResponseCode() == 200)  
            {  
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");  //ע����һ�㣬���׳�����������
                int len = 0;  
                char[] buf = new char[1024];  
  
                while ((len = isr.read(buf)) != -1)  
                {  
                    sb.append(new String(buf, 0, len));  
                }  
                is.close();  
                isr.close();  
            } else  
            {  
                throw new Exception("��������ʧ�ܣ�");  
            }  
  
        } catch (Exception e)  
        {  
            throw new Exception("��������ʧ�ܣ�");  
        }  
        return sb.toString();  
    }  
}
