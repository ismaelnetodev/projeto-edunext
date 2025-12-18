package com.edunext.app.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private final SecurityFilter securityFilter;

    public SecurityConfigurations(SecurityFilter securityFilter){
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers("/v3/api-docs/**").permitAll();
                    req.requestMatchers("/swagger-ui/**").permitAll();
                    req.requestMatchers("/swagger-ui.html").permitAll();

                    //PERMISSÕES DO GESTOR
                    req.requestMatchers(HttpMethod.GET, "/alunos").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.POST, "/alunos").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/alunos/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/alunos/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/alunos/*/resetar-senha").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.POST, "/turmas").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/turmas/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/turmas/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.POST, "/turmas/{turmaId}/associar-professor").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/professores").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.POST, "/professores").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/professores/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/professores/**").hasRole("ADMIN");

                    //PROFESSOR E GESTOR
                    req.requestMatchers(HttpMethod.GET, "/alunos/{id}").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.GET, "/turmas").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.POST, "/avaliacoes").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/avaliacoes").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.GET, "/professores/minhas-turmas").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.POST, "simulados/gerar-aleatorio").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.POST, "/simulados/criar").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.GET, "/simulados/professor/meus-simulados").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.GET, "/alunos/buscar").hasAnyRole("ADMIN", "PROFESSOR");
                    req.requestMatchers(HttpMethod.GET, "/turmas/buscar").hasAnyRole("ADMIN", "PROFESSOR");
                    
                    //ALUNO
                    req.requestMatchers(HttpMethod.GET, "/simulados/aluno/minhas-provas").hasRole("ALUNO");
                    req.requestMatchers(HttpMethod.POST, "/simulados/{simuladoId}/submeter").hasRole("ALUNO");
                    req.requestMatchers(HttpMethod.GET, "/perfil/aluno").hasRole("ALUNO");
                    req.requestMatchers(HttpMethod.PUT, "/alunos/alterar-senha").hasRole("ALUNO");

                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/upload/photo").authenticated();
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        List<String> allowedOrigins = new ArrayList<>();

        allowedOrigins.add("https://projeto-educa-mais.onrender.com");
        allowedOrigins.add("http://localhost:4200");      // Angular dev
        allowedOrigins.add("http://localhost:8100");      // Ionic dev
        allowedOrigins.add("http://localhost:3000");      // React dev
        allowedOrigins.add("capacitor://localhost");      // Ionic/Capacitor
        allowedOrigins.add("ionic://localhost");          // Ionic legacy
        allowedOrigins.add("http://localhost");           // Genérico

        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

}
