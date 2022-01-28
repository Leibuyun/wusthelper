package com.linghang.wusthelper.wustyjs.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tess4jConfig {

    @Bean
    public Tesseract createTesseract(@Value("${wusthelper.wustyjs.tessdataPath}") String dataPath) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
        return tesseract;
    }

}
