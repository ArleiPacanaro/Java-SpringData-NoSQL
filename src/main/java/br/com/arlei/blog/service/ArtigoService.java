package br.com.arlei.blog.service;


import br.com.arlei.blog.model.Artigo;
import br.com.arlei.blog.model.ArtigoStatusCount;
import br.com.arlei.blog.model.Autor;
import br.com.arlei.blog.model.AutorTotalPorArtigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {

    public List<Artigo> obterTodos();
    public Artigo obterPorCodigo(String codigo);
    //public Artigo criar(Artigo artigo);
    public ResponseEntity<?> criar(Artigo artigo);
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor);
    public void excluirArtigoComAutor(Artigo artigo);
    public ResponseEntity<?> AtualizarArtigo(String id,Artigo artigo);
    public void atualizar(Artigo artigo);
    public void atualizarArtigo(String id, String url);
    public List<Artigo> findByDataGreaterThan(LocalDateTime data);
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status);
    public void deleteById(String id);
    public void deleteArtigoById(String id);
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data);
    public List<Artigo> findByStatusEquals(Integer status);
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);
    public List<Artigo> encontrarArtigosComplexos(Integer status,
                                                  LocalDateTime data,
                                                  String titulo);
    public Page<Artigo> listaArtigos(Pageable pageable);
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status);

    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);

    public List<Artigo> findByTexto(String searchItem);
    public List<ArtigoStatusCount> contarArtigosPorStatus();
    public List<AutorTotalPorArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate inicio, LocalDate fim);



}
