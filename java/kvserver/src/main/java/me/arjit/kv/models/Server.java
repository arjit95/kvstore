package me.arjit.kv.models;

public class Server {
    private String name;
    private String address;

    private Server(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public static Server create(String name, String address) {
        return new Server(name, address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartitionName() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i))) {
                break;
            }

            builder.append(name.charAt(i));
        }

        return builder.toString();
    }
}
