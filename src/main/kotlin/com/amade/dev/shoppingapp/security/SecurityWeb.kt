package com.amade.dev.shoppingapp.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityWeb {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/api/**").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/**").permitAll()
            .pathMatchers(HttpMethod.PUT, "/api/**").permitAll()
            .pathMatchers(HttpMethod.DELETE, "/api/**").permitAll()
            .and()
            .httpBasic()
            .disable()
            .csrf()
            .disable()
            .formLogin()
            .disable()
        return http.build()
    }

}