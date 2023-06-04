import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends JFrame {

    private static DatagramSocket socket;
    static CopyOnWriteArrayList<Point> allPoints = new CopyOnWriteArrayList<>();
    static CopyOnWriteArrayList<ClientInfo> allClient = new CopyOnWriteArrayList<>();
    private static byte[] buf = new byte[256];

    public static void runReceiving() throws IOException {
        while (true) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            ClientInfo clientInfo = new ClientInfo(packet.getPort(), packet.getAddress(), 0);
            if (!allClient.contains(clientInfo)) allClient.add(clientInfo);
            ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
            Point point = new Point(buffer.getInt(), buffer.getInt());
            allPoints.add(point);
        }
    }

    public static void runSending() throws IOException {
        while (true) {
            for (int i = 0; i < allClient.size(); i++) {
                if (allClient.get(i).numberOfPocket < allPoints.size()) {
                    for (int j = allClient.get(i).numberOfPocket; j < allPoints.size(); j++) {
                        ByteBuffer buffer = ByteBuffer.allocate(8);
                        buffer.putInt(allPoints.get(j).x);
                        buffer.putInt(allPoints.get(j).y);
                        buf = buffer.array();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, allClient.get(i).inetAddress, allClient.get(i).port);
                        socket.send(packet);
                    }
                    allClient.get(i).numberOfPocket = allPoints.size();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        socket = new DatagramSocket(4445);
        Thread t = new Thread(() -> {
            try {
                runReceiving();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        Thread t1 = new Thread(() -> {
            try {
                runSending();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
    }
}