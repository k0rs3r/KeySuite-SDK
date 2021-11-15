package it.kdm.orchestratore.exception;

import it.kdm.orchestratore.utils.KDMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static it.kdm.orchestratore.utils.KDMUtils.getErrorMessage;

@ControllerAdvice
public class GlobalException {

    @Autowired
    private MessageSource messageSource;

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {

        if (AnnotationUtils.findAnnotation
                (ex.getClass(), ResponseStatus.class) != null)
            throw ex;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");

        logger.error(ex.getMessage());
        ex.printStackTrace();

        String errorMsg = getErrorMessage(messageSource,ex);

        if (ex instanceof KDMException)
            ex = new KDMException(errorMsg, ex.getCause());
        else
            ex = new KDMException(errorMsg, ex);

        modelAndView.addObject("message",ex);
        KDMUtils.setErrorStatus(request, response);

        return KDMUtils.finalizeModelAndView(modelAndView, request);
    }
}
