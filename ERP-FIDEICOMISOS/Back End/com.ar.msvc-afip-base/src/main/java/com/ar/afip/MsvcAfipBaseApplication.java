package com.ar.afip;

import com.ar.afip.config.JksProperties;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication()
@EnableConfigurationProperties(JksProperties.class)
@EnableFeignClients
public class MsvcAfipBaseApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MsvcAfipBaseApplication.class, args);
        Security.addProvider(new BouncyCastleProvider());
    }

}
