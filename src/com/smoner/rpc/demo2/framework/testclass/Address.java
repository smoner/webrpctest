package com.smoner.rpc.demo2.framework.testclass;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by smoner on 2017/1/29.
 */
public final class Address implements Serializable {

    private static final long serialVersionUID = 3982995147110832680L;

    private String authority = null;

    private String file = null;

    private String host = null;

    private String path = null;

    private int port = -1;

    private String protocol = null;

    private String query = null;

    private String ref = null;

    private String userInfo = null;

    public Address(String spec) throws MalformedURLException {

        this(null, spec);

    }

    public Address(Address context, String spec) throws MalformedURLException {

        String original = spec;
        int i, limit, c;
        int start = 0;
        String newProtocol = null;
        boolean aRef = false;

        try {

            // Eliminate leading and trailing whitespace
            limit = spec.length();
            while ((limit > 0) && (spec.charAt(limit - 1) <= ' ')) {
                limit--;
            }
            while ((start < limit) && (spec.charAt(start) <= ' ')) {
                start++;
            }

            // If the string representation starts with "url:", skip it
            if (spec.regionMatches(true, start, "url:", 0, 4)) {
                start += 4;
            }

            // Is this a ref relative to the context URL?
            if ((start < spec.length()) && (spec.charAt(start) == '#')) {
                aRef = true;
            }

            // Parse out the new protocol
            for (i = start; !aRef && (i < limit)
                    && ((c = spec.charAt(i)) != '/'); i++) {
                if (c == ':') {
                    String s = spec.substring(start, i).toLowerCase(
                            Locale.ENGLISH);
                    // Assume all protocols are valid
                    newProtocol = s;
                    start = i + 1;
                    break;
                } else if (c == '#') {
                    aRef = true;
                } else if (c == '?') {
                    break;
                }
            }

            // Only use our context if the protocols match
            protocol = newProtocol;
            if ((context != null)
                    && ((newProtocol == null) || newProtocol
                    .equalsIgnoreCase(context.getProtocol()))) {
                // If the context is a hierarchical URL scheme and the spec
                // contains a matching scheme then maintain backwards
                // compatibility and treat it as if the spec didn't contain
                // the scheme; see 5.2.3 of RFC2396
                if ((context.getPath() != null)
                        && (context.getPath().startsWith("/")))
                    newProtocol = null;
                if (newProtocol == null) {
                    protocol = context.getProtocol();
                    authority = context.getAuthority();
                    userInfo = context.getUserInfo();
                    host = context.getHost();
                    port = context.getPort();
                    file = context.getFile();
                    int question = file.lastIndexOf("?");
                    if (question < 0)
                        path = file;
                    else
                        path = file.substring(0, question);
                }
            }

            if (protocol == null)
                throw new MalformedURLException("no protocol: " + original);

            // Parse out any ref portion of the spec
            i = spec.indexOf('#', start);
            if (i >= 0) {
                ref = spec.substring(i + 1, limit);
                limit = i;
            }

            // Parse the remainder of the spec in a protocol-specific fashion
            parse(spec, start, limit);
            if (context != null)
                normalize();

        } catch (MalformedURLException e) {
            throw e;
        } catch (Exception e) {
            throw new MalformedURLException(e.toString());
        }

    }

    public Address(String protocol, String host, String file)
            throws MalformedURLException {

        this(protocol, host, -1, file);

    }

    public Address(String protocol, String host, int port, String file)
            throws MalformedURLException {

        this.protocol = protocol;
        this.host = host;
        this.port = port;

        int hash = file.indexOf('#');
        this.file = hash < 0 ? file : file.substring(0, hash);
        this.ref = hash < 0 ? null : file.substring(hash + 1);
        int question = file.lastIndexOf('?');
        if (question >= 0) {
            query = file.substring(question + 1);
            path = file.substring(0, question);
        } else
            path = file;

        if ((host != null) && (host.length() > 0))
            authority = (port == -1) ? host : host + ":" + port;

    }

    public boolean equals(Object obj) {

        if (obj == null)
            return (false);
        if (!(obj instanceof Address))
            return (false);
        Address other = (Address) obj;
        if (!sameFile(other))
            return (false);
        return (compare(ref, other.getRef()));

    }

    public int hashCode() {

        int hashCode = 0;

        if (getProtocol() != null) {
            hashCode += getProtocol().hashCode();
        }

        if (getHost() != null) {
            hashCode += getHost().hashCode();
        }

        hashCode += getPort();

        if (getFile() != null) {
            hashCode += getFile().hashCode();
        }

        if (getRef() != null) {
            hashCode += getRef().hashCode();
        }

        return hashCode;
    }

    public String getAuthority() {

        return (this.authority);

    }

    public String getFile() {

        if (file == null)
            return ("");
        return (this.file);

    }

    public String getHost() {

        return (this.host);

    }

    public String getPath() {

        if (this.path == null)
            return ("");
        return (this.path);

    }

    public int getPort() {

        return (this.port);

    }

    public String getProtocol() {

        return (this.protocol);

    }

