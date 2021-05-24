package swp490.spa.utils.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("swp490.spa.entities")
@EnableJpaRepositories("swp490.spa.repositories")
public class DatasourceConfig {

}
