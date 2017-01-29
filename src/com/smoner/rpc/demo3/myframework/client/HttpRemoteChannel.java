package com.smoner.rpc.demo3.myframework.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by smoner on 2017/1/29.
 */

public class HttpRemoteChannel implements RemoteChannel {
    private URL url;
    private HttpURLConnection conn;
    private Proxy proxy;
    private RCInputStream rcInput;
    private RCOutputStream rcOutput;

    public HttpRemoteChannel(URL url, Proxy proxy) {
        this.url = url;
        this.proxy = proxy;
    }

    @Override
    public void destroy() {
        if (rcOutput != null) {
            try {
                rcOutput.close();
            } catch (IOException e) {
            }
        }
        if (rcInput != null) {
            try {
                rcInput.close();
            } catch (IOException e) {
            }
        }
        if (conn != null) {
            conn.disconnect();
        }
        conn = null;
        rcInput = null;
        rcOutput = null;
    }

    @Override
    public InputStream getInputStream() {
        return rcInput;
    }

    @Override
    public OutputStream getOutputStream() {
        return rcOutput;
    }

    @Override
    public void init() throws IOException {
        String protocol = url.getProtocol();
        if (!(protocol.equalsIgnoreCase("http")
                || protocol.equalsIgnoreCase("https"))) {
            throw new IOException("Illegal Protocol " + url.getProtocol());
        }
        if (conn != null) {
            throw new IllegalStateException(
                    "attempt to reprepare remote channel for write: " + url);
        }

        if (proxy != null) {
            conn = (HttpURLConnection) url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/octet-stream");
        conn.setChunkedStreamingMode(-1);
        rcInput = new RCInputStream();
        rcOutput = new RCOutputStream();
    }

    @Override
    public void processIOException(IOException ioe) throws IOException {
        if (ioe instanceof Exception) {
            CookieHandler.setDefault(newCookieManager());
        } else if (conn.getResponseCode() > 500) {
            CookieHandler.setDefault(newCookieManager());
        }
        InputStream es = ((HttpURLConnection) conn).getErrorStream();

        if (es != null) {
            byte[] buf = new byte[4096];
            while (es.read(buf) > 0) {

            }
            es.close();
        }

    }

    class RCInputStream extends InputStream {
        private InputStream in;

        public void deactivate() {
            in = null;
        }

        public int read() throws IOException {
            if (in == null)
                in = readPrepare();
            return in.read();
        }

        public int read(byte b[], int off, int len) throws IOException {
            if (len == 0)
                return 0;
            if (in == null)
                in = readPrepare();
            return in.read(b, off, len);
        }

        public long skip(long n) throws IOException {
            if (n == 0)
                return 0;
            if (in == null)
                in = readPrepare();
            return in.skip(n);
        }

        public int available() throws IOException {
            if (in == null)
                in = readPrepare();
            return in.available();
        }

        public void close() throws IOException {
            if (in != null) {
                in.close();
            }
        }

        public synchronized void mark(int readlimit) {
            if (in == null) {
                try {
                    in = readPrepare();
                } catch (IOException e) {
                    return;
                }
            }
            in.mark(readlimit);
        }

        public synchronized void reset() throws IOException {
            if (in == null)
                in = readPrepare();
            in.reset();
        }

        public boolean markSupported() {
            if (in == null) {
                try {
                    in = readPrepare();
                } catch (IOException e) {
                    return false;
                }
            }
            return in.markSupported();
        }

        private InputStream readPrepare() throws IOException {
            rcOutput.deactivate();
            in = conn.getInputStream();
            conn.getContentType();
//			String contentType = conn.getContentType();
//			if (contentType == null
//					|| !(contentType.equals("application/octet-stream") || contentType
//							.equals("application/x-java-serialized-object"))) {
//				throw new IOException("request failed, invalid content-type: "
//						+ contentType);
//			}
            return in;
        }
    }

    class RCOutputStream extends OutputStream {

        private OutputStream out;

        public RCOutputStream() throws IOException {
            writePrepare();
        }

        public void deactivate() {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            out = null;
        }

        public void write(int b) throws IOException {
            if (out == null) {
                throw new IllegalStateException(
                        "inactive remote outputstream: " + url);
            }
            out.write(b);
        }

        public void write(byte b[], int off, int len) throws IOException {
            if (len == 0)
                return;
            if (out == null)
                throw new IllegalStateException(
                        "inactive remote outputstream: " + url);
            out.write(b, off, len);
        }

        public void flush() throws IOException {
            if (out != null) {
                out.flush();
            }
        }

        public void close() throws IOException {
            if (out != null) {
                out.flush();
                out.close();
            }
        }

        private void writePrepare() throws IOException {
            rcInput.deactivate();
            out = conn.getOutputStream();
        }
    }

    public static CookieHandler newCookieManager() {
        return new RmiCookieHandler();
    }

}