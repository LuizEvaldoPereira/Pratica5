package cadastroclient;

import model.Produto;
import java.net.Socket;
import java.io.*;
import java.util.List;

public class CadastroClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4321);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            // Envia login e senha (exemplo: op1/op1)
            out.writeObject("op1");
            out.writeObject("op1");
            out.flush();
            
            // Envia comando L para listar produtos
            out.writeObject("L");
            out.flush();
            
            // Recebe lista de produtos
            List<Produto> produtos = (List<Produto>) in.readObject();
            System.out.println("Produtos recebidos:");
            for (Produto p : produtos) {
                System.out.println("- " + p.getNome());
            }
            
            // Fecha conexão automaticamente usando try-with-resources
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
