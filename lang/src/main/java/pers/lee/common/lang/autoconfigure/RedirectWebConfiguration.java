package pers.lee.common.lang.autoconfigure;

import pers.lee.common.lang.web.HttpConnectionRedirectService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Passyt on 2018/5/16.
 */
@ConditionalOnWebApplication
public class RedirectWebConfiguration {

    @Bean
    public ServletRegistrationBean redirectServletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(new RedirectServlet());
        registrationBean.addUrlMappings("/redirect.ci");
        return registrationBean;
    }

    private static class RedirectServlet extends HttpServlet {

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            new HttpConnectionRedirectService().execute(request, response);
        }
    }


}
