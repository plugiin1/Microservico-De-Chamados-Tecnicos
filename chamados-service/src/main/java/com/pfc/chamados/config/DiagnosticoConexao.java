package com.pfc.chamados.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiagnosticoConexao {

    private static final Pattern CREDENCIAIS = Pattern.compile("^mongodb\\+srv://([^:]+):([^@]+)@(.+)$");

    private DiagnosticoConexao() {
    }

    public static void executar() {
        String uri = System.getenv("MONGODB_URI");
        String database = System.getenv("MONGODB_DATABASE");

        if (uri == null) {
            System.out.println("DIAG MONGODB_URI ausente no ambiente");
            return;
        }

        System.out.println("DIAG uri.tamanho=" + uri.length());
        System.out.println("DIAG uri.prefixo=[" + uri.substring(0, Math.min(14, uri.length())) + "]");
        System.out.println("DIAG database=[" + database + "]");

        Matcher m = CREDENCIAIS.matcher(uri);
        if (!m.matches()) {
            System.out.println("DIAG uri fora do padrao esperado");
            return;
        }

        String usuario = m.group(1);
        String senha = m.group(2);
        String resto = m.group(3);

        System.out.println("DIAG usuario=[" + usuario + "] tamanho=" + usuario.length());
        System.out.println("DIAG senha.tamanho=" + senha.length() + " hash=" + hash(senha)
                + " alfanumerica=" + senha.matches("^[A-Za-z0-9]+$"));
        System.out.println("DIAG resto=[" + resto + "]");
    }

    private static String hash(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes).substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            return "indisponivel";
        }
    }
}
