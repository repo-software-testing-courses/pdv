package net.originmobi.pdv.utilitarios;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConexaoJDBCTest {

    private ConexaoJDBC conexaoJDBC;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    public void IniciarConexão() throws Exception {
        conexaoJDBC = new ConexaoJDBC();
        dataSource = conexaoJDBC.abre();
        connection = dataSource.getConnection();
    }

    @AfterEach
    public void FecharConexão() throws Exception {
        conexaoJDBC.fecha();
    }
    @Test
    public void testAbrirEFecharConexao() throws SQLException {
        assertNotNull(dataSource);
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

    @Test
    public void testConexaoAtiva() throws SQLException {
        assertTrue(connection.isValid(1)); // Verifica se a conexão está ativa
    }
}