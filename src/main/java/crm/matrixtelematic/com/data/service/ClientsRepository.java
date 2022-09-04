package crm.matrixtelematic.com.data.service;

import crm.matrixtelematic.com.data.entity.Clients;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientsRepository extends JpaRepository<Clients, UUID> {

}