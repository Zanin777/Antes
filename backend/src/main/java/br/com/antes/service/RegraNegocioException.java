package br.com.antes.service;

import org.springframework.http.HttpStatus;

public class RegraNegocioException extends RuntimeException {
    private final HttpStatus status;
    private final String codigo;
    private RegraNegocioException(HttpStatus status,String codigo,String mensagem) { super(mensagem); this.status=status; this.codigo=codigo; }
    public HttpStatus getStatus() { return status; }
    public String getCodigo() { return codigo; }
    public static RegraNegocioException invalida(String mensagem) { return new RegraNegocioException(HttpStatus.BAD_REQUEST,"regra_invalida",mensagem); }
    public static RegraNegocioException conflito(String mensagem) { return new RegraNegocioException(HttpStatus.CONFLICT,"estado_incompativel",mensagem); }
    public static RegraNegocioException ausente(String recurso) { return new RegraNegocioException(HttpStatus.NOT_FOUND,"nao_encontrado",recurso+" não encontrado"); }
}
