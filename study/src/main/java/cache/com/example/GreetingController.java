package cache.com.example;

import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

@Controller
public class GreetingController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 인터셉터를 쓰지 않고 response에 직접 헤더값을 지정할 수도 있다.
     */
    @GetMapping("/cache-control")
    public String cacheControl(final HttpServletResponse response) {
        final String cacheControl = CacheControl
                .noCache()
                .cachePrivate()
                .getHeaderValue();
        response.addHeader(CACHE_CONTROL, cacheControl);
        return "index";
    }

    @GetMapping("/etag")
    public String etag(final HttpServletResponse response) {
        return "index";
    }

    @GetMapping("/resource-versioning")
    public String resourceVersioning() {
        return "resource-versioning";
    }
}
