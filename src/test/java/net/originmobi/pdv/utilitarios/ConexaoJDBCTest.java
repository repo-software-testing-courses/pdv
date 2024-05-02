package net.originmobi.pdv.utilitarios;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConexaoJDBCTest {

    private ConexaoJDBC conexaoJDBC;
    private DataSource dataSource;
    private Connection connection;

    @Before
    public void IniciarConexão() throws Exception {
        conexaoJDBC = new ConexaoJDBC();
        dataSource = conexaoJDBC.abre();
        connection = dataSource.getConnection();
    }

    @After
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