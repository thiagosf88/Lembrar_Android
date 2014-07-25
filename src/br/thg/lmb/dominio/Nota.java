package br.thg.lmb.dominio;


public class Nota {

	private Long id;
	private String nota = "";
	private String titulo = "";
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String toString(){
		return "id: " + this.id + " título: " + this.titulo + " nota: " + this.nota + "\n"; 
	}

}
