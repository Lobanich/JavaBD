package com.example.domain;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        System.out.println("Connection established");

    }

    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet resultSet = meta.getTables(null, null, tableName.toUpperCase(), null)) {
            return resultSet.next();
        }
    }

    private boolean isTableEmpty(String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            resultSet.next();
            return resultSet.getInt(1) == 0;
        }
    }

    public void createTables() throws SQLException {
        if (tableExists("Transport") || tableExists("Requests") || tableExists("Delivery")) {
            System.out.println("One or more tables already exist. Creation skipped.");
            return;
        }

        String createTransportTable = "CREATE TABLE IF NOT EXISTS Transport (" +
                "license_plate NVARCHAR(10) PRIMARY KEY, " +
                "brand NVARCHAR(50), " +
                "capacity DECIMAL(10,2))";

        String createRequestsTable = "CREATE TABLE IF NOT EXISTS Requests (" +
                "request_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "cargo_name NVARCHAR(100), " +
                "departure_point NVARCHAR(100), " +
                "destination_point NVARCHAR(100))";

        String createDeliveryTable = "CREATE TABLE IF NOT EXISTS Delivery (" +
                "delivery_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "departure_datetime TIMESTAMP, " +
                "arrival_datetime TIMESTAMP, " +
                "request_id INT, " +
                "vehicle_license_plate NVARCHAR(10), " +
                "distance DECIMAL(10,2), " +
                "FOREIGN KEY (request_id) REFERENCES Requests(request_id), " +
                "FOREIGN KEY (vehicle_license_plate) REFERENCES Transport(license_plate))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTransportTable);
            statement.executeUpdate(createRequestsTable);
            statement.executeUpdate(createDeliveryTable);
            System.out.println("Tables created successfully");
        }
    }

    public void fillTables() throws SQLException {
        if (!tableExists("Transport") || !tableExists("Requests") || !tableExists("Delivery")) {
            System.out.println("One or more tables do not exist. Fill operation skipped.");
            return;
        }

        if (!isTableEmpty("Transport") || !isTableEmpty("Requests") || !isTableEmpty("Delivery")) {
            System.out.println("One or more tables are already filled. Fill operation skipped.");
            return;
        }

        String insertTransport = "INSERT INTO Transport (license_plate, brand, capacity) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertTransport)) {
            statement.setString(1, "A123BC");
            statement.setString(2, "KamAZ");
            statement.setDouble(3, 10000.00);
            statement.executeUpdate();

            statement.setString(1, "B456XT");
            statement.setString(2, "MAN");
            statement.setDouble(3, 15000.00);
            statement.executeUpdate();

            statement.setString(1, "C789YZ");
            statement.setString(2, "Volvo");
            statement.setDouble(3, 12000.00);
            statement.executeUpdate();


            System.out.println("Transport table data inserted");
        }

        String insertRequests = "INSERT INTO Requests (cargo_name, departure_point, destination_point) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertRequests)) {
            statement.setString(1, "Building Materials");
            statement.setString(2, "Moscow");
            statement.setString(3, "Saint Petersburg");
            statement.executeUpdate();

            statement.setString(1, "Food Products");
            statement.setString(2, "Kazan");
            statement.setString(3, "Nizhny Novgorod");
            statement.executeUpdate();

            System.out.println("Requests table data inserted");
        }

        String insertDelivery = "INSERT INTO Delivery (departure_datetime, arrival_datetime, request_id, vehicle_license_plate, distance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertDelivery)) {
            statement.setTimestamp(1, Timestamp.valueOf("2023-01-01 08:00:00"));
            statement.setTimestamp(2, Timestamp.valueOf("2023-01-01 18:00:00"));
            statement.setInt(3, 1);
            statement.setString(4, "A123BC");
            statement.setDouble(5, 700.00);
            statement.executeUpdate();

            statement.setTimestamp(1, Timestamp.valueOf("2023-01-02 08:00:00"));
            statement.setTimestamp(2, Timestamp.valueOf("2023-01-02 18:00:00"));
            statement.setInt(3, 2);
            statement.setString(4, "B456XT");
            statement.setDouble(5, 400.00);
            statement.executeUpdate();

            System.out.println("Delivery table data inserted");
        }
    }

    private void clearTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM Delivery");
            statement.executeUpdate("DELETE FROM Requests");
            statement.executeUpdate("DELETE FROM Transport");
            System.out.println("Tables cleared successfully");
        }
    }

    public void displayTables() throws SQLException {
        if (!tableExists("Transport") || !tableExists("Requests") || !tableExists("Delivery")) {
            System.out.println("One or more tables do not exist. Display operation skipped.");
            return;
        }

        String selectTransport = "SELECT * FROM Transport";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectTransport)) {
            System.out.println("Transport table:");
            while (resultSet.next()) {
                String plate = resultSet.getString("license_plate");
                String brand = resultSet.getString("brand");
                double capacity = resultSet.getDouble("capacity");

                System.out.printf("License Plate: %s, Brand: %s, Capacity: %.2f%n", plate, brand, capacity);
            }
        }

        String selectRequests = "SELECT * FROM Requests";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectRequests)) {
            System.out.println("Requests table:");
            while (resultSet.next()) {
                int id = resultSet.getInt("request_id");
                String cargo = resultSet.getString("cargo_name");
                String from = resultSet.getString("departure_point");
                String to = resultSet.getString("destination_point");

                System.out.printf("Request ID: %d, Cargo Name: %s, Departure Point: %s, Destination Point: %s%n", id, cargo, from, to);
            }
        }

        String selectDelivery = "SELECT * FROM Delivery";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectDelivery)) {
            System.out.println("Delivery table:");
            while (resultSet.next()) {
                int id = resultSet.getInt("delivery_id");
                Timestamp departure = resultSet.getTimestamp("departure_datetime");
                Timestamp arrival = resultSet.getTimestamp("arrival_datetime");
                int requestId = resultSet.getInt("request_id");
                String vehiclePlate = resultSet.getString("vehicle_license_plate");
                double distance = resultSet.getDouble("distance");

                System.out.printf("Delivery ID: %d, Departure: %s, Arrival: %s, Request ID: %d, Vehicle License Plate: %s, Distance: %.2f%n",
                        id, departure, arrival, requestId, vehiclePlate, distance);
            }
        }
    }

    public void dropTables() throws SQLException {
        if (!tableExists("Transport") && !tableExists("Requests") && !tableExists("Delivery")) {
            System.out.println("No tables exist to drop. Drop operation skipped.");
            return;
        }

        // Drop dependent tables first
        String dropDeliveryTable = "DROP TABLE IF EXISTS Delivery";
        String dropRequestsTable = "DROP TABLE IF EXISTS Requests";
        String dropTransportTable = "DROP TABLE IF EXISTS Transport";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropDeliveryTable);
            statement.executeUpdate(dropRequestsTable);
            statement.executeUpdate(dropTransportTable);
            System.out.println("Tables dropped successfully");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed");
        }
    }
}
