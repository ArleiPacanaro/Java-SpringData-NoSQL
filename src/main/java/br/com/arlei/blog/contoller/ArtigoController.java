package br.com.arlei.blog.contoller;

import br.com.arlei.blog.model.*;
import br.com.arlei.blog.service.ArtigoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value="/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    /// tem que ter a anotação... e o nome correto para o @Transactioal identificar o metodo e lanças a exceção...
    // ideal e ter na controller. vai capturar uma exceção nas chamadas dos metodos...

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity handleOptimisticLockingFailureException(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(" Erro de Concorrência, Artigo foi atualizado por outro usuário, tente novamente");
    }

    @PostMapping
    public ResponseEntity<?> CriarArtigoComAutor(
            @RequestBody ArtigoComAutorRequest request
    )
    {
        Artigo artigo   = request.getArtigo();
        Autor autor     = request.getAutor();
        return this.artigoService.criarArtigoComAutor(artigo,autor);

    }

    @GetMapping
    public List<Artigo> obterTodos(){
        return  artigoService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo){
        return  artigoService.obterPorCodigo(codigo);

    }


   /*
   outras formas....
   @PostMapping
    public Artigo criar(@RequestBody Artigo artigo){
        return artigoService.criar(artigo);


   @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Artigo artigo){
        return artigoService.criar(artigo);
    }

    */

    // para o novo metodo com tratamento de exceção try catch mais adequado
    @PutMapping("/atualizar-artigo/{id}")
    public ResponseEntity<?> atualizarArtigo(@PathVariable("id") String id ,@Valid @RequestBody Artigo artigo)
    {
        return artigoService.AtualizarArtigo(id,artigo);
    }


    @GetMapping("/maiordata")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data")LocalDateTime data){
        return this.artigoService.findByDataGreaterThan(data);

    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(@RequestParam("data")LocalDateTime data
                                           ,@RequestParam("status")Integer status)
    {
        return this.artigoService.findByDataAndStatus(data,status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo artigo){

        this.artigoService.atualizar(artigo);
        
    }

    @PutMapping("/{id}")
    public void atualizarArtigo(@PathVariable String id, @RequestBody String novaurl){

        this.artigoService.atualizarArtigo(id,novaurl);

    }

    @DeleteMapping("/delete-artigo-autor")
    public void excluirArtigoEAutor(@RequestBody Artigo artigo){
        this.artigoService.excluirArtigoComAutor(artigo);

    }


    @DeleteMapping("/{id}")
    public void deleteArtigo(@PathVariable String id){

        this.artigoService.deleteById(id);
    }

    @DeleteMapping("/delete")
    public void deleteArtigoById(@RequestParam("id") String id){
        this.artigoService.deleteArtigoById(id);

    }

    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime data
    ){
        // Usando QueryMethods o proprio spring data faz a interpretação pelos nomes das colunas como no JPA
        return artigoService.findByStatusAndDataGreaterThan(status,data);
    }

    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(@RequestParam("de") LocalDateTime de
                                            , @RequestParam("ate") LocalDateTime ate)
    {
        return this.artigoService.obterArtigoPorDataHora(de,ate);
    }

    @GetMapping("/artigos-complexo")
    public List<Artigo> encontrarArtigosComplexos(@RequestParam("status") Integer status,
                                                  @RequestParam("data") LocalDateTime data,
                                                  @RequestParam("titulo") String titulo){

        return artigoService.encontrarArtigosComplexos(status, data, titulo);
    }


    @GetMapping("/pagina-artigos")
    public ResponseEntity<Page<Artigo>> ObterArtigosPaginados(Pageable pageable) {
        Page<Artigo> artigos = this.artigoService.listaArtigos(pageable);
        return ResponseEntity.ok(artigos);
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(@RequestParam("status") Integer status){

            return artigoService.findByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(@RequestParam("status") Integer status){
        return artigoService.obterArtigoPorStatusComOrdenacao(status);
    }

    @GetMapping("/buscatexto")
    public List<Artigo> findByTexto(@RequestParam("searchItem") String searchItem){

        /// e faz pegando uma parte do texto e nem precisa ser de toda string....
        return this.artigoService.findByTexto(searchItem);


    }

    @GetMapping("/contar-artigo")
    public List<ArtigoStatusCount> contarArtigosPorStatus(){
        return this.artigoService.contarArtigosPorStatus();
    }

    @GetMapping("/total-artigo-autor-periodo")
    public List<AutorTotalPorArtigo> calcularTotalArtigosPorAutorNoPeriodo(@RequestParam("inicio") LocalDate inicio,
                                                                           @RequestParam("fim")LocalDate fim)
    {
        return this.artigoService.calcularTotalArtigosPorAutorNoPeriodo(inicio,fim);

    }

}
