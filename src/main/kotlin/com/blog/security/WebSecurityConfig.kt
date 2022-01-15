package com.blog.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    @Autowired val webTokenFilter: WebTokenFilter
) : WebSecurityConfigurerAdapter() {
    @Override
    override fun configure(http: HttpSecurity) {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests()
            .antMatchers("/oauth/client-id", "/oauth/sign-in/code", "/posts/published/*").permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(webTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}

