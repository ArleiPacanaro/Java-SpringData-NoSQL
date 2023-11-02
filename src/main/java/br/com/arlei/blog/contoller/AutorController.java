package br.com.arlei.blog.contoller;

import br.com.arlei.blog.model.Autor;
import br.com.arlei.blog.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/autores")
public class AutorController {

    AutorService autorService;

    @Autowired
    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @PostMapping
    public Autor criar(@RequestBody Autor autor){

        return  this.autorService.criar(autor);

    }

    @GetMapping("/{codigo}")
    public Autor ObterPorCodigo(@PathVariable String codigo){
        return this.autorService.obterPorCodigo(codigo);
    }
}
