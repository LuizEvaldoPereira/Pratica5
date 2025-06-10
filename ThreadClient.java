package cadastroclientv2;

import model.Produto;
import javax.swing.*;
import java.io.ObjectInputStream;
import java.util.List;

public class ThreadClient extends Thread {
    private ObjectInputStream entrada;
    private JTextArea textArea;

    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = entrada.readObject();

                if (obj instanceof String) {
                    String msg = (String) obj;
                    SwingUtilities.invokeLater(() -> {
                        textArea.append(msg + "\n");
                    });
                } else if (obj instanceof List<?>) {
                    List<Produto> produtos = (List<Produto>) obj;

                    SwingUtilities.invokeLater(() -> {
                        textArea.append("Lista de Produtos (Nome - Quantidade):\n");
                        for (Produto p : produtos) {
                            textArea.append(String.format("- %s : %d\n", p.getNome(), p.getQuantidade()));
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

