package com.example.announcement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


@Configuration // 表示這是一個配置類，等同於 Spring 的 XML 配置文件，Spring 容器會在啟動時加載這個類，並應用其中的配置。
@EnableWebMvc // 啟用 Spring MVC 的配置，將其用作 Web 應用程序的支持。包括註冊 Spring MVC 的核心組件（例如 DispatcherServlet）。
@ComponentScan(basePackages = "com.example.announcement") // 指定要掃描的基礎包，Spring 會自動檢測並註冊該包中的所有 @Controller、@Service、@Repository 等註解的類。
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); // 設置模板路徑
        resolver.setSuffix(".html"); // 設置模板後綴
        resolver.setTemplateMode("HTML5"); // 設置模板模式
        resolver.setCharacterEncoding("UTF-8"); // 設置字符編碼
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver()); // 設置模板解析器
        engine.setEnableSpringELCompiler(true); // 啟用 Spring 表達式語言編譯
        return engine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine()); // 設置模板引擎
        resolver.setCharacterEncoding("UTF-8"); // 設置字符編碼
        registry.viewResolver(resolver); // 註冊視圖解析器
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // 靜態資源的 URL 映射
                .addResourceLocations("classpath:/static/"); // 靜態資源的物理路徑
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 創建並配置 DateTimeFormatterRegistrar
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(false); // 若需要 ISO 格式，設為 true
        registrar.setDateFormatter(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 註冊到 FormatterRegistry
        registrar.registerFormatters(registry);
    }
    
    
    //設置文件上傳解析器
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(10 * 1024 * 1024); // 最大 10 MB
        return resolver;
    }
    
}