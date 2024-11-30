package com.jennyduarte.sis.service;
import com.jennyduarte.sis.entity.Contacto;
import com.jennyduarte.sis.repository.ContactoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactoService extends BaseService<Contacto, Long> {
    public ContactoService(ContactoRepository repository) {
        super(repository);
    }
}
