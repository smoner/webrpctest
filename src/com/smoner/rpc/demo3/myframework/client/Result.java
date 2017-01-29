package com.smoner.rpc.demo3.myframework.client;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by smoner on 2017/1/29.
 */
public class Result  implements Externalizable {

    private static final long serialVersionUID = 1L;
    private static final byte RS_HEADER_OBJ = 0;

    private static final byte RS_HEADER_EXP = 1;

    private static final byte RS_HEADER_NULL = 2;

    /**
     * the normal return object
     */
    public Object result;

    /**
     * the exception
     */
    public Throwable appexception;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte header = in.readByte();

        switch (header) {
            case RS_HEADER_OBJ:
                result = in.readObject();
                appexception = null;
                break;
            case RS_HEADER_EXP:
                appexception = (Throwable) in.readObject();
                result = null;
                break;
            default:
                appexception = null;
                result = null;
        }

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if (result != null) {
            out.writeByte(RS_HEADER_OBJ);
            out.writeObject(result);
        } else if (appexception != null) {
            out.writeByte(RS_HEADER_EXP);
            out.writeObject(appexception);

        } else {
            out.writeByte(RS_HEADER_NULL);
        }

    }

}