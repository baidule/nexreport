package jatools;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class CrossBrowserFilter implements Filter {
    final Map<String, String> filteredPatterns = new HashMap<String, String>();
    private FilterConfig fc;

    public void destroy() {
        // TODO Auto-generated method stub
    }

    public void doFilter(ServletRequest req, ServletResponse res,
        FilterChain chain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) req).getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            uri = uri.substring(0, pathParamIndex);
        }

        if (this.filteredPatterns.containsKey(uri)) {
            String realURI = uri.replaceAll("_x.htm$", ".jsp")
                                .replaceAll(".htm$", ".jsp") +
                "?crossbrowser=" + uri.endsWith("_x.htm");

            fc.getServletContext().getRequestDispatcher(realURI)
              .forward(req, res);
        } else {
            chain.doFilter(req, res);
        }
    }

    public static void main(String[] args) {
        System.out.println("aab_x.htmaab_x.htm".replaceAll("_x.htm$", ".jsp")
                                               .replaceAll(".htm$", ".jsp"));
    }

    public void init(FilterConfig fc) throws ServletException {
        this.fc = fc;
        
    }
}
