package it.kdm.orchestratore.menuappdoc;

/**
 * Created by enrico on 11/07/17.
 */
public class MenuAppDocInformation {

    private Long countActivity;
    private Long countPotentialActivity;
    private Long refreshTimeInSecond;

    public Long getCountActivity() {
        return countActivity;
    }

    public void setCountActivity(Long countActivity) {
        this.countActivity = countActivity;
    }

    public Long getCountPotentialActivity() {
        return countPotentialActivity;
    }

    public void setCountPotentialActivity(Long countPotentialActivity) {
        this.countPotentialActivity = countPotentialActivity;
    }

    public Long getRefreshTimeInSecond() {
        return refreshTimeInSecond;
    }

    public void setRefreshTimeInSecond(Long refreshTimeInSecond) {
        this.refreshTimeInSecond = refreshTimeInSecond;
    }
}
