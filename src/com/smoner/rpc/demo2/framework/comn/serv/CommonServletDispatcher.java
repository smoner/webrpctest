package com.smoner.rpc.demo2.framework.comn.serv;

import com.smoner.rpc.demo2.framework.rmi.server.HttpRMIContext;
import com.smoner.rpc.demo2.framework.rmi.server.RMIHandler;
import com.smoner.rpc.demo2.framework.rmi.server.RMIHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by smoner on 2017/1/26.
 */
public class CommonServletDispatcher extends HttpServlet {

    Logger log = Logger.getLogger(CommonServletDispatcher.class.getName());
    private transient RMIHandler rmiHandler;

    public void init() throws ServletException {
        //log.debug("ServletDispatcher.initing......");
        rmiHandler = new RMIHandlerImpl();
       // log.debug("ServletDispatcher.inited");
    }

    public CommonServletDispatcher() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * <p/>
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * <p/>
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            rmiHandler.handle(new HttpRMIContext(request, response));
        } catch (Throwable e) {
           // log.log(Level.,"remote service error", e);
            System.out.print(e.getMessage());
        }
    }
}
