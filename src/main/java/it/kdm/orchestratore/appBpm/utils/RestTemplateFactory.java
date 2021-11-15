package it.kdm.orchestratore.appBpm.utils;

import it.kdm.orchestratore.session.Session;
import it.kdm.orchestratore.session.UserInfo;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RestTemplateFactory {
    private static RestTemplateFactory instanceFactory;


//    private static HashMap<String, RestTemplateKdm>instances;


    private RestTemplateFactory(){
        //instances = new HashMap<String, RestTemplateKdm>();
    }


    public static RestTemplateFactory getInstance(){
        if(instanceFactory == null){
            instanceFactory = new RestTemplateFactory();
        }

        return instanceFactory;
    }

    public RestTemplateKdm getRestTemplateKdm() throws Exception {

        UserInfo userInfo = Session.getUserInfo();
        String username = userInfo != null? userInfo.getUsername() : "default";
        return getRestTemplateKdm(username);
    }
    public RestTemplate getRestTemplate() throws Exception {
        return getRestTemplateKdm().getRestTemplate();
    }
    public RestTemplateKdm getRestTemplateKdm(String userId){
//        if(instances != null && instances.containsKey(userId))
//            return instances.get(userId).getRestTemplate();

        RestTemplateKdm restTemplateKdm = new RestTemplateKdm(userId);
        //instances.put(userId, restTemplateKdm);

//        return instances.get(userId);
        return restTemplateKdm;
    }


    public class RestTemplateKdm{
        private RestTemplate restTemplate;
        private String userId;

        public RestTemplateKdm(String userId){
            super();
            this.userId = userId;
            this.restTemplate = new RestTemplate();
            setMessageConverterMixed();
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public RestTemplate getRestTemplate() {
            return restTemplate;
        }

        public void setRestTemplate(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        private void setMessageConverterMixed() {
            List<HttpMessageConverter<?>> converter = new ArrayList<HttpMessageConverter<?>>();
            FormHttpMessageConverter httpConverter = new FormHttpMessageConverter();
            httpConverter.setCharset(Charset.forName("UTF-8"));
            converter.add(httpConverter);
            StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
            converter.add(stringConverter);
            MappingJackson2HttpMessageConverter mappingJacksonConverter = new MappingJackson2HttpMessageConverter();
            converter.add(mappingJacksonConverter);
            restTemplate.setMessageConverters(converter);
        }
    }

}
