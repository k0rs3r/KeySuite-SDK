package it.kdm.orchestratore.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.regex.Matcher;

/**
 * Created by danilo.russo on 14/12/2017.
 */
public abstract class SpringMessageReplacer implements StringReplacerCallback {

    public String getMessage(Matcher m, MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(m.group(), null, m.group(), locale);
    }
}
