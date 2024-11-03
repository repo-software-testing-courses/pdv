package net.originmobi.pdv.service;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.GrupoUsuarioRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GrupoUsuarioServiceTest {

    @InjectMocks
    private GrupoUsuarioService grupoUsuarioService;

    @Mock
    private GrupoUsuarioRepository grupousuarios;

    @Mock
    private RedirectAttributes redirectAttributes;

    private Usuario usuario;
    private GrupoUsuario grupoUsuario;

    @Before
    public void setUp() {
        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setUser("usuarioTeste");

        grupoUsuario = new GrupoUsuario();
        grupoUsuario.setCodigo(1L);
        grupoUsuario.setNome("Grupo Teste");
        grupoUsuario.setDescricao("Descrição do Grupo Teste");
    }

    // Teste para o método buscaGrupos(Usuario usuario)
    @Test
    public void testBuscaGrupos() {
        // Dados simulados
        GrupoUsuario grupo1 = new GrupoUsuario();
        grupo1.setCodigo(1L);
        grupo1.setNome("Grupo 1");
        grupo1.setDescricao("Descrição do Grupo 1");

        GrupoUsuario grupo2 = new GrupoUsuario();
        grupo2.setCodigo(2L);
        grupo2.setNome("Grupo 2");
        grupo2.setDescricao("Descrição do Grupo 2");

        List<GrupoUsuario> gruposEsperados = Arrays.asList(grupo1, grupo2);

        // Configuração do mock
        when(grupousuarios.findByUsuarioIn(usuario)).thenReturn(gruposEsperados);

        // Execução do método a ser testado
        List<GrupoUsuario> gruposRetornados = grupoUsuarioService.buscaGrupos(usuario);

        // Verificações
        assertEquals(2, gruposRetornados.size());
        assertEquals("Grupo 1", gruposRetornados.get(0).getNome());
        assertEquals("Grupo 2", gruposRetornados.get(1).getNome());
    }

    // Teste para o método lista()
    @Test
    public void testLista() {
        // Dados simulados
        GrupoUsuario grupo1 = new GrupoUsuario();
        grupo1.setCodigo(1L);
        grupo1.setNome("Grupo 1");

        GrupoUsuario grupo2 = new GrupoUsuario();
        grupo2.setCodigo(2L);
        grupo2.setNome("Grupo 2");

        List<GrupoUsuario> gruposEsperados = Arrays.asList(grupo1, grupo2);

        // Configuração do mock
        when(grupousuarios.findAll()).thenReturn(gruposEsperados);

        // Execução do método
        List<GrupoUsuario> gruposRetornados = grupoUsuarioService.lista();

        // Verificações
        assertEquals(2, gruposRetornados.size());
        verify(grupousuarios, times(1)).findAll();
    }

    // Teste para o método buscaGrupo(Long codigoGru)
    @Test
    public void testBuscaGrupo() {
        Long codigoGru = 1L;

        // Configuração do mock
        when(grupousuarios.findByCodigoIn(codigoGru)).thenReturn(grupoUsuario);

        // Execução do método
        GrupoUsuario grupoRetornado = grupoUsuarioService.buscaGrupo(codigoGru);

        // Verificações
        assertNotNull(grupoRetornado);
        assertEquals("Grupo Teste", grupoRetornado.getNome());
        verify(grupousuarios, times(1)).findByCodigoIn(codigoGru);
    }

    // Teste para o método merge(GrupoUsuario grupoUsuario, RedirectAttributes attributes)
    @Test
    public void testMergeNovoGrupo() {
        // Dados de entrada
        GrupoUsuario novoGrupo = new GrupoUsuario();
        novoGrupo.setNome("Novo Grupo");
        novoGrupo.setDescricao("Descrição do Novo Grupo");

        // Configuração do mock
        when(grupousuarios.save(novoGrupo)).thenReturn(novoGrupo);

        // Execução do método
        grupoUsuarioService.merge(novoGrupo, redirectAttributes);

        // Verificações
        verify(grupousuarios, times(1)).save(novoGrupo);
        verify(redirectAttributes, times(1)).addFlashAttribute("mensagem", "Grupo adicionado com sucesso");
    }

    @Test
    public void testMergeAtualizaGrupo() {
        // Dados de entrada
        GrupoUsuario grupoExistente = new GrupoUsuario();
        grupoExistente.setCodigo(1L);
        grupoExistente.setNome("Grupo Atualizado");
        grupoExistente.setDescricao("Descrição Atualizada");

        // Não é necessário configurar o mock para métodos void

        // Execução do método
        grupoUsuarioService.merge(grupoExistente, redirectAttributes);

        // Verificações
        verify(grupousuarios, times(1)).update(
                grupoExistente.getNome(),
                grupoExistente.getDescricao(),
                grupoExistente.getCodigo()
        );
        verify(redirectAttributes, times(1)).addFlashAttribute("mensagem", "Grupo atualizado com sucesso");
    }

    // Teste para o método remove(Long codigo, RedirectAttributes attributes)
    @Test
    public void testRemoveGrupoSemUsuariosVinculados() {
        Long codigoGrupo = 1L;

        // Configuração do mock
        when(grupousuarios.grupoTemUsuaio(codigoGrupo)).thenReturn(0);
        // Não é necessário configurar o mock para deleteById

        // Execução do método
        String resultado = grupoUsuarioService.remove(codigoGrupo, redirectAttributes);

        // Verificações
        verify(grupousuarios, times(1)).deleteById(codigoGrupo);
        assertEquals("redirect:/grupousuario", resultado);
    }

    @Test
    public void testRemoveGrupoComUsuariosVinculados() {
        Long codigoGrupo = 1L;

        // Configuração do mock
        when(grupousuarios.grupoTemUsuaio(codigoGrupo)).thenReturn(1);

        // Execução do método
        String resultado = grupoUsuarioService.remove(codigoGrupo, redirectAttributes);

        // Verificações
        verify(grupousuarios, never()).deleteById(codigoGrupo);
        verify(redirectAttributes, times(1))
                .addFlashAttribute(eq("mensagemErro"), eq("Este grupo possue usuários vinculados a ele, verifique"));
        assertEquals("redirect:/grupousuario/" + codigoGrupo, resultado);
    }

    // Teste para o método addPermissao(Long codgrupo, Long codpermissao)
    @Test
    public void testAddPermissaoComSucesso() {
        Long codgrupo = 1L;
        Long codpermissao = 2L;

        // Configuração do mock
        when(grupousuarios.grupoTemPermissao(codgrupo, codpermissao)).thenReturn(0);
        // Não é necessário configurar o mock para addPermissao

        // Execução do método
        String resultado = grupoUsuarioService.addPermissao(codgrupo, codpermissao);

        // Verificações
        verify(grupousuarios, times(1)).addPermissao(codgrupo, codpermissao);
        assertEquals("Permissao adicionada com sucesso", resultado);
    }

    @Test(expected = RuntimeException.class)
    public void testAddPermissaoJaExiste() {
        Long codgrupo = 1L;
        Long codpermissao = 2L;

        // Configuração do mock
        when(grupousuarios.grupoTemPermissao(codgrupo, codpermissao)).thenReturn(1);

        // Execução do método que deve lançar exceção
        grupoUsuarioService.addPermissao(codgrupo, codpermissao);

        // Verificações
        verify(grupousuarios, never()).addPermissao(anyLong(), anyLong());
    }

    // Teste para o método removePermissao(Long codigo, Long codgrupo)
    @Test
    public void testRemovePermissaoComSucesso() {
        Long codigoPermissao = 1L;
        Long codgrupo = 1L;

        // Não é necessário configurar o mock para métodos void

        // Execução do método
        String resultado = grupoUsuarioService.removePermissao(codigoPermissao, codgrupo);

        // Verificações
        verify(grupousuarios, times(1)).removePermissao(codigoPermissao, codgrupo);
        assertEquals("Permissão removida com sucesso", resultado);
    }

    @Test(expected = RuntimeException.class)
    public void testRemovePermissaoComErro() {
        Long codigoPermissao = 1L;
        Long codgrupo = 1L;

        // Configuração do mock para lançar exceção
        doThrow(new RuntimeException("Erro ao tentar remover permissão, chame o suporte"))
                .when(grupousuarios).removePermissao(codigoPermissao, codgrupo);

        // Execução do método que deve lançar exceção
        grupoUsuarioService.removePermissao(codigoPermissao, codgrupo);

        // Verificações
        verify(grupousuarios, times(1)).removePermissao(codigoPermissao, codgrupo);
}
}