    /**
     * Return the query part of the URL.
     */
    public String getQuery() {

        return (this.query);

    }

    /**
     * Return the reference part of the URL.
     */
    public String getRef() {

        return (this.ref);

    }

    public String getUserInfo() {

        return (this.userInfo);

    }

    public void normalize() throws MalformedURLException {

        // Special case for null path
        if (path == null) {
            if (query != null)
                file = "?" + query;
            else
                file = "";
            return;
        }

        // Create a place for the normalized path
        String normalized = path;
        if (normalized.equals("/.")) {
            path = "/";
            if (query != null)
                file = path + "?" + query;
            else
                file = path;
            return;
        }

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index)
                    + normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index)
                    + normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                throw new MalformedURLException(
                        "Invalid relative URL reference");
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2)
                    + normalized.substring(index + 3);
        }

        // Resolve occurrences of "/." at the end of the normalized path
        if (normalized.endsWith("/."))
            normalized = normalized.substring(0, normalized.length() - 1);

        // Resolve occurrences of "/.." at the end of the normalized path
        if (normalized.endsWith("/..")) {
            int index = normalized.length() - 3;
            int index2 = normalized.lastIndexOf('/', index - 1);
            if (index2 < 0)
                throw new MalformedURLException(
                        "Invalid relative URL reference");
            normalized = normalized.substring(0, index2 + 1);
        }

        // Return the normalized path that we have completed
        path = normalized;
        if (query != null)
            file = path + "?" + query;
        else
            file = path;

    }

    public boolean sameFile(Address other) {

        if (!compare(protocol, other.getProtocol()))
            return (false);
        if (!compare(host, other.getHost()))
            return (false);
        if (port != other.getPort())
            return (false);
        if (!compare(file, other.getFile()))
            return (false);
        return (true);

    }

    public String toExternalForm() {

        StringBuilder sb = new StringBuilder();
        if (protocol != null) {
            sb.append(protocol);
            sb.append(":");
        }
        if (authority != null) {
            sb.append("//");
            sb.append(authority);
        }
        if (path != null)
            sb.append(path);
        if (query != null) {
            sb.append('?');
            sb.append(query);
        }
        if (ref != null) {
            sb.append('#');
            sb.append(ref);
        }
        return (sb.toString());

    }

    public String toString() {
        return (toExternalForm());
    }

    private boolean compare(String first, String second) {

        if (first == null) {
            if (second == null)
                return (true);
            else
                return (false);
        } else {
            if (second == null)
                return (false);
            else
                return (first.equals(second));
        }

    }

    private void parse(String spec, int start, int limit)
            throws MalformedURLException {

        // Trim the query string (if any) off the tail end
        int question = spec.lastIndexOf('?', limit - 1);
        if ((question >= 0) && (question < limit)) {
            query = spec.substring(question + 1, limit);
            limit = question;
        } else {
            query = null;
        }

        // Parse the authority section
        if (spec.indexOf("//", start) == start) {
            int pathStart = spec.indexOf("/", start + 2);
            if ((pathStart >= 0) && (pathStart < limit)) {
                authority = spec.substring(start + 2, pathStart);
                start = pathStart;
            } else {
                authority = spec.substring(start + 2, limit);
                start = limit;
            }
            if (authority.length() > 0) {
                int at = authority.indexOf('@');
                if (at >= 0) {
                    userInfo = authority.substring(0, at);
                }
                int ipv6 = authority.indexOf('[', at + 1);
                int hStart = at + 1;
                if (ipv6 >= 0) {
                    hStart = ipv6;
                    ipv6 = authority.indexOf(']', ipv6);
                    if (ipv6 < 0) {
                        throw new MalformedURLException(
                                "Closing ']' not found in IPV6 address: "
                                        + authority);
                    } else {
                        at = ipv6 - 1;
                    }
                }

                int colon = authority.indexOf(':', at + 1);
                if (colon >= 0) {
                    try {
                        port = Integer.parseInt(authority.substring(colon + 1));
                    } catch (NumberFormatException e) {
                        throw new MalformedURLException(e.toString());
                    }
                    host = authority.substring(hStart, colon);
                } else {
                    host = authority.substring(hStart);
                    port = -1;
                }
            }
        }

        // Parse the path section
        if (spec.indexOf("/", start) == start) { // Absolute path
            path = spec.substring(start, limit);
            if (query != null)
                file = path + "?" + query;
            else
                file = path;
            return;
        }

        // Resolve relative path against our context's file
        if (path == null) {
            if (query != null)
                file = "?" + query;
            else
                file = null;
            return;
        }
        if (!path.startsWith("/"))
            throw new MalformedURLException("Base path does not start with '/'");
        if (!path.endsWith("/"))
            path += "/../";
        path += spec.substring(start, limit);
        if (query != null)
            file = path + "?" + query;
        else
            file = path;
        return;

    }

    private transient URL url;

    public URL toURL() throws MalformedURLException {
        if (url == null)
            url = new URL(toString());
        return url;
    }

}
