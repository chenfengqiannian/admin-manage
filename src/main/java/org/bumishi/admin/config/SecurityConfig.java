package org.bumishi.admin.config;

import org.bumishi.admin.security.UrlSecurityInterceptor;
import org.bumishi.admin.security.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiang.xie
 * @date 2016/9/22
 */

@Configuration
//@EnableWebSecurity 注释掉可以既能享受到springboot的自动配置又能覆盖某些配置
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected UserDetailService userDetailService;


    @Autowired
    protected Md5PasswordEncoder md5PasswordEncoder;

    @Value("${remember.key}")
    private String key="weixin:javajidi_com";


    @Bean
    public UrlSecurityInterceptor urlSecurityInterceptor() {
        return new UrlSecurityInterceptor();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable();
        http.headers().disable();
        http.jee().disable();
        http.x509().disable();
        http.servletApi().disable();
        http.anonymous().disable();
        http.requestCache().disable();

        // http.csrf().ignoringAntMatchers("/","/index");
        http.rememberMe().userDetailsService(userDetailService).key(key).useSecureCookie(true);
        http.addFilterBefore(urlSecurityInterceptor(), FilterSecurityInterceptor.class);//处理自定义的权限
       //authorizeRequests()对应FilterSecurityInterceptor，不配置就不会加入FilterSecurityInterceptor
       // http.authorizeRequests().anyRequest().denyAll();//其他url全部拒绝
        http.formLogin().loginProcessingUrl("/login").defaultSuccessUrl("/").loginPage("/to-login").failureUrl("/to-login");
        http.logout().logoutSuccessUrl("/to-login");
        http.exceptionHandling().accessDeniedPage("/401");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setPasswordEncoder(md5PasswordEncoder);
        provider.setUserDetailsService(userDetailService);
        ReflectionSaltSource saltSource=new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("getSalt");
        provider.setSaltSource(saltSource);
        auth.authenticationProvider(provider);

    }

    @Bean
    protected AccessDecisionManager accessDecisionManager(){
        RoleVoter roleVoter=new RoleVoter();
        roleVoter.setRolePrefix("");
        List voters=new ArrayList<>();
        voters.add(roleVoter);
        AccessDecisionManager accessDecisionManager=new AffirmativeBased(voters);
        return accessDecisionManager;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.js", "/**/*.js.map", "/**/*.ts", "/**/*.css", "/**/*.css.map", "/**/*.png", "/**/*.gif", "/**/*.jpg", "/**/*.fco", "/**/*.woff", "/**/*.woff2", "/**/*.font", "/**/*.svg", "/**/*.ttf", "/**/*.pdf", "/admin/api/**", "/to-login", "/404", "/401", "/error");
    }

    //由于springboot默认会将所要的servlet,filter,listenr等标准servlet组件自动加入到servlet的过滤器链中，自定义的UrlSecurityInterceptor只希望加入security的过滤器链，中，所以这里配置不向servlet容器中注册
    @Bean
    public FilterRegistrationBean registration(UrlSecurityInterceptor filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }


     /*
        区别于传统的服务端应用直接返回页面内容（包括页面片段），服务器端采用restful的接口返回json数据，而页面则由客户端框架来实现。所以Spring Security的默认配置不是很适合。
    *   禁用csrf，由于login form并不是由服务端实现，所以spring防御跨站请求伪造的方式在这里不适用;
        配置哪些url不需要拦截，通常css，js之类的静态资源不需要权限控制;
        配置对login的处理，这里用自定义的handle类覆盖了原有的login处理方式；默认情况下成功login后会跳转到用户指定的url，覆盖为只返回200，不做跳转，由客户端决定login成功后的行为。同样login失败默认的处理是返回401，并跳转到指定的url，所以同样覆盖为只返回401，不做跳转；
        配置对logout的处理，同样覆盖为只返回200，不做跳转；
        配置权限认证不通过后的行为，返回401。
    * */

    public static class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response, Authentication authentication)
                throws ServletException, IOException {

            clearAuthenticationAttributes(request);
        }
    }

    public static class RestLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(HttpServletRequest request,
                                    HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            //Do nothing!
        }
    }

    public static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication Failed: " + authException.getMessage());

        }
    }


}
