package platform;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CodeRepository  extends CrudRepository<Code, UUID> {
	Iterable<Code> findAll(Sort date);
}
