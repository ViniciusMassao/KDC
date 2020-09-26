import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Pessoas {
    private String id;
    private String key_pessoal = "";
    private String key_sessao = "";
    private static String ALGORITMO = "AES";
    private String mensagemRecebida;
    private int nonce;

    public Pessoas(String id, String key_pessoal) {
        this.id = id;
        this.key_pessoal = key_pessoal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey_pessoal() {
        return key_pessoal;
    }

    public void setKey_pessoal(String key_pessoal) {
        this.key_pessoal = key_pessoal;
    }

    public String getKey_sessao() {
        return key_sessao;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    // recebendo chave de sessao, decifrando e setando para o objeto
    public void setKey_sessao(byte[] key_sessao) throws BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            UnsupportedEncodingException,
            NoSuchPaddingException,
            InvalidKeyException {
        this.key_sessao = decifra(key_sessao, key_pessoal);
    }

    public String getMensagemRecebida() {
        return mensagemRecebida;
    }

    public void setMensagemRecebida(String mensagemRecebida) {
        this.mensagemRecebida = mensagemRecebida;
    }

    // settando chave de sessao
    public void setKey_sessao_Mensagem(){
        key_sessao = mensagemRecebida;
        mensagemRecebida = "";
    }

    // cifra
    public byte[] cifra(String texto, String chave)
            throws IllegalBlockSizeException,
            BadPaddingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            UnsupportedEncodingException,
            InvalidKeyException
    {
        Key key =
                new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), ALGORITMO);
        Cipher cifrador = Cipher.getInstance(ALGORITMO);
        cifrador.init(Cipher.ENCRYPT_MODE, key);
        byte[] textoCifrado = cifrador.doFinal(texto.getBytes());
        return textoCifrado;
    }

    // decifra
    public String decifra(byte[] texto, String chave)
            throws IllegalBlockSizeException,
            BadPaddingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            UnsupportedEncodingException,
            InvalidKeyException {
        Key key =
                new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), ALGORITMO);
        Cipher decifrador = Cipher.getInstance(ALGORITMO);
        decifrador.init(Cipher.DECRYPT_MODE, key);
        byte[] textoDecifrado = decifrador.doFinal(texto);
        return new String(textoDecifrado);
    }

    public void ImprimeMensagem(String m){
        System.out.println(this.getId()+": "+m);
    }

    // settando a chave de sessao como mensagem no destinatario
    public void enviaMensagemKSessao(byte[] mensagem, Pessoas destinatario) throws BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            UnsupportedEncodingException,
            NoSuchPaddingException,
            InvalidKeyException {
        destinatario.setMensagemRecebida(destinatario.decifra(mensagem, destinatario.getKey_pessoal()));
    }

    // settando o nonce para a variavel nonce do destinatario
    public void enviaMensagemNonce(byte[] mensagem, Pessoas destinatario) throws BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            UnsupportedEncodingException,
            NoSuchPaddingException,
            InvalidKeyException {
        destinatario.setNonce(
                Integer.parseInt(destinatario.decifra(mensagem, destinatario.getKey_sessao()))
        );
    }

    // gerar nonce
    public void geraNonce(){
        Random r = new Random();
        int max = 999999999;
        int min = 100000000;
        nonce = r.nextInt((max - min) + 1) + min;
    }

    // funcao de autenticação
    public int funcaoAuth(){
        return nonce++;
    }
}
