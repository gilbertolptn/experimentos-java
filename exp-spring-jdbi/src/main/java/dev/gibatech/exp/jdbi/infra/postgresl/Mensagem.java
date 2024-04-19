package dev.gibatech.exp.jdbi.infra.postgresl;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;

@Data
public class Mensagem {
    private Long id;
    private String mensagem;
    private LocalDateTime dataHora;

    @ConstructorProperties({"id", "mensagem", "dataHora"})
    public Mensagem(Long id, String mensagem, LocalDateTime dataHora) {
        this.id = id;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }
}
