package com.gef.minhasFinancas.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity

// Anotações do Lombok
// A anotação @Data equivale à @Getter, @Setter, @EqualsAndHashCode, @ToString e @ RequiredargsConstructor
// @Data será usada nas outras classes.
@Getter
@Setter
@EqualsAndHashCode
@ToString

@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "usuario", schema="financas")
public class Usuario {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nome")
	private String nome;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "senha")
	private String senha;

	public Long getId() {
		return id;
	}
}
