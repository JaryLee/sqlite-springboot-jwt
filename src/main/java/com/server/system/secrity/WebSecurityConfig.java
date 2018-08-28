package com.server.system.secrity;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.server.system.secrity.handler.AuthEntryPoint;
import com.server.system.secrity.handler.AuthFailHandler;
import com.server.system.secrity.handler.AuthSuccessHandler;
import com.server.system.secrity.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启security注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	/**
	 * 配置WebSecurity配置
	 * 
	 * @param web
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}

	/**
	 * 开启防火墙对URL分号的处理
	 * 
	 * @return
	 */
	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowSemicolon(true);
		return firewall;
	}

	/**
	 * 配置访问策略
	 * 
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(new AuthEntryPoint()).and()// token认证失败时候的处理类
				// session无状态
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(HttpMethod.GET, "/service/**").permitAll().antMatchers(HttpMethod.POST, "/user/register")
				.permitAll().antMatchers("/**").authenticated()
				// 浏览器报错 x-frame-options deny 的错误
				.and().headers().frameOptions().disable()
				// 登录设置
				.and().formLogin().loginPage("/login").usernameParameter("username").passwordParameter("password")
				.successHandler(new AuthSuccessHandler()).successForwardUrl("/login")
				.failureHandler(new AuthFailHandler()).permitAll();
				// 退出操作设置
/*				.and().logout().addLogoutHandler(new LogoutHandler()).logoutUrl("/logout")
				.logoutSuccessHandler(new LogoutSuccessHandler()).permitAll()*/

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		// 关闭csrf 防止循环定向
		http.csrf().disable();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

}
