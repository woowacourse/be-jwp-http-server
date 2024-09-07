package cache.com.example.cachecontrol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CacheWebConfig implements WebMvcConfigurer {

    private final CacheControllerInterceptor cacheControllerInterceptor;

    public CacheWebConfig(final CacheControllerInterceptor cacheControllerInterceptor) {
        this.cacheControllerInterceptor = cacheControllerInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(cacheControllerInterceptor);
    }
}
