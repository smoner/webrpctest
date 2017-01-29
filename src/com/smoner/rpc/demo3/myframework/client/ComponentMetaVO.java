package com.smoner.rpc.demo3.myframework.client;


import java.io.Serializable;

/**
 * Created by smoner on 2017/1/29.
 */
public class ComponentMetaVO implements Serializable {

    private static final long serialVersionUID = -6772182283831182886L;

    /**
     * name can't be null
     */
    private String name;

    private String[] alias;

    private String[] itfs;

    private String module;

    private int rank;

    public ComponentMetaVO() {

    }

    /**
     * construct with name,alias and the interface infromations
     *
     * @param name
     * @param alias
     * @param itfs
     */
    public ComponentMetaVO(String module, String name, String[] alias,
                           String[] itfs) {
        this.module = module;
        this.name = name;
        this.alias = alias;
        this.itfs = itfs;
    }

    /**
     * construct with name,alias and the interface classes
     *
     * @param name
     * @param alias
     * @param interfaces
     */
    public ComponentMetaVO(String module, String name, String[] alias,
                           Class<?>[] interfaces) {
        this.module = module;
        this.name = name;
        this.alias = alias;
        itfs = new String[interfaces.length];

        for (int i = 0; i < itfs.length; i++) {
            itfs[i] = interfaces[i].getName();
        }
    }

    public ComponentMetaVO(String name, String[] alias, Class<?>[] interfaces) {
        this(null, name, alias, interfaces);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String[] getAlias() {
        return alias;
    }

    public String[] getInterfaces() {
        return itfs;
    }

    public void setInterfaces(String interfaces[]) {
        this.itfs = interfaces;
    }

    public String toString() {
        return module == null ? name : module + "/" + name;
    }

    public int hashCode() {
        return module == null ? name.hashCode() : module.hashCode() + 27
                * name.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof ComponentMetaVO) {
            ComponentMetaVO other = (ComponentMetaVO) o;
            return equals(other.module, module) && equals(other.name, name);
        } else {
            return false;
        }
    }

    /**
     * @return Returns the module.
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module
     *            The module to set.
     */
    public void setModule(String module) {
        this.module = module;
    }


    private static boolean equals(String s1, String s2) {
        if (s1 != null && s2 != null) {
            return s1.equals(s2);
        } else {
            return s1 == s2;
        }
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
