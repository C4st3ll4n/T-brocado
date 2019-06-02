package br.com.ifood.cursoandroid.ifood.helper;

import java.text.Normalizer;

public class StringHelper {
    public static String removerCaracteresEspeciais(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        return string;
    }
}
