package br.com.arlei.blog.service.impl;

import br.com.arlei.blog.model.Autor;
import br.com.arlei.blog.repository.AutorRepository;
import br.com.arlei.blog.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutorServiceImpl implements AutorService {

    AutorRepository autorRepository;

    @Autowired
    public AutorServiceImpl(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Override
    public Autor criar(Autor autor) {
        return  this.autorRepository.save(autor);
    }

    @Override
    public Autor obterPorCodigo(String codigo) {
        return  this.autorRepository.findById(codigo)
                .orElseThrow(()-> new IllegalArgumentException("Não existe o codigo"));
    }
}
