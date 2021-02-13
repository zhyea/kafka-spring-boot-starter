package org.chobit.spring.config;


import org.chobit.spring.KafkaConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * kafka stater annotation
 *
 * @author robin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(KafkaConfiguration.class)
public @interface EnableKafkaClients {


}
