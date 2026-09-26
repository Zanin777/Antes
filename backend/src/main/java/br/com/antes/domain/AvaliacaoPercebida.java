package br.com.antes.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;

/** Na API: inteiro 1–5 OU a string NAO_SE_APLICA. No banco: enum nominal, nunca zero. */
public enum AvaliacaoPercebida {
    NIVEL_1(1), NIVEL_2(2), NIVEL_3(3), NIVEL_4(4), NIVEL_5(5), NAO_SE_APLICA(null);
    private final Integer nota;
    AvaliacaoPercebida(Integer nota) { this.nota = nota; }
    public boolean aplicavel() { return nota != null; }
    public int nota() {
        if (!aplicavel()) throw new IllegalStateException("NAO_SE_APLICA não possui nota numérica");
        return nota;
    }
    @JsonValue public Object valorJson() { return aplicavel() ? nota : "NAO_SE_APLICA"; }
    @JsonCreator
    public static AvaliacaoPercebida deJson(JsonNode valor) {
        if (valor != null && valor.isTextual() && "NAO_SE_APLICA".equals(valor.textValue())) return NAO_SE_APLICA;
        if (valor != null && valor.isIntegralNumber() && valor.canConvertToInt() && valor.intValue() >= 1 && valor.intValue() <= 5)
            return values()[valor.intValue() - 1];
        throw new IllegalArgumentException("Use um inteiro de 1 a 5 ou a string NAO_SE_APLICA");
    }
}
