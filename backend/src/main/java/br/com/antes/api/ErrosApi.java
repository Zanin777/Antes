package br.com.antes.api;

import br.com.antes.service.RegraNegocioException;
import java.time.DateTimeException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@RestControllerAdvice
public class ErrosApi {
    public record ErroCampo(String campo,String mensagem) {}
    @ExceptionHandler(RegraNegocioException.class)
    ResponseEntity<ProblemDetail> negocio(RegraNegocioException e) { return erro(e.getStatus(),e.getCodigo(),e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validacao(MethodArgumentNotValidException e) {
        var detalhe=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Revise os campos informados");
        detalhe.setProperty("codigo","validacao");
        List<ErroCampo> campos=e.getBindingResult().getFieldErrors().stream().map(f->new ErroCampo(f.getField(),f.getDefaultMessage())).toList();
        detalhe.setProperty("campos",campos);
        return ResponseEntity.badRequest().body(detalhe);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ProblemDetail> jsonInvalido() { return erro(HttpStatus.BAD_REQUEST,"json_invalido","JSON inválido. Confira campos, datas e enums. Nas avaliações, use inteiro de 1 a 5 ou NAO_SE_APLICA; zero não é válido."); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> integridade() { return erro(HttpStatus.CONFLICT,"integridade","O registro viola uma restrição ou já existe para este experimento"); }
    @ExceptionHandler(PessimisticLockingFailureException.class)
    ResponseEntity<ProblemDetail> concorrencia() { return erro(HttpStatus.CONFLICT,"concorrencia","Outra gravação está em andamento. Consulte o registro antes de tentar novamente."); }
    @ExceptionHandler(DateTimeException.class)
    ResponseEntity<ProblemDetail> dataInvalida() { return erro(HttpStatus.BAD_REQUEST,"data_invalida","A data informada não permite formar uma janela válida de sete dias"); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ProblemDetail> metodo() { return erro(HttpStatus.METHOD_NOT_ALLOWED,"metodo_nao_permitido","Este recurso não oferece essa operação"); }
    private static ResponseEntity<ProblemDetail> erro(HttpStatus status,String codigo,String mensagem) {
        var detalhe=ProblemDetail.forStatusAndDetail(status,mensagem);detalhe.setProperty("codigo",codigo);
        return ResponseEntity.status(status).body(detalhe);
    }
}
