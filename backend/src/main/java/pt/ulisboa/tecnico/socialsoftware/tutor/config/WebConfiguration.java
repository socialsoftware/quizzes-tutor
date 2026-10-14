package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.GenerationAccess;

@Configuration
@ComponentScan(basePackageClasses = {WebConfiguration.class})
public class WebConfiguration extends WebMvcConfigurationSupport {
    private static final long MAX_AGE_SECS = 3600;

    @Value("${figures.dir}")
    private String figuresDir;

    @Autowired
    private GenerationAccess generationAccess;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(generationAccess).addPathPatterns(GenerationAccess.PATHS);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/questions/**").addResourceLocations("file:" + figuresDir);

        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/5.31.2/");

        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/5.31.2/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }
}
