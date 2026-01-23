package woobl0g.boardservice.global.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woobl0g.boardservice.global.query.QueryCountInspector;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {

    private final QueryCountInspector queryCountInspector;

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertyConfig() {
        return hibernateProperties ->
                hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, queryCountInspector);
    }
}
