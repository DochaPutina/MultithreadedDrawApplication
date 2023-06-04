import java.net.InetAddress;
import java.util.Objects;

public class ClientInfo {
    int port;
    InetAddress inetAddress;
    int numberOfPocket;

    public ClientInfo(int port, InetAddress inetAddress, int numberOfPocket) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.numberOfPocket = numberOfPocket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return port == that.port && Objects.equals(inetAddress, that.inetAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, inetAddress);
    }
}
