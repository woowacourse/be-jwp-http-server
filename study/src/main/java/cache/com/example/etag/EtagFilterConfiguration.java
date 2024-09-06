package cache.com.example.etag;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

import cache.com.example.version.ResourceVersion;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class EtagFilterConfiguration {

    private final ResourceVersion version;

    public EtagFilterConfiguration(ResourceVersion version) {
        this.version = version;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>(
                new ShallowEtagHeaderFilter());
        filterRegistrationBean.addUrlPatterns("/etag");
        filterRegistrationBean.addUrlPatterns("/resource-versioning/**");
        filterRegistrationBean.addUrlPatterns(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/*");
        return filterRegistrationBean;
    }
}
