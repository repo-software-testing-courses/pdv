package net.originmobi.pdv.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Autowired
	private LoginUserDatailsService loginUserDetailsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/resources/**", "/css/**", "/js/**", "/fonts/**", "/webjars/**").permitAll()

                        .requestMatchers("/").hasRole("ENTRAR_NO_SISTEMA")
                        .requestMatchers("/pessoa").hasRole("VISUALIZAR_PESSOA")
                        .requestMatchers("/pessoa/form").hasRole("EDITAR_PESSOA")
                        .requestMatchers("/fornecedor").hasRole("VISUALIZAR_FORNECEDOR")
                        .requestMatchers("/fornecedor/form").hasRole("EDITAR_FORNECEDOR")
                        .requestMatchers("/grupo").hasRole("VISUALIZAR_GRUPO")
                        .requestMatchers("/grupo/form").hasRole("EDITAR_GRUPO")
                        .requestMatchers("/categoria").hasRole("VISUALIZAR_CATEGORIA")
                        .requestMatchers("/categoria/form").hasRole("EDITAR_CATEGORIA")
                        .requestMatchers("/produto").hasRole("VISUALIZAR_PRODUTO")
                        .requestMatchers("/produto/form").hasRole("EDITAR_PRODUTO")
                        .requestMatchers("/usuario").hasRole("VISUALIZAR_USUARIO")
                        .requestMatchers("/usuario/form").hasRole("EDITAR_USUARIO")

                        .requestMatchers("/venda/status/ABERTA").hasRole("VISUALIZAR_PEDIDO_ABERTO")
                        .requestMatchers("/venda/status/FECHADA").hasRole("VISUALIZAR_PEDIDO_FECHADO")
                        .requestMatchers("/venda/form").hasRole("ABRIR_PEDIDO")
                        .requestMatchers("/venda/fechar/").hasRole("GERAR_VENDA")
                        .requestMatchers("/venda/addproduto/").hasRole("INSERIR_PRODUTO_VENDA")
                        .requestMatchers("/venda/removeproduto/").hasRole("REMOVER_PRODUTO_VENDA")
                        .requestMatchers("/caixa").hasRole("LISTAR_CAIXA")
                        .requestMatchers("/caixa/gerenciar/").hasRole("ACESSAR_CAIXA")
                        .requestMatchers("/caixa/lancamento/suprimento").hasRole("CAIXA_SUPRIMENTO")
                        .requestMatchers("/caixa/lancamento/sangria").hasRole("CAIXA_SANGRIA")
                        .requestMatchers("/transferencia").hasRole("CAIXA_TRANSFERENCIA")
                        .requestMatchers("/caixa/fechar").hasRole("FECHAR_CAIXA")
                        .requestMatchers("/receber").hasRole("VISUALIZAR_RECEBER")
                        .requestMatchers("/recebimento/").hasRole("REALIZAR_RECEBIMENTO")
                        .requestMatchers("/pagar").hasRole("VISUALIZAR_DESPESAS")
                        .requestMatchers("/pagar/quitar").hasRole("PAGAR_DESPESA")
                        .requestMatchers("/pagamentotipo").hasRole("VISUALIZAR_FORMA_PAGAMENTO")
                        .requestMatchers("/pagamentotipo/form").hasRole("CADASTRAR_FORMA_PAGAMENTO")
                        .requestMatchers("/tributacao").hasRole("LISTA_TRIBUTAÇÃO")
                        .requestMatchers("/tributacao").hasRole("CADASTRA_TRIBUTACAO")
                        .requestMatchers("/regras").hasRole("CADATRAR_REGRA_TRIBUTACAO")
                        .requestMatchers("/regras").hasRole("EXCLUIR_REGRA_TRIBUTACAO")
                        .requestMatchers("/regras").hasRole("EDITAR_REGRA_TRIBUTACAO")
                        .requestMatchers("/notafiscal").hasRole("VISUALIZA_NOTAFISCAL")
                        .requestMatchers("/empresa").hasRole("EDITAR_PARAMETROS")
                        .requestMatchers("/banco").hasRole("LISTAR_BANCO")
                        .requestMatchers("/maquinacartao").hasRole("EDITAR_CARTAO")
                        .requestMatchers("/titulos").hasRole("EDITAR_TITULO")
                        .requestMatchers("/cartaolancamentos").hasRole("GERENCIAR_CARTOES")
                        .requestMatchers("/cartaolancamentos").hasRole("ANTECIPAR_CARTAO")
                        .requestMatchers("/cartaolancamentos").hasRole("PROCESSAR_CARTAO")

                        .requestMatchers("/ajustes").hasRole("LISTA_AJUSTE")
                        .requestMatchers("/ajustes/cancelar/").hasRole("FAZ_AJUSTE")
                        .requestMatchers("/ajustes/processar").hasRole("FAZ_AJUSTE")

                        .anyRequest().authenticated())
                .sessionManagement(management -> management.invalidSessionUrl("/login"))
                .formLogin(login -> login.loginPage("/login").permitAll())
                .logout(logout -> logout.permitAll());

        http.sessionManagement(management -> management.maximumSessions(1).expiredUrl("/login?expired"));
        return http.build();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(loginUserDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}

}
