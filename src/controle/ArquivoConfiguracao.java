package controle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArquivoConfiguracao {

    public static void GerarArquivo(String host, String caminho) {

        File file = new File("Preco.txt");

        if (!file.exists()) {

            try {
                file.createNewFile();

                PrintWriter pw = new PrintWriter(file);
                pw.println(host);
                pw.println(caminho);

                pw.flush();
                pw.close();

            } catch (IOException ex) {
                Logger.getLogger(ArquivoConfiguracao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            PrintWriter pw = null;
            try {
                FileWriter fileWriter = new FileWriter(file,false);

                pw = new PrintWriter(fileWriter);
                pw.println(host);
                pw.println(caminho);
                pw.flush();
                pw.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ArquivoConfiguracao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ArquivoConfiguracao.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if(pw != null)
                   pw.close();
            }

        }

    }

}
