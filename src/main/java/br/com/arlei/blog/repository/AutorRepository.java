package br.com.arlei.blog.repository;

import br.com.arlei.blog.model.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends MongoRepository<Autor,String> {
}
