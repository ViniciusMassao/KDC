import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MainKDC {

    public static void main(String[] args) throws BadPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            UnsupportedEncodingException,
            NoSuchPaddingException,
            InvalidKeyException {
        String k_pessoa1 = "bolabolabolabola";
        String k_pessoa2 = "gatogatogatogato";
        Pessoas pessoa1 = new Pessoas("Bob", k_pessoa1);
        Pessoas pessoa2 = new Pessoas("Ana", k_pessoa2);
        String mensagem = "Bom dia Ana, aqui é o Bob. :)";


        String[] k_array = {k_pessoa1, k_pessoa2};
        Pessoas[] pessoas = {pessoa1, pessoa2};

        KDC server = new KDC(k_array, pessoas, mensagem);
        server.run();
    }
}
