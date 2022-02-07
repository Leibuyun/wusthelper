package com.linghang.wusthelper.wustlib.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

@Component
public class ExecJSConfig {

    @Bean
    public Invocable invoke(@Value("${wusthelper.wustlib.js-path}") String filePath) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");
            // 读取ClassPath下的fun.js文件, 用于执行js代码
            // 注释的代码, maven打包后无法加载资源文件
//            ClassPathResource classPathResource = new ClassPathResource("fun.js");
//            FileReader reader = new FileReader(classPathResource.getFile());
            FileReader reader = new FileReader(new File(filePath));
            engine.eval(reader);
            return (Invocable) engine;    // 调用myfun方法，并传入参数
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
