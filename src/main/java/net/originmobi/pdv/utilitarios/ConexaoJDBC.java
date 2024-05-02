package net.originmobi.pdv.utilitarios;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ConexaoJDBC {
	private DataSource conexao;

	public DataSource abre() {
		
		try {
			conexao = new DriverManagerDataSource("jdbc:mysql://127.0.0.1:3306", "root", "1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conexao;
	}
	
	public void fecha() {
		try {
			conexao.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
