package cadastroclientv2;

import model.Produto;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class CadastroClientV2 {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4321);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {

            // Login e senha (exemplo op1/op1)
            out.writeObject("op1");
            out.writeObject("op1");
            out.flush();

            // Cria GUI para saída de mensagens
            SaidaFrame saida = new SaidaFrame();
            saida.setVisible(true);

            // Inicia thread assíncrona que lê servidor continuamente
            ThreadClient tClient = new ThreadClient(in, saida.texto);
            tClient.start();

            while (true) {
                System.out.println("\nMenu:\nL - Listar Produtos\nE - Entrada de Produto\nS - Saída de Produto\nX - Sair");
                System.out.print("Comando: ");
                String cmd = teclado.readLine();

                if (cmd == null) continue;
                cmd = cmd.trim().toUpperCase();

                if (cmd.equals("X")) {
                    out.writeObject("X");
                    out.flush();
                    break;
                }

                if (cmd.equals("L")) {
                    out.writeObject("L");
                    out.flush();

                } else if (cmd.equals("E") || cmd.equals("S")) {
                    out.writeObject(cmd);
                    out.flush();

                    System.out.print("Id da Pessoa: ");
                    int idPessoa = Integer.parseInt(teclado.readLine());
                    out.writeObject(idPessoa);
                    out.flush();

                    System.out.print("Id do Produto: ");
                    int idProduto = Integer.parseInt(teclado.readLine());
                    out.writeObject(idProduto);
                    out.flush();

                    System.out.print("Quantidade: ");
                    int qtd = Integer.parseInt(teclado.readLine());
                    out.writeObject(qtd);
                    out.flush();

                    System.out.print("Valor Unitário: ");
                    double valU = Double.parseDouble(teclado.readLine());
                    out.writeObject(valU);
                    out.flush();

                } else {
                    System.out.println("Comando inválido!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
