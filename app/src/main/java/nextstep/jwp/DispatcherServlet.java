package nextstep.jwp;

import nextstep.jwp.adapter.HandlerAdapter;
import nextstep.jwp.adapter.HandlerAdapters;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.model.httpmessage.response.HttpStatus;
import nextstep.jwp.util.FileUtils;
import nextstep.jwp.view.*;

import javax.servlet.ServletException;
import java.io.IOException;

import static nextstep.jwp.model.httpmessage.response.HttpStatus.NOT_FOUND;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;


public class DispatcherServlet {

    public void service(HttpRequest request, HttpResponse response) throws IOException, ServletException {
        RequestMapping requestMapping = new RequestMapping();

        View view = new View();
        Object handler = requestMapping.getHandler(request);
        if (handler == null) {
            String path = request.getPath();
            ModelAndView mv = new ModelAndView();

            if (FileUtils.isStaticFile(path)) {
                view = new StaticView(FileUtils.getAbsolutePath(path));
                view.render(mv, request, response);
                return;
            }

            sendError(request, response, mv, NOT_FOUND);
            return;
        }

        HandlerAdapters handlerAdapters = new HandlerAdapters();
        HandlerAdapter adapter = handlerAdapters.getHandlerAdapter(handler);
        if (adapter == null) {
            sendError(request, response, new ModelAndView(), NOT_FOUND);
            return;
        }

        ModelAndView mv = adapter.handle(request, response, handler);

        if (mv.getViewName() == null) {
            if (mv.hasModel()) {
                view.render(mv, request, response);
                return;
            }
            throw new IllegalArgumentException("출력할 수 없는 결과입니다.");
        }

        String viewName = mv.getViewName();
        if (viewName != null) {
            view = new HTMLView();
            mv.setViewName(resolveView(viewName));

            if (response.getStatus() == REDIRECT) {
                view.redirect(mv, response);
                return;
            }

            view.render(mv, request, response);
        }
    }

    private void sendError(HttpRequest request, HttpResponse response, ModelAndView mv, HttpStatus status) throws IOException {
        ErrorView view = new ErrorView();
        view.sendError(status);
        String absolutePath = resolveView(status.value());
        mv.setViewName(absolutePath);
        view.render(mv, request, response);
    }

    private String resolveView(String viewName) {
        ViewResolver viewResolver = new ViewResolver();
        return viewResolver.resolve(viewName);
    }
}
