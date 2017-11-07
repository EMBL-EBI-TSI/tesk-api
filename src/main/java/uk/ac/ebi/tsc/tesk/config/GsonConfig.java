package uk.ac.ebi.tsc.tesk.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ania Niewielska <aniewielska@ebi.ac.uk>
 */
@Configuration
public class GsonConfig {
    @Bean
    Gson gson() {
        return new Gson();
    }
}
