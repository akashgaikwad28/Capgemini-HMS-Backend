package com.capgemini.hms.security;

import com.capgemini.hms.security.jwt.AuthEntryPointJwt;
import com.capgemini.hms.security.jwt.AuthTokenFilter;
import com.capgemini.hms.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private PatientIdentityManager patientIdentityManager;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public REST APIs
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 🔓 PUBLIC UI + STATIC FILES (🔥 YOUR STEP 3)
                        .requestMatchers(
                                "/",
                                "/login",
                                "/ui/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 🔒 1. Master Data Management - ADMIN ONLY
                        .requestMatchers("/api/v1/blocks/**", "/api/v1/rooms/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/physicians/**", "/api/v1/nurses/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/physicians/**", "/api/v1/nurses/**").hasRole("ADMIN")

                        // 🔒 2. Clinical Operations
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/prescriptions/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/stays/**", "/api/v1/patients/**", "/api/v1/medical-records/procedure/**")
                        .hasAnyRole("ADMIN", "NURSE", "DOCTOR")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/medical-records/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "NURSE", "PATIENT")

                        // 🔒 3. Scheduling & Appointments
                        .requestMatchers("/api/v1/appointments/my").hasRole("PATIENT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/appointments")
                        .hasAnyRole("ADMIN", "NURSE", "PATIENT")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/appointments/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "NURSE")

                        // 🔒 4. Reporting & Dashboards
                        .requestMatchers("/api/v1/dashboard/**").hasRole("ADMIN")

                        // 🔒 5. General Read Access
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/api/v1/medications/**",
                                "/api/v1/procedures/**",
                                "/api/v1/certifications/**")
                        .hasAnyRole("ADMIN", "DOCTOR", "NURSE", "PATIENT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/certifications/**").hasRole("ADMIN")

                        // 🔒 6. Patient Privacy
                        .requestMatchers("/api/v1/patients/{ssn}/**").access(patientIdentityManager)

                        // 🔒 Everything else
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}