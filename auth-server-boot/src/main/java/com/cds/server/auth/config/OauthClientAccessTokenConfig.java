package com.cds.server.auth.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.cds.api.auth.model.UserAuthVO;

@Configuration
public class OauthClientAccessTokenConfig {

    /**
     * 前面的 jwt key 我这里写死为 5371f568a45e5ab1f442c38e0932aef24447139b
     */
    private static final String SIGNING_KEY = "5371f568a45e5ab1f442c38e0932aef24447139b";

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcClientDetailsService jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * tokenService 配置
     */
    @Bean(name = "tokenServices")
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setClientDetailsService(jdbcClientDetailsService());
        // 允许支持refreshToken
        tokenServices.setSupportRefreshToken(true);
        // refreshToken 不重用策略
        tokenServices.setReuseRefreshToken(false);
        // 设置Token存储方式
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(tokenEnhancerChain());
        return tokenServices;
    }

    /**
     * 配置TokenStore token持久化
     */
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 自定义TokenEnhancerChain 由多个TokenEnhancer组成
     */
    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain
            .setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter(), additionalInformationTokenEnhancer()));
        return tokenEnhancerChain;
    }

    /**
     * JWT 转换器
     */
    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }

    // @Bean
    // public JwtAccessTokenConverter jwtAccessTokenConverter() {
    // JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
    // accessTokenConverter.setSigningKey(SIGNING_KEY);
    // ((DefaultAccessTokenConverter)accessTokenConverter.getAccessTokenConverter())
    // .setUserTokenConverter(new DefaultUserAuthenticationConverter() {
    // @Override
    // public Authentication extractAuthentication(Map<String, ?> map) {
    // CustomUser customUser = new CustomUser();
    // BeanMap.create(customUser).putAll(map);
    // Object authorities = map.get("authorities");
    // if (authorities instanceof String) {
    // customUser
    // .setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList((String)authorities));
    // } else if (authorities instanceof Collection) {
    // customUser.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(
    // StringUtils.collectionToCommaDelimitedString((Collection)authorities)));
    // }
    // return new PreAuthenticatedAuthenticationToken(customUser, null, customUser.getAuthorities());
    // }
    // });
    // return accessTokenConverter;
    // }

    /**
     * token 额外自定义信息 此例获取用户信息
     */
    @Bean
    public TokenEnhancer additionalInformationTokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> information = new HashMap<>(8);
            Authentication userAuthentication = authentication.getUserAuthentication();
            if (userAuthentication instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)userAuthentication;
                Object principal = token.getPrincipal();
                if (principal instanceof UserAuthVO) {
                    UserAuthVO userDetails = (UserAuthVO)token.getPrincipal();
                    information.put("userDetails", userDetails);
                    ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(information);
                }
            }
            return accessToken;
        };
    }
}
