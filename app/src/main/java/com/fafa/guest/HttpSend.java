package com.fafa.guest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class HttpSend implements Runnable {

    /**
     * 发送get请求
     *
     * @param address
     * @param callback
     */
    private void get(final String address, final Callback<Object, Object> callback) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            System.out.println(address);
            URL url = new URL(address);

            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("GET");
            // 设置连接超时时间（毫秒）
            connection.setConnectTimeout(5000);
            //设置读取超时时间（毫秒）
            connection.setReadTimeout(5000);

            sendRep(connection, callback);
        } catch (IOException e) {
            e.printStackTrace();
            callback.call("0:" + e.getMessage());
        } finally {
            if (connection != null) {
                //关闭连接
                connection.disconnect();
            }
        }
    }

    /**
     * 发送pos数据请求
     *
     * @param address
     * @param params
     * @param callback
     */
    public void post(final String address,
                     final Map<String, String> params,
                     final Callback<Object, Object> callback) {

        HttpURLConnection conn = null;
        try {
            //1：url对象
            URL url = new URL(address);
            //2;url.openconnection
            conn = (HttpURLConnection) url.openConnection();
            //3设置请求参数
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(30 * 1000);
            StringBuilder sb = new StringBuilder();
            if (params != null || params.size() != 0) {
                Set<String> set = params.keySet();
                for (String s : set) {
                    sb.append(s).append("=").append(params.get(s)).append("&");
                }
            }
            //请求头的信息
            String body = "";
            if (!sb.toString().equals("")) {
                body = sb.toString().substring(0, sb.length() - 1);
            }

//            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
            conn.setRequestProperty("Content-Type", "application/json");
            //设置conn可以写请求的内容
            conn.setDoOutput(true);
            JSONObject jo = new JSONObject(params);
            System.out.println(jo);
            conn.getOutputStream().write(jo.toString().getBytes());
            //4响应码
            sendRep(conn, callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.call("0:" + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void sendRep(HttpURLConnection conn, Callback<Object, Object> callback) throws IOException {
        int code = conn.getResponseCode();
        if (code == 200) {
            String o = inputStream2String(conn.getInputStream());
            callback.call(o);
        } else {
            callback.call("0:网络错误");
        }
    }

    private static String inputStream2String(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        int n;
        while ((n = in.read(b)) != -1) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * 访问方法
     */
    private String method;

    private Map<String, String> params;

    private String address;

    private String key;

    private Callback<Object, Object> callback;

    public HttpSend(String method, String address) {
        if (method == null || address == null) {
            throw new IllegalArgumentException("请求方法不可以为空");
        }
        this.method = method;
    }

    public HttpSend(String method, String address, Callback<Object, Object> callback) {
        this(method, address);
        this.address = address;
        this.key = "";
        this.callback = callback;
    }

    public HttpSend(String method, String address, String key, Callback<Object, Object> callback) {
        this(method, address);
        this.address = address;
        this.key = (key == null ? "" : key);
        this.callback = callback;
    }

    public HttpSend(String method, Map<String, String> params, String address, Callback<Object, Object> callback) {
        this(method, address);
        this.params = params;
        this.address = address;
        this.key = "";
        this.callback = callback;
    }

    public HttpSend(String method, Map<String, String> params, String address, String key, Callback<Object, Object> callback) {
        this(method, address);
        this.params = params;
        this.address = address;
        this.key = (key == null ? "" : key);
        this.callback = callback;
    }

    @Override
    public void run() {
        switch (this.method) {
            case "get":
                this.get(address, callback);
                break;
            case "post":
                this.post(address, params, callback);
                break;
            default:
                throw new RuntimeException("未支持的方法");
        }
    }
}
