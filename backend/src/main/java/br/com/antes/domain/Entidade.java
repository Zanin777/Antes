package br.com.antes.domain;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Entidade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    public Long getId() { return id; }
}
