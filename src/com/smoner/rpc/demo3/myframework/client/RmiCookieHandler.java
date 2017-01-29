package com.smoner.rpc.demo3.myframework.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by smoner on 2017/1/29.
 */

public class RmiCookieHandler extends CookieHandler {

    private Map<URI, List<HttpCookie>> uriIndex;

    private Map<URI, List<HttpCookie>> hostIndex;

    public RmiCookieHandler() {
        uriIndex = new HashMap<URI, List<HttpCookie>>();
        hostIndex = new HashMap<URI, List<HttpCookie>>();
    }

    @Override
    public Map<String, List<String>> get(URI uri,
                                         Map<String, List<String>> requestHeaders) throws IOException {
        if (uri == null || requestHeaders == null) {
            throw new IllegalArgumentException("Argument is null");
        }

        Map<String, List<String>> cookieMap = new java.util.HashMap<String, List<String>>();

        List<HttpCookie> cookies = new java.util.ArrayList<HttpCookie>();
        List<HttpCookie> found = uriIndex.get(uri);

        if (found != null) {
            for (HttpCookie cookie : found) {
                if (!cookie.hasExpired()) {
                    cookies.add(cookie);
                }
            }
        }
        found = hostIndex.get(getEffectiveUri(uri));

        if (found != null) {
            for (HttpCookie cookie : found) {
                if (!cookie.hasExpired() && !cookies.contains(cookie)
                        && pathMatches(uri.getPath(), cookie.getPath())) {
                    cookies.add(cookie);
                }
            }
        }
        List<String> cookieHeader = sortByPath(cookies);
        cookieMap.put("Cookie", cookieHeader);
        return Collections.unmodifiableMap(cookieMap);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders)
            throws IOException {

        if (uri == null || responseHeaders == null) {
            throw new IllegalArgumentException("Argument is null");
        }

        for (String headerKey : responseHeaders.keySet()) {
            if (headerKey == null
                    || !(headerKey.equalsIgnoreCase("Set-Cookie2") || headerKey
                    .equalsIgnoreCase("Set-Cookie"))) {
                continue;
            }

            for (String headerValue : responseHeaders.get(headerKey)) {
                try {
                    List<HttpCookie> cookies = HttpCookie.parse(headerValue);
                    uriIndex.put(uri, cookies);
                    URI hostUri = getEffectiveUri(uri);
                    HttpCookie hc = findCookie("JSESSIONID", hostIndex
                            .get(hostUri));

                    List<HttpCookie> hostCookies = new ArrayList<HttpCookie>();

                    for (HttpCookie c : cookies) {
                        if (hc == null || hc.hasExpired() || !hc.equals(c)) {
                            hostCookies.add(c);
                        } else {
                            // preserve the old
                            hostCookies.add(hc);
                        }
                    }

                    hostIndex.put(hostUri, hostCookies);

                } catch (IllegalArgumentException e) {
                }
            }
        }

    }

    private List<String> sortByPath(List<HttpCookie> cookies) {
        Collections.sort(cookies, new CookiePathComparator());
        List<String> cookieHeader = new java.util.ArrayList<String>();
        for (HttpCookie cookie : cookies) {
            if (cookies.indexOf(cookie) == 0 && cookie.getVersion() > 0) {
                cookieHeader.add("$Version=\"1\"");
            }

            cookieHeader.add(cookie.toString());
        }
        return cookieHeader;
    }

    private boolean pathMatches(String path, String pathToMatchWith) {
        if (path == pathToMatchWith)
            return true;
        if (path == null || pathToMatchWith == null)
            return false;
        if (path.startsWith(pathToMatchWith))
            return true;

        return false;
    }

    static class CookiePathComparator implements Comparator<HttpCookie> {
        public int compare(HttpCookie c1, HttpCookie c2) {
            if (c1 == c2)
                return 0;
            if (c1 == null)
                return -1;
            if (c2 == null)
                return 1;
            if (!c1.getName().equals(c2.getName()))
                return 0;
            if (c1.getPath().startsWith(c2.getPath()))
                return -1;
            else if (c2.getPath().startsWith(c1.getPath()))
                return 1;
            else
                return 0;
        }
    }

    private URI getEffectiveUri(URI uri) {
        URI effectiveURI = null;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getHost(), null, null,
                    null);
        } catch (URISyntaxException ignored) {
            effectiveURI = uri;
        }

        return effectiveURI;
    }

    private HttpCookie findCookie(String name, List<HttpCookie> cookies) {
        if (cookies == null) {
            return null;
        }
        for (HttpCookie c : cookies) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

}