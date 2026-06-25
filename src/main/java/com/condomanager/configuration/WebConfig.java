package com.condomanager.configuration;

import com.condomanager.security.AuditoriaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração web: regista o interceptor de auditoria nas rotas da API e
 * serve a interface estática (SPA) sem cache, para que cada deploy seja sempre
 * obtido pelo browser (evita ficar com uma versão antiga do {@code index.html}).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditoriaInterceptor auditoriaInterceptor;

    public WebConfig(AuditoriaInterceptor auditoriaInterceptor) {
        this.auditoriaInterceptor = auditoriaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditoriaInterceptor).addPathPatterns("/api/v1/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // O único recurso estático local é a SPA (index.html); os restantes (Bootstrap,
        // Chart.js) vêm de CDN. Servir sempre revalidado evita o browser ficar preso a
        // uma versão antiga da interface após um novo deploy.
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache().mustRevalidate());
    }
}
