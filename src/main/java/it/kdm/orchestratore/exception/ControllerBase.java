package it.kdm.orchestratore.exception;

import it.kdm.orchestratore.security.Security;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public class ControllerBase {

    //@Autowired
    //private MessageSource messageSource;

    //private static final Logger logger = LoggerFactory.getLogger(ControllerBase.class);

    @ModelAttribute()
    public void jspUtils(Model model) {
        //rende disponibili alle jsp tutti i metodi della classe
        model.addAttribute("jspUtils", new Security());
    }

    /*public boolean checkRights(ICIFSObject target, String rightsKey) throws Exception {
        String token = Session.getUserInfo().getToken();
        return Security.checkRights(token, target, rightsKey);
    }*/

    /*@ExceptionHandler(Throwable.class)
    public ModelAndView handleException(Exception ex,HttpServletRequest request,HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");

        logger.error(ex.getMessage());
        ex.printStackTrace();

        String errorMsg = getErrorMessage(ex);

        if (ex instanceof KDMException)
            ex = new KDMException(errorMsg, ex.getCause());
        else
            ex = new KDMException(errorMsg, ex);

        modelAndView.addObject("message",(KDMException)ex);
        KDMUtils.setErrorStatus(request, response);

        return KDMUtils.finalizeModelAndView(modelAndView, request);
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView handleException(Throwable ex)
    {
        String cause = ex.getClass().getSimpleName();
        String message = ex.getMessage();

        String mess = "";
        boolean isError = true;

        // per eventuale customizzazione dei messaggi
        if (ex instanceof ResourceAccessException) {
            mess = " rilevata eccezione. (" + cause + ") " + message;
        } else if (ex instanceof HttpServerErrorException) {
            mess = " rilevata eccezione. (" + cause + ") "+ message;
        } else if (ex instanceof MissingServletRequestParameterException) {
            mess = " Parametro in ingresso non inserito";
        } else if (ex instanceof HttpClientErrorException) {
            message = ((HttpClientErrorException)ex).getResponseBodyAsString();
            mess = " il ritorno della chiamata Rest ha causato un errore: " + message;
        } else if (ex instanceof TemplateException || ex instanceof ParseException)  {
            mess = "Rilevato problema durante l'esecuzione del Form html impostato per l'operazione richiesta: "+ message ;
            isError = false;
        }  else  {
            mess = "Eccezione generica rilevata " + cause + " " + message;
        }

        logger.info("Rilevata eccezione: (" +mess + ") :"+ ex.getMessage());
        KDMUtils.printMyStackTrace(logger, ex.getStackTrace());

        if (isError) {
            return errorModelAndView(mess);
        } else {
            return warningModelAndView(mess);
        }
    }

    protected String getErrorMessage(Exception e) {
        //String errorMsg = "Eccezione generica rilevata contattare l'amministratore";
        String lblKey = "label.error.generic";
        String msg="";

        if (e instanceof DocerApiException) {
            lblKey = String.format("label.error.%s",((DocerApiException)e).errorCode);
            msg=((DocerApiException)e).getMessage();
        }

        if (e instanceof KDMException) {
            lblKey = String.format("label.error.%s",((KDMException)e).getErrorCode());
            msg=((KDMException)e).getMessage();
        }

        String language;

        try {
            UserInfo uInfo = Session.getUserInfo();
            language = uInfo.getLanguage();
        } catch (Exception e1) {
            language = "it";
        }

        String errorMsg = messageSource.getMessage(lblKey, new Object[]{msg} , "Codice Errore: "+lblKey, new Locale(language));

        return errorMsg;
    }



    //TODO: Questi due metodi sono duplicati
    private ModelAndView errorModelAndView(String ex)  {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("default");
        //String rel = MessageFormat.format(PropertiesReader.getInstance().getAppError1(),  ex);
        modelAndView.addObject("errore",ex);
        return modelAndView;
    }
    private ModelAndView warningModelAndView(String ex)  {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("default");
        //String rel = MessageFormat.format(PropertiesReader.getInstance().getAppError2(),  ex);
        modelAndView.addObject("warning",ex);
        return modelAndView;
    }*/

}



