package it.kdm.orchestratore;

import it.kdm.doctoolkit.services.ToolkitConnector;
import it.kdm.orchestratore.query.QueryParams;
import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;
import it.kdm.orchestratore.utils.RestTemplateFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

public class RestCall {

    private static final String SEPARATOR = "|";
    private static final String KS_AUTH_GROUP = "KS_AUTH_GROUP";
    private static String BPM_SERVER_REST = ToolkitConnector.getGlobalProperty("server.rest");


    public static RestResponse execute(String url, QueryParams params){
        return GET(url, params, RestResponse.class);
    }

    public static <T> T GET(String url, QueryParams params, Class<T> responseType){
        return GETorPOST(url,params,null,responseType);
    }

    public static <T> T POST(String url, QueryParams params, Object post, Class<T> responseType){
        return GETorPOST(url,params,post,responseType);
    }

    private static <T> T GETorPOST(String url, QueryParams params, Object post, Class<T> responseType) {

        RestTemplate restTemplate = RestTemplateFactory.getInstance().getRestTemplate();

        if (restTemplate.getInterceptors() == null || (restTemplate.getInterceptors() != null && restTemplate.getInterceptors().size() == 0)) {
            UserInfo ui = Session.getUserInfoNoExc();

            final String ksAuthGroup = ui.getUsername() + SEPARATOR + ui.getEnte().getCod() + SEPARATOR + ui.getCurrentAoo().getCod() + SEPARATOR;

            restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
                @Override
                public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                    request.getHeaders().set(KS_AUTH_GROUP, ksAuthGroup);
                    return execution.execute(request, body);
                }
            });
        }

        String qs = params.toString();

        URI uri = URI.create(BPM_SERVER_REST + url + "?" + qs);

        if (post != null)
            return restTemplate.postForObject(uri, post, responseType);
        else
            return restTemplate.getForObject(uri, responseType);

    }
}
