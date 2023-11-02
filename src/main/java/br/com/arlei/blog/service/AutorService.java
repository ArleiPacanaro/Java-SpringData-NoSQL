package br.com.arlei.blog.service;

import br.com.arlei.blog.model.Autor;

public interface AutorService {

    public Autor criar(Autor autor);
    public Autor obterPorCodigo(String codigo);

}
