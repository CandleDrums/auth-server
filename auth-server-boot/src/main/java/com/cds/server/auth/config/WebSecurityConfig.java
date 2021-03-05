package com.cds.server.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsUtils;

import com.cds.api.auth.model.RespBody;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author EalenXie create on 2020/11/3 13:00
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ClientDetailsService clientDetailsService;
    /**
     * 这些接口 对于认证中心来说无需授权
     */
    protected static final String[] PERMIT_ALL_URL =
        {"/oauth/**", "/user/**", "/actuator/**", "/login", "/error", "/open/api"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
            // 处理跨域请求中的Preflight请求
            .antMatchers(HttpMethod.OPTIONS).permitAll().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers(PERMIT_ALL_URL).permitAll().and().formLogin().loginProcessingUrl("/login")
            .usernameParameter("username").passwordParameter("password").successHandler(authenticationSuccessHandler())
            .failureHandler(authenticationFailureHandler()).and().logout().logoutSuccessHandler(logoutSuccessHandler())
            .deleteCookies("JSESSIONID").and().httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            RespBody<Void> resp = new RespBody<>(HttpStatus.OK.value(), "login success", null);
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(resp));
        };
    }

    // //
    // // /**
    // // * 登录成功处理器
    // // */
    // // @Bean
    // // public AuthenticationSuccessHandler authenticationSuccessHandler() {
    // // return (request, response, authentication) -> {
    // // String header = request.getHeader("Authorization");
    // //
    // // try {
    // // String[] tokens = AuthUtils.extractAndDecodeHeader(header);
    // // assert tokens.length == 2;
    // // String clientId = tokens[0];
    // // Enumeration<String> attributeNames = request.getAttributeNames();
    // // String authType = request.getAuthType();
    // // Enumeration<String> headerNames = request.getHeaderNames();
    // // Principal userPrincipal = request.getUserPrincipal();
    // // Map<String, String[]> parameterMap = request.getParameterMap();
    // // ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
    // // TokenRequest tokenRequest =
    // // new TokenRequest(new HashMap<>(), clientId, clientDetails.getScope(), "mobile");
    // // OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
    // //
    // // OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
    // //
    // // AuthorizationServerTokenServices tokenServices = SpringContextAware
    // // .getBean("defaultAuthorizationServerTokenServices", AuthorizationServerTokenServices.class);
    // //
    // // OAuth2AccessToken oAuth2AccessToken = tokenServices.createAccessToken(oAuth2Authentication);
    // // // log.info("获取token 成功：{}", oAuth2AccessToken.getValue());
    // // response.setCharacterEncoding("UTF-8");
    // // response.setContentType("application/json; charset=utf-8");
    // // PrintWriter printWriter = response.getWriter();
    // // printWriter.append(objectMapper.writeValueAsString(oAuth2AccessToken));
    // // } catch (IOException e) {
    // // throw new BadCredentialsException("Failed to decode basic authentication token");
    // // }
    // // };
    // // }
    //
    // public static String[] extractAndDecodeHeader(String header) throws IOException {
    //
    // byte[] base64Token = header.substring(6).getBytes("UTF-8");
    // byte[] decoded = null;
    // try {
    // decoded = Base64.decode(base64Token);
    // } catch (IllegalArgumentException e) {
    // // throw new UserAuthBizException("Failed to decode basic authentication token");
    // }
    //
    // String token = new String(decoded, "UTF-8");
    //
    // int delim = token.indexOf(":");
    //
    // if (delim == -1) {
    // // throw new UserAuthBizException("Invalid basic authentication token");
    // }
    // return new String[] {token.substring(0, delim), token.substring(delim + 1)};
    // }
    //
    /**
     * 登出成功处理器
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            RespBody<Void> resp = new RespBody<>(HttpStatus.OK.value(), "logout success", null);
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(resp));
        };
    }

    /**
     * 常规登录失败处理器
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            RespBody<Void> resp = new RespBody<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(resp));
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        auth.eraseCredentials(true);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
