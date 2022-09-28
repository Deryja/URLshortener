package com.example.urlshortener2;


import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

//Check out the read me file to know how to use this API on Postman.

//Det som skjer er at en url blir tatt inn og url id blir generert istedenfor (i form av en hash)

@RequestMapping("/rest/url")

@RestController
public class UrlShortener {

    @Autowired
      StringRedisTemplate redisTemplate;

    @GetMapping("/{id}")

    //Steg 5: siste steg --> Det som skjer her er at urlen blir mottat og vi pusher (returnerer) den.
    public String getUrl(@PathVariable String id){

        String url = redisTemplate.opsForValue().get(id);
        System.out.println("URL retrieved "+ url);

        if (url == null){throw new RuntimeException("Det er ingen kortere url for " + url);
        }

        return url;
    }

    //Fortsttelse på steg 5: Nå går vi til Add-configurations og legger til server porten 8081 i spring boot

    //Steg 2: Nå som vi har requesta body under så må vi sjekke om urlen er bra eller ikke

    /*
    Dermed går vi til pom.xml og legger til i dependency: <dependency>
            <groupId>commons-validator</groupId>
            <artifactId>commons-validator</artifactId>
            <version>1.6</version>
        </dependency>
     */


    @PostMapping
    public String create(@RequestBody String url){

        //steg 2: Fortsettelse: Etter å ha lagt til dependencies, så lager vi validator av urlen nå;
        UrlValidator urlValidator = new UrlValidator(

                //Det skal fungere å bruke url shortener for både http og https linker
         new String[]{"http", "https"}
        );
//steg 3 Nå går vi til dependencies igjen og legger til guava

        //Steg 4: nå hasher vi urlen. og vi skal bruke murmurhash3 som google kan gi oss
        if (urlValidator.isValid(url)){

          String id =  Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString(); //UTF står for unique indenditifer, vi gjør den om til en string her

//Fortsettelse steg 4: vi går helt øverst og legger til @Autowired og skriver under "StringRedisTemplate"
//Nå fortsetter vi på den:

            System.out.println("The new generated URL is " + id);
            redisTemplate.opsForValue().set(id, url);
return id;}

        //Hvis urlen ikke er gyldig kaster vi en feilmelding: steg 4 fullført
        throw new RuntimeException("Invalid URL: "+url);
    }
    //Steg 5: Nå går vi til getmapping og skriver redisTemplate.opsforvalue for url og returnerer den
}


