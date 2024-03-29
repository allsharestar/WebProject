package spms.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

@WebFilter(
		urlPatterns="/*",
		initParams= {
				@WebInitParam(name="encoding", value="UTF-8")
		})
public class CharacterEncodingFilter implements Filter {
	FilterConfig config;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}  

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain nextFilter)
			throws IOException, ServletException {
		
		/* 서블릿이 실행되기 전에 해야 할 작업 */
		req.setCharacterEncoding(config.getInitParameter("encoding"));
		
		// 다음 필터 호출, 더이상 필터가 없다면 서블릿의 service()가 호출됨
		nextFilter.doFilter(req, res);
		
		/* 서블릿을 실행한 후, 클라이언트에게 응답하기 전에 해야할 작업 */
	}

	@Override
	public void destroy() {}
}
