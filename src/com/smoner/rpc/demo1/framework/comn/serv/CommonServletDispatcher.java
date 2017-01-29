package com.smoner.rpc.demo1.framework.comn.serv;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by smoner on 2017/1/26.
 */
public class CommonServletDispatcher extends HttpServlet {
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
         *
         * This method is called when a form has its tag value method equals to get.
         *
         * @param request
         *            the request send by the client to the server
         * @param response
         *            the response send by the server to the client
         * @throws ServletException
         *             if an error occurred
         * @throws IOException
         *             if an error occurred
         */
        public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
            this.doPost(request, response);
        }

        /**
         * The doPost method of the servlet. <br>
         *
         * This method is called when a form has its tag value method equals to
         * post.
         *
         * @param request
         *            the request send by the client to the server
         * @param response
         *            the response send by the server to the client
         * @throws ServletException
         *             if an error occurred
         * @throws IOException
         *             if an error occurred
         */
        public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
            response.setContentType("text/html;charset=utf-8");
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            //客户端 HttpUtils并没有写request方法是post ,但服务器端可自动识别
            String method = request.getMethod();
            System.out.println("request method :"+method);


            PrintWriter out = response.getWriter();
            String username = request.getParameter("username");
            System.out.println("-username->>"+username);

            String password = request.getParameter("password");
            System.out.println("-password->>"+password);

            if (username.equals("admin") && password.equals("123")) {
                // 表示服务器段返回的结果
                out.print("login is success !");
            } else {
                out.print("login is fail !");
            }
            out.flush();
            out.close();
        }

        /**
         * Initialization of the servlet. <br>
         *
         * @throws ServletException
         *             if an error occurs
         */
        public void init() throws ServletException {
            // Put your code here
        }
    }
