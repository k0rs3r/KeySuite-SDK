package it.kdm.orchestratore.utils;

import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.regex.Matcher;

/**
 * Created by danilo.russo on 14/12/2017.
 */
public interface StringReplacerCallback {
    public String replace(Matcher match, MessageSource messageSource, Locale locale);
}
