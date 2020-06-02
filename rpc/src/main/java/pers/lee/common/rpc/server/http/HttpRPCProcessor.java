package pers.lee.common.rpc.server.http;

import pers.lee.common.rpc.server.RpcConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.zip.GZIPInputStream;

/**
 * @author Passyt
 * @version 0.1, 2010-4-16 10:18:59
 */
public class HttpRPCProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRPCProcessor.class);

    private static String HEADER_CONTENT_ENCODING = "Content-Encoding";
    private static String HEADER_VALUE_CONTENT_ENCODING_GZIP = "gzip";

    private RpcConsole console;
    private ServletContext servletContext;

    private static boolean PAUSE = false;

    public HttpRPCProcessor(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setConsole(RpcConsole console) throws ServletException {
        this.console = console;
    }

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        // support js url
        String request = httpServletRequest.getParameter("json");
        PrintWriter writer = httpServletResponse.getWriter();
        if (request != null) {
            request = URLDecoder.decode(request, console.getCharset());
            String responseName = httpServletRequest.getParameter("return");
            responseName = responseName != null ? responseName : "result";
            httpServletResponse.setCharacterEncoding(console.getCharset());
            httpServletResponse.setContentType("application/x-javascript");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            console.invoke(new ByteArrayInputStream(request.getBytes()), outputStream,
                    new HttpContext(httpServletRequest, httpServletResponse, servletContext));
            writer.write(responseName + "=" + outputStream.toString());
        } else {
            String statusCommand = getStatusCommand(httpServletRequest.getRequestURI(), httpServletRequest.getQueryString());
            if (statusCommand != null) {
                if ("pause".equalsIgnoreCase(statusCommand)) {
                    PAUSE = true;
                    writer.write("status.ci is pausing");
                    return;
                } else if ("resume".equalsIgnoreCase(statusCommand)) {
                    PAUSE = false;
                } else {
                    if (PAUSE) {
                        httpServletResponse.sendError(500, "status.ci is paused");
                        return;
                    }
                }
            }

            httpServletResponse.setContentType("text/html");
            String title = "Plastosome JSON-RPC Service";
            writer.write("<html><head><title>" + title + "</title></head><body>");
            writer.write("<h1>" + title + "</h1><p>Powered by Derbysoft</p></body></html>");
        }
    }

    public static String getStatusCommand(String requestURI, String queryString) {
        int index = requestURI.indexOf("/status.ci");
        if (index < 0) {
            return null;
        }

        return queryString == null ? "" : queryString;
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        InputStream inputStream = httpServletRequest.getInputStream();
        OutputStream outputStream = httpServletResponse.getOutputStream();

        if (HEADER_VALUE_CONTENT_ENCODING_GZIP.equalsIgnoreCase(httpServletRequest.getHeader(HEADER_CONTENT_ENCODING))) {
            LOGGER.debug("Catch compressed inputStream");
            inputStream = new GZIPInputStream(inputStream);
        }

        httpServletResponse.setCharacterEncoding(console.getCharset());
        httpServletResponse.setContentType(httpServletRequest.getContentType());
        console.invoke(inputStream, outputStream, new HttpContext(httpServletRequest, httpServletResponse, servletContext));
    }

    public boolean accept(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return console.accept(new HttpContext(httpServletRequest, httpServletResponse, servletContext));
    }
}
