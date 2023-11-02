package br.com.arlei.blog.repository;

import br.com.arlei.blog.model.Artigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArtigoRepository extends MongoRepository<Artigo, String> {

    public void deleteById(String id);
    // O Sring Data ja assume os campos pela entidade e as condições....
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data);
    public List<Artigo> findByStatusEquals(Integer status);

    // comandos que no mongoshell vão dentro do parametro do find
    @Query("{ $and: [{'data':{$gte:?0}},{'data':{$lte:?1}}] }")
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);

    // named query como no deleteById... não precisava mesmo do delete
    public Page<Artigo> findAll(Pageable pageable);

    // ordenação por query method --- 1 na Repository
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status);

    // ordenação por query  --- 1 na Repository 1 asc e -1 desc se passar no parametro do metodo find no mongoshell deve funcionar
    @Query(value="{'status': {$eq:?0}}",sort="{'titulo':1}")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);


}
