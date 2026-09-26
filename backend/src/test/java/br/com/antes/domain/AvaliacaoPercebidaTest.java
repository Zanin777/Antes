package br.com.antes.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

class AvaliacaoPercebidaTest {
    private final ObjectMapper json=new ObjectMapper();
    @ParameterizedTest @ValueSource(ints={1,2,3,4,5})
    void notasSaoNumerosNoJson(int nota) throws Exception {
        var valor=json.readValue(Integer.toString(nota),AvaliacaoPercebida.class);
        assertThat(valor.nota()).isEqualTo(nota);
        assertThat(json.writeValueAsString(valor)).isEqualTo(Integer.toString(nota));
    }
    @Test void ausenciaTemRepresentacaoPropria() throws Exception {
        var valor=json.readValue("\"NAO_SE_APLICA\"",AvaliacaoPercebida.class);
        assertThat(valor.aplicavel()).isFalse();
        assertThatThrownBy(valor::nota).isInstanceOf(IllegalStateException.class);
        assertThat(json.writeValueAsString(valor)).isEqualTo("\"NAO_SE_APLICA\"");
    }
    @ParameterizedTest @ValueSource(strings={"0","6","-1","1.5","1.0","\"1\"","\"NIVEL_1\"","true","{}","[]"})
    void rejeitaValoresAmbiguosOuInvalidos(String valor) {
        assertThatThrownBy(()->json.readValue(valor,AvaliacaoPercebida.class)).isInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }
}
