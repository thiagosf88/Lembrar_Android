package br.thg.lmb.dominio;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import br.thg.lmb.Lembrar;
import br.thg.lmb.dao.TarefasDbAdapter;
import br.thg.lmb.util.TrataData;
import br.thg.lmb.util.TrataTag;

//Essa classe será utilizada para realizar atualizçao, deleçao entre outras acoes
public class Operacao {
	
	
	
	public static final int ADIAR = 1;
	public static final int ADICIONAR = ADIAR + 1;
	public static final int ATUALIZAR = ADIAR + 2;
	public static final int COMPLETAR = ADIAR + 3;
	public static final int EXCLUIR = ADIAR + 4;
	
	private int tipo;
	private Long idTarefa;
	private Long idSeriesTarefa;
	private String listaTarefa;
	
	
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public Long getIdTarefa() {
		return idTarefa;
	}
	public void setIdTarefa(Long idTarefa) {
		this.idTarefa = idTarefa;
	}
	public Long getIdSeriesTarefa() {
		return idSeriesTarefa;
	}
	public void setIdSeriesTarefa(Long idSeriesTarefa) {
		this.idSeriesTarefa = idSeriesTarefa;
	}

	public String getListaTarefa() {
		return listaTarefa;
	}
	public void setListaTarefa(String listaTarefa) {
		this.listaTarefa = listaTarefa;
	}
	@Override
	public String toString() {
		
		return "id:" + idTarefa + " idSeries:" + idSeriesTarefa + " tipo:" + tipo + " lista:" + listaTarefa;
	}
	
	public static boolean executaOperacoesAgendadas(TarefasDbAdapter bd, String auth_token, float diferencaFuso){
		
		if(!bd.isAberto()){
			bd.open();
		}
		Cursor cTmp = bd.getTodasOperacoes();
		ArrayList<Operacao> agendadas = TarefasDbAdapter.deCursorParaArrayListOperacoes(cTmp);
		cTmp.close();
		Tarefa tmp;
		Lembrar lembrar = new Lembrar();
		if(agendadas == null || agendadas.size() == 0){
			bd.deletaTodasTarefasTemporarias();
			bd.deletaTodasTagsTemporarias();
			return false;
		}
		for(Operacao op : agendadas){
			
			switch(op.getTipo()){
			case ADIAR:
				if(lembrar.adiarTarefa(auth_token, op.getIdTarefa().toString(), op.getIdSeriesTarefa().toString(), op.getListaTarefa()))
				bd.deletaOperacao(op.getIdTarefa(), op.getIdSeriesTarefa(), ADIAR);
			break;
			case ADICIONAR:
				cTmp = bd.getTarefaTemporariaPelosIds(op.getIdTarefa(), op.getIdSeriesTarefa());
				if(cTmp == null || cTmp.isClosed() || cTmp.getCount() == 0){
					cTmp.close();
					return false;
				}
				tmp = TarefasDbAdapter.deCursorParaArrayListTarefas
				(cTmp).get(0);				
				cTmp.close();
				cTmp = bd.getTagsPorTarefaTemporaria(op.getIdTarefa(), op.getIdSeriesTarefa());
				tmp.setTags(TarefasDbAdapter.deCursorParaArrayListTags(cTmp));
				
				if(lembrar.adicionaTarefaComSmart(auth_token, tmp.getName(),
						op.getListaTarefa(), TrataTag.deArrayListParaStringRTM(tmp.getTags()),
						tmp.getPrioridade(), tmp.getRepeticao(),
						TrataData.trataGregorian(tmp.getDataPrevista()), tmp.getHoraPrevista(), tmp.getUrl())
						!= null){
					bd.deletaOperacao(op.getIdTarefa(), op.getIdSeriesTarefa(), ADICIONAR);
				}					
					
					cTmp.close();
					
				
				if(!cTmp.isClosed())
				cTmp.close();
				
				break;
			case ATUALIZAR:
				cTmp = bd.getTarefaTemporariaPelosIds(op.getIdTarefa(), op.getIdSeriesTarefa());
				tmp = TarefasDbAdapter.deCursorParaArrayListTarefas
				(cTmp).get(0);
				cTmp.close();
				lembrar.alterarTexto(auth_token,op.getIdTarefa().toString(),
						op.getIdSeriesTarefa().toString(), op.getListaTarefa(), tmp.getName()
						);
				if(tmp.getDataPrevista() != null)
				lembrar.inserirLimite(auth_token,op.getIdTarefa().toString(),
						op.getIdSeriesTarefa().toString(), op.getListaTarefa(), 
						TrataData.trataGregorian(tmp.getDataPrevista()),
						tmp.getHoraPrevista(), diferencaFuso
						);
				
				if(tmp.getPrioridade() != null
						&& !tmp.getPrioridade().trim().equalsIgnoreCase(""))
				lembrar.inserirPrioridade(auth_token,op.getIdTarefa().toString(),
						op.getIdSeriesTarefa().toString(), op.getListaTarefa(), tmp.getPrioridade()
						);
				
				if(tmp.getRepeticao() != null && !tmp.getRepeticao().trim().equals(""))
					lembrar.adicionarRepeticao(auth_token,op.getIdTarefa().toString(),
							op.getIdSeriesTarefa().toString(), op.getListaTarefa(), tmp.getRepeticao())
							;
				cTmp = bd.getTagsPorTarefaTemporaria(op.getIdTarefa(), op.getIdSeriesTarefa());
				tmp.setTags(TarefasDbAdapter.deCursorParaArrayListTags(cTmp));
				if(tmp.getTags() != null && tmp.getTags().size()>0)
				lembrar.inserirTags(auth_token,op.getIdTarefa().toString(),
						op.getIdSeriesTarefa().toString(), op.getListaTarefa(), TrataTag.deArrayListParaStringRTM(tmp.getTags())
						);
				
				bd.deletaOperacao(op.getIdTarefa(), op.getIdSeriesTarefa(), ATUALIZAR);
				cTmp.close();
				break;
				
			case COMPLETAR:
				if(lembrar.completarTarefa(auth_token,op.getIdTarefa().toString(),
						op.getIdSeriesTarefa().toString(), op.getListaTarefa()))
				bd.deletaOperacao(op.getIdTarefa(), op.getIdSeriesTarefa(), COMPLETAR);
				break;
			case EXCLUIR:
				if(lembrar.deletarTarefa(auth_token,op.getIdTarefa().toString(),
						op.getIdSeriesTarefa().toString(), op.getListaTarefa()))
				bd.deletaOperacao(op.getIdTarefa(), op.getIdSeriesTarefa(), EXCLUIR);
				break;
			}
		
		
		}
		bd.close();
		return false;
	}

}
