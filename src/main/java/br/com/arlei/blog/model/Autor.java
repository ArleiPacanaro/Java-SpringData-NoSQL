package br.com.arlei.blog.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Autor {

    @Id
    private String codigo;
    private String nome;
    private String biografia;
    private String imagem;

}
