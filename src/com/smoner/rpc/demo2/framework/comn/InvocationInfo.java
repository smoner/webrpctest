package com.smoner.rpc.demo2.framework.comn;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class InvocationInfo implements Externalizable {

    private static final long serialVersionUID = -2872276266073304000L;

    private String module;

    private String servicename = null;

    private String methodname = null;

    private Object[] parameters = null;

    private Class<?>[] parametertypes = null;

    private String userDataSource;

    private String clientHost;

    private String langCode;

    private String userId = null;

    private String groupId;

    private byte sysid;

    private String groupNumber;

    // for tool
    private String callId;

    // for tool
    private String logLevel;

    private String callServer;

    private String hyCode;

    private String bizCenterCode;
    private String busiAction;
    private long bizDateTime;
    private String deviceId;

    private String callPath;

    private String runAs;

    private String timeZone;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    private String userCode;

    public InvocationInfo() {
    }

    public InvocationInfo(String module, String serviceName, String methodName,
                          Class<?>[] parameterTypes, Object[] parameters, String clientHost) {
        this(serviceName, methodName, parameterTypes, parameters, clientHost);
        this.module = module;
    }

    public InvocationInfo(String servicename, String methodname,
                          Class<?>[] parametertypes, Object[] parameters, String clientHost) {
        this.servicename = servicename;
        this.methodname = methodname;
        this.parameters = parameters;
        this.parametertypes = parametertypes;
        this.clientHost = clientHost;
    }

    public String getServiceName() {
        return servicename;
    }

    public String getMethodName() {
        return methodname;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Class<?>[] getParametertypes() {
        return parametertypes;
    }

    public String getClientHost() {
        return clientHost;
    }

    public String getUserDataSource() {
        return userDataSource;
    }

    public void setUserDataSource(String ds) {
        if (null == ds) {
            //Logger.error(new Exception("datasource is set null!"));
        }
        this.userDataSource = ds;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userCode) {
        this.userId = userCode;
    }

    public void setServiceName(String serviceName) {
        this.servicename = serviceName;
    }

    public void setMetodName(String methodName) {
        this.methodname = methodName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        module = readString(in);
        servicename = readString(in);
        methodname = readString(in);
        userDataSource = readString(in);
        langCode = readString(in);
        userId = readString(in);
        groupId = readString(in);
        groupNumber = readString(in);
        hyCode = readString(in);
        clientHost = readString(in);
        bizCenterCode = readString(in);
        sysid = in.readByte();

        logLevel = readString(in);
        callId = readString(in);

        callServer = readString(in);
        busiAction = readString(in);
        bizDateTime = in.readLong();
        deviceId = readString(in);
        callPath = readString(in);
        runAs = readString(in);
        timeZone = readString(in);
        userCode = readString(in);
        boolean atServer =true;
        InvocationInfo old = null;
        if (atServer) {
            old = InvocationInfoProxy.getInstance().getInvocationInfo();
            if (old != this) {
                InvocationInfoProxy.getInstance().setInvocationInfo(this);
            }

        }
        try {
            Object obj = in.readObject();
            if (obj != null) {
                parametertypes = (Class[]) obj;
                parameters = (Object[]) in.readObject();
            }
        } finally {
/*            if (atServer) {
                if (old != this) {
                    InvocationInfoProxy.getInstance().setInvocationInfo(this);
                }
            }*/
        }

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        writeString(out, module);
        writeString(out, servicename);
        writeString(out, methodname);
        writeString(out, userDataSource);
        writeString(out, langCode);
        writeString(out, userId);
        writeString(out, groupId);
        writeString(out, groupNumber);
        writeString(out, hyCode);
        writeString(out, clientHost);
        writeString(out, bizCenterCode);
        out.write(sysid);

        // TODO:NEED HGY TO AUDIT.
        writeString(out, logLevel);
        writeString(out, callId);
        writeString(out, callServer);
        writeString(out, busiAction);
        out.writeLong(bizDateTime);
        writeString(out, deviceId);
        writeString(out, callPath);
        writeString(out, runAs);
        writeString(out, timeZone);
        writeString(out, userCode);

        if (parametertypes != null && parametertypes.length != 0) {
            out.writeObject(parametertypes);
            out.writeObject(parameters);
        } else {
            out.writeObject(null);
        }

    }

    public byte getSysid() {
        return sysid;
    }

    public void setSysid(byte sysid) {
        this.sysid = sysid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void fix() {
        InvocationInfoProxy.getInstance().setInvocationInfo(this);
    }

    public String getCallId() {
        return this.callId;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String userLevel) {
        this.logLevel = userLevel;
    }

    public void setCallId(String callId2) {
        this.callId = callId2;
    }

    public String getCallServer() {
        return callServer;
    }

    public void setCallServer(String callServer) {
        this.callServer = callServer;
    }

    public String getHyCode() {
        return hyCode;
    }

    public void setHyCode(String hyCode) {
        this.hyCode = hyCode;

    }

    public String getBizCenterCode() {
        return bizCenterCode;
    }

    public void setBizCenterCode(String bizCenterCode) {
        this.bizCenterCode = bizCenterCode;

    }

    public String getBusiAction() {
        return busiAction;
    }

    public void setBusiAction(String busiAction) {
        this.busiAction = busiAction;
    }

    public long getBizDateTime() {
        return bizDateTime;
    }

    public void setBizDateTime(long bizDateTime) {
        this.bizDateTime = bizDateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private static String readString(ObjectInput in) throws IOException {
        byte len = in.readByte();
        if (len == -1) {
            return null;
        } else {
            byte[] bytes = new byte[len];
            in.read(bytes);
            return new String(bytes, "UTF-8");
        }
    }

    private static void writeString(ObjectOutput out, String str)
            throws IOException {
        if (str != null) {
            byte[] bytes = str.getBytes("UTF-8");
            out.writeByte(bytes.length);
            out.write(bytes);
        } else {
            out.writeByte(-1);
        }

    }

    public String getCallPath() {
        return callPath;
    }

    public void setCallPath(String callPath) {
        this.callPath = callPath;
    }

    public String getRunAs() {
        return runAs;
    }

    public void setRunAs(String ra) {
        this.runAs = ra;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

}