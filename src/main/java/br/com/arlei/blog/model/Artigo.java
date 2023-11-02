package br.com.arlei.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document
public class Artigo {
    @Id
    private String codigo;

    @NotBlank(message="O Título do artigo não pode estar em branco.")
    @TextIndexed
    private String texto;
    // criar no banco também

    private String url;

    @NotBlank(message="O Título do artigo não pode estar em branco.")
    private String titulo;

    @NotNull(message = "Status não pode ser nulo")
    private Integer status;

    @NotNull(message = "A data do artigo não pode ser nula")
    private LocalDateTime data;

    @DBRef
    private Autor autor;

    // Controlar a transação mas tem que importar no POM spring-boot-starter-data-mongodb-reactive
    @Version
    private Long version;

}
