package br.com.arlei.blog.service.impl;

import br.com.arlei.blog.model.Artigo;
import br.com.arlei.blog.model.ArtigoStatusCount;
import br.com.arlei.blog.model.Autor;
import br.com.arlei.blog.model.AutorTotalPorArtigo;
import br.com.arlei.blog.repository.ArtigoRepository;
import br.com.arlei.blog.repository.AutorRepository;
import br.com.arlei.blog.service.ArtigoService;
import com.mongodb.DuplicateKeyException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {

    private final MongoTemplate mongoTemplate;

    private ArtigoRepository artigoRepository;

    private AutorRepository autorRepository;

    @Autowired
    private MongoTransactionManager mongoTransactionManager;

    @Autowired
    public ArtigoServiceImpl(MongoTemplate mongoTemplate, ArtigoRepository artigoRepository, AutorRepository autorRepository) {
        this.mongoTemplate = mongoTemplate;
        this.artigoRepository = artigoRepository;
        this.autorRepository = autorRepository;

    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> obterTodos() {
        return artigoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Artigo obterPorCodigo(String codigo) {
        return artigoRepository.findById(codigo)
                .orElseThrow(()-> new IllegalArgumentException("Não existe o codigo"));
    }

    // tem quase 3 formas de fazer a comentada a outra com artigo e esta....
    @Override
    public ResponseEntity<?> criar(Artigo artigo) {
        if(artigo.getAutor().getCodigo() != null)
        {
            Autor autor = this.autorRepository
                    .findById(artigo.getAutor().getCodigo())
                    .orElseThrow(()->new IllegalArgumentException("Autor inexistente"));

            artigo.setAutor(autor);

        }
        else
        {
            artigo.setAutor(null);
        }


        try {

            this.artigoRepository.save(artigo);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (DuplicateKeyException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Artigo ja existe na coleção");

        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("erro ao criar artigo " + e.getMessage());
        }


    }

    // usando a tarnsação da classe do config
    @Override
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor)
    {

        // não precisa mais do @Transactional pode ser das 2 formas com mis codigo e a anotação... esta e um 2 forma
        // mas para usar o template preciso do esquema da config....
        TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
        transactionTemplate.execute(
                status->{
                    try {
                        autorRepository.save(autor);  // ideal usar da propria service do autor...
                        artigo.setData(LocalDateTime.now()); // usar no projeto estacionamento
                        artigo.setAutor(autor);
                        artigoRepository.save(artigo);

                    } catch (Exception ex){

                        status.setRollbackOnly();
                        throw new RuntimeException("erro ao criar artigo com autor: " + ex.getMessage());


                    }
                    return null;

                });
                return null;

    }

    @Override
    public void excluirArtigoComAutor(Artigo artigo) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
        transactionTemplate.execute(
                status->{
                    try {
                        artigoRepository.delete(artigo);
                        Autor autor = artigo.getAutor();
                        autorRepository.delete(autor);
                    } catch (Exception ex){
                        status.setRollbackOnly();
                        throw new RuntimeException("erro ao excluir artigo com autor: " + ex.getMessage());
                    }
                    return null;
                });
    }

    // forma mais adequada de controlar pelo
    @Override
    public ResponseEntity<?> AtualizarArtigo(String id, Artigo artigo) {
        try {
            Artigo artigoExistente = this.artigoRepository
                    .findById(id).orElse(null);

            if (artigoExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Artigo não encontrado");
            }

            artigoExistente.setTitulo(artigo.getTitulo());
            artigoExistente.setData(artigo.getData());
            artigoExistente.setStatus(artigo.getStatus());

            this.artigoRepository.save(artigoExistente);

            return ResponseEntity.status(HttpStatus.OK).build();


        }catch(Exception e){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro ao atualizar "
            + e.getMessage());
        }


    }

    /*@Transactional
    @Override
    public Artigo criar(Artigo artigo) {

        if(artigo.getAutor().getCodigo() != null)
        {

            Autor autor = this.autorRepository
                    .findById(artigo.getAutor().getCodigo())
                    .orElseThrow(()->new IllegalArgumentException("Autor inexistente"));

            artigo.setAutor(autor);

        } else
        {
            artigo.setAutor(null);
        }

        // implementar exceção do controle de versão
        try{

            return artigoRepository.save(artigo);

        }
        catch(OptimisticLockingFailureException ex){
            //desenvolver a estratégia
            // 1. Recuperar o documento mais recente no banco de dados (na coleção artigos)
            Artigo atualizado  =
                    artigoRepository.findById(artigo.getCodigo()).orElse(null);
            if(atualizado != null){
                //2. Atualizar os campos desejados
                atualizado.setTitulo(artigo.getTitulo());
                atualizado.setTexto(artigo.getTexto());
                atualizado.setStatus(artigo.getStatus());

                //3. Incrementar a versão manual do documento

                atualizado.setVersion(atualizado.getVersion() + 1);

                //
                return artigoRepository.save(artigo);
            }
                    else
            {
                throw  new RuntimeException("Artigo não encontrado" + artigo.getCodigo());
            }

        }


    }
*/
    @Transactional
    @Override
    public void atualizar(Artigo artigo) {

           Artigo artigoAtualiza = this.artigoRepository
                    .findById(artigo.getCodigo())
                    .orElseThrow(()->new IllegalArgumentException("Artigo inexistente"));

            artigoRepository.save(artigo);

    }

    @Transactional
    @Override
    public void atualizarArtigo(String id, String novaurl) {
        Query query = new Query(Criteria.where("codigo").is(id));
        Update update = new Update().set("url",novaurl);
        this.mongoTemplate.updateFirst(query,update,Artigo.class);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {

        // Classes para fazer a pesquisa de forma personalidaza
        Query query = new Query(Criteria.where("data").gt(data));
        // tem que fazer referencia a classe compilada que vai receber o resultado.
        return mongoTemplate.find(query,Artigo.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status) {

            /// data igual e status igual .. and
            Query query = new Query(Criteria.where("data").is(data).and("status").is(status));

            // tem que fazer referencia a classe compilada que vai receber o resultado.
            return mongoTemplate.find(query,Artigo.class);
        }

    @Transactional
    @Override
    public void deleteById(String id) {
        this.artigoRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteArtigoById(String id) {

        Query query = new Query(Criteria.where("codigo").is(id));
        this.mongoTemplate.remove(query,Artigo.class);

    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data) {
        return artigoRepository.findByStatusAndDataGreaterThan(status,data);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> findByStatusEquals(Integer status) {
        return artigoRepository.findByStatusEquals(status);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return this.artigoRepository.obterArtigoPorDataHora(de,ate);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo) {
        Criteria criteria = new Criteria();
        criteria.and("data").lte(data);

        if(status != null)
            criteria.and("status").is(status);
        if(titulo != null && !titulo.isEmpty())
            criteria.and("titulo").regex(titulo,"i");
        Query query = new Query(criteria);


        return mongoTemplate.find(query,Artigo.class);
    }
    // ordenação de forma direta as classes do repository

    @Transactional(readOnly = true)
    @Override
    public Page<Artigo> listaArtigos(Pageable pageable) {

        /// Page ordenado
        Sort sort = Sort.by("titulo").ascending();
        Pageable paginacao = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return artigoRepository.findAll(paginacao);
    }
    // ordenação por Query Methods
    @Transactional(readOnly = true)
    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return this.artigoRepository.findByStatusOrderByTituloAsc(status);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status) {
        return this.artigoRepository.obterArtigoPorStatusComOrdenacao(status);
    }

    // Trabalhar com indexes e neste caso tbm faz um esquema similar a ao like do sql
    @Transactional(readOnly = true)
    @Override
    public List<Artigo> findByTexto(String searchItem) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(searchItem);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query,Artigo.class);
    }

    // Agregações
    @Transactional(readOnly = true)
    @Override
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        TypedAggregation<Artigo> aggregation =
                    Aggregation.newAggregation(
                            Artigo.class,
                            Aggregation.group("status").count().as("quantidade"),
                            Aggregation.project("quantidade").and("status")
                                    .previousOperation()
                    );
        AggregationResults<ArtigoStatusCount> result =
                            mongoTemplate.aggregate(aggregation,ArtigoStatusCount.class);

        return result.getMappedResults();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AutorTotalPorArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate inicio,
                                                                           LocalDate fim)
    {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.match(
                                Criteria.where("data")
                                        .gte(inicio.atStartOfDay())
                                        .lt(fim.plusDays(1).atStartOfDay())
                        ),
                        Aggregation.group("autor").count().as("totalArtigos"),
                        Aggregation.project("totalArtigos").and("autor")
                                .previousOperation()
                );
        AggregationResults<AutorTotalPorArtigo> result =
                mongoTemplate.aggregate(aggregation,AutorTotalPorArtigo.class);

        return result.getMappedResults();
    }

}





