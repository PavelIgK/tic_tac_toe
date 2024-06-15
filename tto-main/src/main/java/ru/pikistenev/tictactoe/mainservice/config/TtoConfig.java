package ru.pikistenev.tictactoe.mainservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Берем данные для конфигурирования игр из настроек приложения.
 */

@Data
@Component
@EnableScheduling
@ConfigurationProperties("application.properties")
public class TtoConfig {

    @Value("${tto.session.attr.name}")
    String attrName;

    @Value("${tto.ai-symbol}")
    String aiSymbol;

    @Value("${tto.user-symbol}")
    String userSymbol;

    @Value("${tto.inactive-time}")
    Long inactiveTime;

}