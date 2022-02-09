package com.security.tokensecurity.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/token")
@RestController
public class UserController {

    final String URL_LOCAL = "http://localhost:8080/user/searchId";

    @GetMapping("/getUser")
    public Object getUserEntity(){
        List<HttpMessageConverter<?>> converts = new ArrayList<HttpMessageConverter<?>>();
        converts.add(new FormHttpMessageConverter());
        converts.add(new StringHttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(converts);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String,String>();
        map.add("id", "admin");

        String result = restTemplate.postForObject(URL_LOCAL, map, String.class) ;

        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.println(result.toString());
        System.out.println("------------------------------------------------------------------------------------------------------");
        return result;
    }

    @GetMapping("/getUser2")
    public Object getUserEntity2(){
        List<HttpMessageConverter<?>> converts = new ArrayList<HttpMessageConverter<?>>();
        converts.add(new FormHttpMessageConverter());
        converts.add(new StringHttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(converts);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String,String>();
        map.add("id", "admin");

        //String result = restTemplate.postForObject(URL_LOCAL, map, String.class) ;
        //ResponseEntity<?> result = restTemplate.postForObject(URL_LOCAL, map, ResponseEntity.class) ;
        ResponseEntity<?> result = restTemplate.postForEntity(URL_LOCAL, map, ResponseEntity.class);

        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.println(result.toString());
        System.out.println("------------------------------------------------------------------------------------------------------");
        return result;
    }

    /*    //다른 방법 인데 실패작
    @GetMapping("/get1")
    public Object getUserEntity1(){
        System.out.println("get1211111111111111111111111111111111111111");
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(URI.create("http://localhost:8080/user/searchId"))
                    .queryParam("id","adimn");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        System.out.println(httpEntity.getBody() + "    ::: builder" + builder.toString());
            System.out.println(httpEntity.getBody());

        return httpEntity;
    }



    @GetMapping("/get2")
    public String getUserEntity2(){

        HashMap<String,Object> result = new HashMap<String,Object>();

        String jsonInString = "";

        try {
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);
            RestTemplate restTemplate = new RestTemplate(factory);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String uri = "";

            ResponseEntity<Map> resultMap = restTemplate.exchange("http://localhost:8080/user/searchId", HttpMethod.GET, entity, Map.class);
            result.put("statusCode", resultMap.getStatusCodeValue());
            result.put("header",resultMap.getHeaders());
            result.put("body",resultMap.getBody()) ;

            ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(resultMap.getBody());
        }catch (HttpClientErrorException | HttpServerErrorException e){
            result.put("statusCode",e.getRawStatusCode());
            result.put("body",e.getStatusCode());
            System.out.println("-------------------------------------");
            System.out.println(e.toString());

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return jsonInString;
    }
     */
}
