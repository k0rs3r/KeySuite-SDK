package it.kdm.doctoolkit.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lorenzo Lucherini
 * Date: 12/3/13
 * Time: 4:55 PM
 */
public class TempMask {

    private final List<Pattern> regexps;

    public TempMask() {
        this(new ArrayList<String>());
    }

    public TempMask(Iterable<String> regexps) {

        this.regexps = new ArrayList<>();
        for (String regex : regexps) {
            this.regexps.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }
    }

    private String getName(String path) {
        if (path.isEmpty() || path.equals("\\")) {
            return "";
        }

        int idx = path.lastIndexOf('\\');
        if (idx == -1) {
            return path;
        }

        return path.substring(idx + 1);
    }

    public boolean isTemporary(String path) {
        String filename = getName(path);
        for (Pattern regex : regexps) {
            Matcher matcher = regex.matcher(filename);
            if(matcher.matches()) {
                return true;
            }
        }

        return false;
    }
}
