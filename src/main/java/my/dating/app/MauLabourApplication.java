package my.dating.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// Boot's own JDBC/JPA autoconfiguration is excluded: DatabaseImpl registers every DataSource, EntityManagerFactory and
// TransactionManager itself, and Boot's would build a second, unused stack over the same entities (see the DatabaseImpl README).
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataJpaRepositoriesAutoConfiguration.class
})
@ComponentScan({"my.dating.app", "org.solarframework.*"})
@EnableAsync // MatchNotifier works after a profile save without making the member wait
public class MauLabourApplication {

    static void main(String[] args) {
        SpringApplication.run(MauLabourApplication.class, args);
        SolarDBManager.verifyEntities();
    }

}
