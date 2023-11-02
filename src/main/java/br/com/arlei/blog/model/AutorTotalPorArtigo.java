package br.com.arlei.blog.model;

import br.com.arlei.blog.model.Autor;
import lombok.Data;


@Data
public class AutorTotalPorArtigo {

    private Autor autor;
    private Integer totalArtigos;

}
