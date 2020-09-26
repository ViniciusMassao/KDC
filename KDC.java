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

public class KDC {
    private Pessoas [] pessoas_array;
    private String [] k_array;
    private String k_sessao;
    private static String ALGORITMO = "AES";
    private String mensagem;

    public KDC(String[] k_array, Pessoas [] pessoas, String mensagem) {
        this.k_array = k_array;
        pessoas_array = pessoas;
        this.mensagem = mensagem;
    }

    public Pessoas[] getPessoas_array() {
        return pessoas_array;
    }

    public void setPessoas_array(Pessoas[] pessoas_array) {
        this.pessoas_array = pessoas_array;
    }

    public String[] getK_array() {
        return k_array;
    }

    public void setK_array(String[] k_array) {
        this.k_array = k_array;
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

    // função que gera chave de sessão
    public String fazChave(){
        Random r = new Random();
        String key = "";
        int max = 9;
        int min = 0;
        for(int i = 0; i < 16; i++)
            key += String.valueOf(r.nextInt((max - min) + 1) + min);
        return key;
    }

    public void run() throws BadPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException,
            UnsupportedEncodingException,
            NoSuchPaddingException,
            InvalidKeyException {
         Pessoas pessoa1 = pessoas_array[0];

         Pessoas pessoa2 = pessoas_array[1];

         // pessoa1 enviando identificador para KDC
         byte[] pessoa1_id = pessoa1.cifra(pessoa1.getId(), pessoa1.getKey_pessoal());

         // identificando se pessoas1 tem a mesma id que foi enviada
         // caso nao significa que pessoa1 nao eh a pessoa que diz ser
         if(decifra(pessoa1_id, k_array[0]).equals(pessoa1.getId())){
             // pessoa1 enviando para quem deseja mandar mensagem
             byte[] pessoa1_destinatrio = pessoa1.cifra(pessoa2.getId(), pessoa1.getKey_pessoal());
             String pessoa_id = decifra(pessoa1_destinatrio, k_array[0]);

             // criando chave de sessao
             k_sessao = fazChave();

             // KDC envia chave de sessao para pessoa1
             // enviando para pessoa1 a chave de sessao ela vai decifra no metodo setKey_sessao
             // k_sessao da pessoa1
             byte[] k_sessao_pessoa1 = cifra(k_sessao, k_array[0]);
             pessoa1.setKey_sessao(k_sessao_pessoa1);

             // k_sessao da pessoa2 -> chave vai enviar ser enviada para pessoa1
             // que vai reenviar para pessoa2, mensagem esta cifrada com a key de pessoa2
             byte[] k_sessao_pessoa2 = cifra(k_sessao, k_array[1]);
             pessoa1.enviaMensagemKSessao(k_sessao_pessoa2, pessoas_array[1]);

             // pessoa2 settando a key da sessao
             pessoa2.setKey_sessao_Mensagem();

             // gerando nonce
             pessoa2.geraNonce();

             // enviando nonce para pessoa1
             pessoa2.enviaMensagemNonce(
                     pessoa2.cifra(String.valueOf(pessoa2.getNonce()), pessoa2.getKey_sessao()),
                     pessoa1
             );

            // pessoa1 realizando autenticacao
            int new_nonce_pessoa1 = pessoa1.funcaoAuth();
            // pessoa2 jah pega o valor da funcao de autenticacao
            int new_nonce_pessoa2 = pessoa2.funcaoAuth();

            // pessoa1 enviando mensagem com o valor de saida da funcao de autenticao com o valor de
            // entrada nonce
            pessoa1.enviaMensagemNonce(
                    pessoa1.cifra(String.valueOf(new_nonce_pessoa1), pessoa1.getKey_sessao()),
                    pessoa2
            );

             // comparando se o valor de saida da funcao de autenticao da pessoa2 eh
             // o mesmo que a mensagem recebida da pessoa1
             // caso seja a pessoa1 pode enviar mensagem para pessoa2
             if(String.valueOf(new_nonce_pessoa2).equals(String.valueOf(pessoa2.getNonce())))
                 pessoa1.ImprimeMensagem(mensagem);
             // se nao for igual o valor da funcao de autenticacao entao a pessoa1 nao eh quem ela diz ser
             else ImprimeMensagem("Erro de autenticação");

         }else ImprimeMensagem("Permissão negada");

    }

    public void ImprimeMensagem(String mensagem){
        System.out.println("KDE: "+mensagem);
    }

}
