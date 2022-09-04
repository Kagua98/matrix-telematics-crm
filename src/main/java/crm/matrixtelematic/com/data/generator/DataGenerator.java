package crm.matrixtelematic.com.data.generator;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import crm.matrixtelematic.com.data.entity.Clients;
import crm.matrixtelematic.com.data.service.ClientsRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(ClientsRepository clientsRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (clientsRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Clients entities...");
            ExampleDataGenerator<Clients> clientsRepositoryGenerator = new ExampleDataGenerator<>(Clients.class,
                    LocalDateTime.of(2022, 9, 4, 0, 0, 0));
            clientsRepositoryGenerator.setData(Clients::setClientName, DataType.WORD);
            clientsRepositoryGenerator.setData(Clients::setKraPin, DataType.WORD);
            clientsRepositoryGenerator.setData(Clients::setContact, DataType.WORD);
            clientsRepositoryGenerator.setData(Clients::setAddress, DataType.WORD);
            clientsRepository.saveAll(clientsRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}
