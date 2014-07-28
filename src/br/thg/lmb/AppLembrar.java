package br.thg.lmb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.thg.lmb.dao.TarefasDbAdapter;
import br.thg.lmb.dominio.Lista;
import br.thg.lmb.dominio.Nota;
import br.thg.lmb.dominio.Operacao;
import br.thg.lmb.dominio.Tag;
import br.thg.lmb.dominio.Tarefa;
import br.thg.lmb.util.ComparaTarefas;
import br.thg.lmb.util.ListaAdapter;
import br.thg.lmb.util.TarefaAdapter;
import br.thg.lmb.util.TrataData;
import br.thg.lmb.util.TrataTag;
import br.thg.lmb.util.WebViewActivity;
import br.thg.rlmb.R;

public class AppLembrar extends ListActivity {

	private static final int CREATE_TASK_OPERATION = 0;
	private static final int EDIT_TASK_OPERATION = CREATE_TASK_OPERATION + 1;	
	private static final int CONFIGURE_OPERATION = EDIT_TASK_OPERATION + 1;
	private static final int GET_AUTHENTICATION_OPERATION = CONFIGURE_OPERATION + 1;
	
	private static final int DELETE_ID = Menu.FIRST;
	private static final int ADIAR_ID = Menu.FIRST + 1;
	private static final int COMPLETAR_ID = Menu.FIRST + 2;
	
	//CORES DE LAYOUT
	private static final int APARENCIA_BARRA_AZUL_ICONE_LARANJA = 0;
	private static final int APARENCIA_BARRA_AZUL_ICONE_COLORIDO = APARENCIA_BARRA_AZUL_ICONE_LARANJA + 1;
	private static final int APARENCIA_BARRA_LARANJA_ICONE_COLORIDO = APARENCIA_BARRA_AZUL_ICONE_LARANJA + 2;
	@SuppressWarnings("unused")
	private static final int APARENCIA_BARRA_LARANJA_ICONE_AZUL = APARENCIA_BARRA_AZUL_ICONE_LARANJA + 3;
	
	private int aparenciaEscolhida = APARENCIA_BARRA_AZUL_ICONE_LARANJA;
	
	private Long TodasTarefas_Id;
	private Long IdTarefasHoje = new Long(Integer.MIN_VALUE);
	public static final Long IdTarefasVencidas = new Long(Integer.MIN_VALUE + 1);
	private float diferencaFuso = 0;
	private String auth_token = "";
	private boolean sincronizarInicio = true;
	private Tarefa tarefaSelecionada = new Tarefa();
	Lembrar lembrar = new Lembrar();
	List<Tag> todasTags;
	List<Lista> todasListas;
	List<Tarefa> todasTarefas;
	private TarefasDbAdapter mDbHelper;
	private Cursor mTarefasCursor;
	LinearLayout barra;
	TextView listaVazia;
	TextView texto_msg;
	Button bt_adicionar;
	Button bt_redirecionar;
	Button bt_iniciar;
	Spinner sp_listas;
	ImageView iconeLembrar;
	private static Lista listaSelecionada;
	boolean temInternet = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDbHelper = new TarefasDbAdapter(this);

		if (!mDbHelper.isAberto())
			mDbHelper.open();
		Bundle dados = mDbHelper.getConfiguracoes();
		if(dados != null){
		auth_token = dados.getString(TarefasDbAdapter.CHAVE_AUTH);
		aparenciaEscolhida = dados.getInt(TarefasDbAdapter.CHAVE_CAPA);
		sincronizarInicio = dados.getBoolean(TarefasDbAdapter.CHAVE_ATUALIZA_INICIO);
		}
		mDbHelper.close();
		if (auth_token == null || auth_token.trim().equals(""))
			direcionaParaAutorizacao();
		else
			inicio();

	}

	private void direcionaParaAutorizacao() {

		setContentView(R.layout.tela_redirecionamento);

		bt_redirecionar = (Button) findViewById(R.id.bt_redirecionar);
		
		bt_redirecionar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				/*Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.addCategory(Intent.CATEGORY_BROWSABLE);
				i.setData(Uri.parse(lembrar.getUrlToAuthentication()));
				System.err.println(lembrar.getUrlToAuthentication());
				startActivity(i);*/
				Intent webSite = new Intent(AppLembrar.this, WebViewActivity.class);
				webSite.setAction(Intent.ACTION_VIEW);
				webSite.addCategory(Intent.CATEGORY_BROWSABLE);
				webSite.putExtra(WebViewActivity.URL, lembrar.getUrlToAuthentication());
				startActivityForResult(webSite, GET_AUTHENTICATION_OPERATION);
				//preAbertura();

			}
		});

	}

	private void preAbertura() {

		setContentView(R.layout.tela_pre_abertura);

		bt_redirecionar = (Button) findViewById(R.id.bt_redirecionar);
		bt_iniciar = (Button) findViewById(R.id.bt_iniciar);

		bt_redirecionar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				/*Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.addCategory(Intent.CATEGORY_BROWSABLE);
				i.setData(Uri.parse(lembrar.getUrlToAuthentication()));
				System.err.println(lembrar.getUrlToAuthentication());
				startActivity(i);*/
				
				Intent webSite = new Intent(AppLembrar.this, WebViewActivity.class);
				webSite.setAction(Intent.ACTION_VIEW);
				webSite.addCategory(Intent.CATEGORY_BROWSABLE);
				webSite.putExtra(WebViewActivity.URL, lembrar.getUrlToAuthentication());
				
				startActivityForResult(webSite, GET_AUTHENTICATION_OPERATION);
				//preAbertura();

			}
		});
		bt_iniciar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				auth_token = lembrar.getToken();
				if (auth_token != null && !auth_token.trim().equals("")) {
					if (!mDbHelper.isAberto())
						mDbHelper.open();
					mDbHelper.insertAuthToken(auth_token);
					mDbHelper.close();
					inicio();

				} else{
					
					AlertDialog.Builder dlg = new AlertDialog.Builder(AppLembrar.this);
					
					dlg.setTitle(getResources().getText(R.string.msg_atencao));
					dlg.setMessage(getResources().getText(R.string.msg_autorizacao));
					dlg.setNeutralButton("Ok", null);
					
					dlg.show();
					
				}
					

			}
		});
	}

	private void inicio() {
		
		
		
		setContentView(R.layout.tarefa_lista);
		bt_adicionar = (Button) findViewById(R.id.bt_nova_tarefa);
		listaVazia = (TextView) findViewById(android.R.id.empty);
		sp_listas = (Spinner) findViewById(R.id.sp_listas_todas);
		texto_msg = (TextView) findViewById(R.id.texto_msg);
		barra = (LinearLayout) findViewById(R.id.linearLayout1);
		iconeLembrar = (ImageView) findViewById(R.id.imageView1);
		
		mudaCapa(aparenciaEscolhida);
//AQUI
		if (temInternet() && sincronizarInicio) {

			temInternet = true;			
			
			new CarregarBack().execute(null, null, null);
		} else {
			Date cFuso = new Date();
			
			diferencaFuso = (float)cFuso.getTimezoneOffset()/ (float)-60;
			Log.v("cf",String.valueOf(diferencaFuso));
			Toast
					.makeText(
							this,
							getResources().getString(R.string.msg_sem_rede2),
							Toast.LENGTH_LONG).show();
			temInternet = false;
			
			buscaTodasAsListas(listaSelecionada);
			selecionaTarefasPelaLista(null);
		}

		bt_adicionar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(AppLembrar.this, TarefaCriar.class);

				startActivityForResult(i, CREATE_TASK_OPERATION);
			}
		});

		sp_listas
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@SuppressWarnings("unchecked")
					public void onItemSelected(AdapterView parentView,
							View childView, int position, long id) {
						listaSelecionada = todasListas.get(position);
						selecionaTarefasPelaLista(listaSelecionada);
					}

					@SuppressWarnings("unchecked")
					public void onNothingSelected(AdapterView parentView) {

					}
				});

		registerForContextMenu(getListView());
	}

	public void mudaCapa(int aparenciaEscolhida) {
		switch (aparenciaEscolhida) {
		case APARENCIA_BARRA_AZUL_ICONE_LARANJA:
			barra.setBackgroundResource(R.color.azulclaro);
			bt_adicionar.setBackgroundResource(R.color.azulclaro);
			sp_listas.setBackgroundResource(R.color.azulclaro);
			iconeLembrar.setImageResource(R.drawable.icon_b_l_barra);
			break;
		case APARENCIA_BARRA_AZUL_ICONE_COLORIDO:
			barra.setBackgroundResource(R.color.azulclaro);
			bt_adicionar.setBackgroundResource(R.color.azulclaro);
			sp_listas.setBackgroundResource(R.color.azulclaro);
			iconeLembrar.setImageResource(R.drawable.icon);
			break;
			
		case APARENCIA_BARRA_LARANJA_ICONE_COLORIDO:
			barra.setBackgroundResource(R.color.laranja);
			bt_adicionar.setBackgroundResource(R.color.laranja);
			sp_listas.setBackgroundResource(R.color.laranja);
			iconeLembrar.setImageResource(R.drawable.icon);
			break;

		default:
			barra.setBackgroundResource(R.color.laranja);
			bt_adicionar.setBackgroundResource(R.color.laranja);
			sp_listas.setBackgroundResource(R.color.laranja);
			iconeLembrar.setImageResource(R.drawable.icon_b_a_barra);
			break;
		}
		
	}

	private void BuscaTarefasNaInternet() {
		if (temInternet) {
			todasListas = lembrar.getListas(auth_token);
			todasTarefas = lembrar.todasAsTarefas(auth_token, diferencaFuso);
			
		}
	}

	private void insereNovasTarefasNoBanco() {

		if (todasTarefas != null) {
			if (!mDbHelper.isAberto()) {
				mDbHelper.open();
			}
			mDbHelper.deletaTodasTarefas();
			mDbHelper.deletaTodasTags();
			mDbHelper.deletaTodasNotas();
			
			for (Tarefa t : todasTarefas) {

				mDbHelper.adicionaTarefa(t.getId(), t.getName(), t
						.getIdSeries(), t.getLista(), t.getPrioridade(),
						TrataData.trataGregorian(t.getDataPrevista()), t.getHoraPrevista(),
						t.getRepeticao(), t.getUrl());
				
				for (String tg : t.getTags()) {

					mDbHelper.adicionaTags(t.getId(), tg, t.getIdSeries());
				}
				
				for (Nota nta : t.getNotas()){
					
					mDbHelper.adicionaNotas(t.getId(), nta.getId(), nta.getTitulo(), nta.getNota());
				}

			}

		}

	}

	public void buscaTodasAsListas(Lista padrao) {

		if (!mDbHelper.isAberto()) {
			mDbHelper.open();
		}
		
		
		if (todasListas == null) {
			Cursor tmpLista = mDbHelper.getTodasListas();
			todasListas = TarefasDbAdapter
					.deCursorParaArrayListListas(tmpLista);
			tmpLista.close();

			for (Lista l : todasListas) {
				
				if (l.getNome().equalsIgnoreCase("Todas as tarefas")) {
					TodasTarefas_Id = l.getId();
				}
			}
		} else {

			Lista lst = new Lista(IdTarefasHoje, getResources().getString(R.string.str_hoje));
			if (!todasListas.contains(lst))
				todasListas.add(0, lst);
			lst = new Lista(IdTarefasVencidas, getResources().getString(R.string.str_vencidas));
			if (!todasListas.contains(lst))
				todasListas.add(1, lst);

		
			
			mDbHelper.deletaTodasListas();
			for (Lista l : todasListas) {
				if (l.getNome().equalsIgnoreCase("Inbox"))
					l.setNome(getResources().getString(R.string.str_entrada));
				else if (l.getNome().equalsIgnoreCase("Sent"))
					l.setNome(getResources().getString(R.string.str_enviadas));
				else if (l.getNome().equalsIgnoreCase("Todas as tarefas")) {
					TodasTarefas_Id = l.getId();
				}
				mDbHelper.adicionaListas(l.getId(), l.getNome());
				
				
				
			}
		}
	

		
		sp_listas.setAdapter(new ListaAdapter(this, todasListas));
		
		if(padrao == null)
			sp_listas.setSelection(0);

		mDbHelper.close();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, TarefaEdit.class);
		i.putExtra(TarefasDbAdapter.CHAVE_ID, todasTarefas.get(position)
				.getId());
		i.putExtra(TarefasDbAdapter.CHAVE_IDSERIES, todasTarefas.get(position)
				.getIdSeries());
		i.putExtra(TarefasDbAdapter.CHAVE_NAME, todasTarefas.get(position)
				.getName());
		i.putExtra(TarefasDbAdapter.CHAVE_LISTA, todasTarefas.get(position)
				.getLista());
		i.putStringArrayListExtra(TarefasDbAdapter.CHAVE_TAGS,
				(ArrayList<String>) todasTarefas.get(position).getTags());
		i.putExtra(TarefasDbAdapter.CHAVE_DATAPREVISTA, TrataData
				.trataGregorian(todasTarefas.get(position).getDataPrevista()));
		i.putExtra(TarefasDbAdapter.CHAVE_PRIO, todasTarefas.get(position)
				.getPrioridade());
		i.putExtra(TarefasDbAdapter.CHAVE_HORAPREVISTA, todasTarefas.get(position)
				.getHoraPrevista());
		
		i.putExtra(TarefasDbAdapter.CHAVE_REPETICAO, todasTarefas.get(position)
				.getRepeticao());
		i.putExtra(TarefasDbAdapter.CHAVE_URL, todasTarefas.get(position)
				.getUrl());
		
		tarefaSelecionada = todasTarefas.get(position);
		startActivityForResult(i, EDIT_TASK_OPERATION);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (intent == null)
			return;

		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		
		/*if(requestCode == OPERACAO_CONFIGURAR){
			if(extras != null && extras.containsKey("FINALIZAR") && extras.getInt("FINALIZAR") == -7)
				finish();
			if(extras != null && extras.getInt(TarefasDbAdapter.CHAVE_CAPA) != aparenciaEscolhida){
				aparenciaEscolhida = extras.getInt(TarefasDbAdapter.CHAVE_CAPA);
			mudaCapa(aparenciaEscolhida);
			}
		}
		else{
		extras.putInt("tipoOperacao", requestCode);
		new RealizaOperacaoOnline().execute(extras, null, null);
		}*/
		
		switch (requestCode) {
		case CONFIGURE_OPERATION:
			if(extras != null && extras.containsKey("FINALIZAR") && extras.getInt("FINALIZAR") == -7)
				finish();
			if(extras != null && extras.getInt(TarefasDbAdapter.CHAVE_CAPA) != aparenciaEscolhida){
				aparenciaEscolhida = extras.getInt(TarefasDbAdapter.CHAVE_CAPA);
			mudaCapa(aparenciaEscolhida);
			}
			break;
		case GET_AUTHENTICATION_OPERATION:
			if(resultCode == AppLembrar.RESULT_OK){
				preAbertura();
			}
			break;
		default:
			extras.putInt("tipoOperacao", requestCode);
			new RealizaOperacaoOnline().execute(extras, null, null);
			break;
		}

	}

	public class CarregarBack extends AsyncTask<Void, String, Void> {
		private ProgressDialog progressDialog;

		protected void onPreExecute() {

			progressDialog = new ProgressDialog(AppLembrar.this);

			progressDialog = ProgressDialog.show(AppLembrar.this, "",
					getResources().getString(R.string.msg_conectando), true);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			diferencaFuso = lembrar.obtemDiferencaFuso(auth_token);
			publishProgress(getResources().getString(R.string.msg_sincronizando));
			Operacao.executaOperacoesAgendadas(mDbHelper, auth_token, diferencaFuso);
			publishProgress(getResources().getString(R.string.msg_buscando));
			BuscaTarefasNaInternet();
			publishProgress(getResources().getString(R.string.msg_armazenando));
			insereNovasTarefasNoBanco();

			return null;
		}

		protected void onProgressUpdate(String... mensagem) {
			progressDialog.setMessage(mensagem[0]);
		}

		@Override
		protected void onPostExecute(Void result) {

			selecionaTarefasPelaLista(listaSelecionada);
			buscaTodasAsListas(listaSelecionada);
			progressDialog.dismiss();
			mDbHelper.close();

		}

	}

	public class AtualizarBack extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		protected void onPreExecute() {

			progressDialog = new ProgressDialog(AppLembrar.this);

			progressDialog = ProgressDialog.show(AppLembrar.this, "",
					getResources().getString(R.string.msg_carregando), true);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			BuscaTarefasNaInternet();
			insereNovasTarefasNoBanco();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			selecionaTarefasPelaLista(listaSelecionada);
			buscaTodasAsListas(listaSelecionada);
			progressDialog.dismiss();
			mDbHelper.close();

		}

	}

	public class RealizaOperacaoContextoOnline extends
			AsyncTask<Bundle, String, Void> {
		private ProgressDialog progressDialog;

		protected void onPreExecute() {

			progressDialog = new ProgressDialog(AppLembrar.this);

			progressDialog = ProgressDialog.show(AppLembrar.this, "",
					getResources().getString(R.string.msg_verificando), true);
		}

		@Override
		protected Void doInBackground(Bundle... arg0) {

			String nome, lista, limite, prioridade, repeticao, hora, url;
			Long idSeries, id;
			Bundle extras = arg0[0];
			nome = extras.getString(TarefasDbAdapter.CHAVE_NAME);
			lista = extras.getString(TarefasDbAdapter.CHAVE_LISTA);
			limite = extras.getString(TarefasDbAdapter.CHAVE_DATAPREVISTA);
			prioridade = extras.getString(TarefasDbAdapter.CHAVE_PRIO);
			repeticao = extras.getString(TarefasDbAdapter.CHAVE_REPETICAO);
			hora = extras.getString(TarefasDbAdapter.CHAVE_HORAPREVISTA);
			idSeries = extras.getLong(TarefasDbAdapter.CHAVE_IDSERIES);
			id = extras.getLong(TarefasDbAdapter.CHAVE_ID);
			url = extras.getString(TarefasDbAdapter.CHAVE_URL);
			temInternet();
			switch (extras.getInt("tipoOperacao")) {
			case DELETE_ID:
				publishProgress(getResources().getString(R.string.msg_apagando));
				if (temInternet
						&& lembrar.deletarTarefa(auth_token, id.toString(),
								idSeries.toString(), lista)) {

					publishProgress(getResources().getString(R.string.msg_apagando2));

				} else {
					if (!mDbHelper.isAberto())
						mDbHelper.open();
					mDbHelper.adicionaOperacao(id, Operacao.EXCLUIR, idSeries,
							lista);
					mDbHelper.close();
					publishProgress(getResources().getString(R.string.msg_apagando3));
				}
				if (!mDbHelper.isAberto())
					mDbHelper.open();
				mDbHelper.deletaTarefa(id, idSeries);
				mDbHelper.close();
				return null;
			case ADIAR_ID:
				publishProgress(getResources().getString(R.string.msg_adiando));
				if (temInternet
						&& lembrar.adiarTarefa(auth_token, id.toString(),
								idSeries.toString(), lista)) {

					publishProgress(getResources().getString(R.string.msg_adiando2));

				} else {
					if (!mDbHelper.isAberto())
						mDbHelper.open();

					if (mDbHelper.adicionaOperacao(id, Operacao.ADIAR,
							idSeries, lista) != -1) {

						publishProgress(getResources().getString(R.string.msg_adiando3));
					}
				}
				if (!mDbHelper.isAberto())
					mDbHelper.open();
				GregorianCalendar ontem = (GregorianCalendar) Calendar.getInstance();
				ontem.add(GregorianCalendar.DAY_OF_MONTH, -1);
				GregorianCalendar lt = ontem;				 
				GregorianCalendar dataTarefa = TrataData.trata(limite);
				
				if(dataTarefa != null && (dataTarefa.after(ontem)))
					lt = dataTarefa;

				lt.add(Calendar.DAY_OF_MONTH, 1);				
				
				mDbHelper.atualizarTarefa(idSeries, id, nome, TrataData
						.trataGregorian(lt), lista, prioridade, hora, repeticao, url );

				mDbHelper.close();
				return null;

			case COMPLETAR_ID:
				publishProgress(getResources().getString(R.string.msg_completando));
				if (temInternet
						&& lembrar.completarTarefa(auth_token, id.toString(),
								idSeries.toString(), lista)) {
					if (!mDbHelper.isAberto())
						mDbHelper.open();
					mDbHelper.deletaTarefa(id, idSeries);
					mDbHelper.close();
					publishProgress(getResources().getString(R.string.msg_completando2));

				} else {
					if (!mDbHelper.isAberto())
						mDbHelper.open();
					mDbHelper.adicionaOperacao(id, Operacao.COMPLETAR,
							idSeries, lista);
					mDbHelper.close();
					publishProgress(getResources().getString(R.string.msg_completando3));
				}
				if (!mDbHelper.isAberto())
					mDbHelper.open();
				mDbHelper.deletaTarefa(id, idSeries);
				mDbHelper.close();
				return null;

			}
			return null;
		}

		protected void onProgressUpdate(String... mensagem) {
			progressDialog.setMessage(mensagem[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			selecionaTarefasPelaLista(listaSelecionada);
			progressDialog.dismiss();
		}

	}

	public class RealizaOperacaoOnline extends AsyncTask<Bundle, String, Void> {
		private ProgressDialog progressDialog;

		protected void onPreExecute() {

			progressDialog = new ProgressDialog(AppLembrar.this);

			progressDialog = ProgressDialog.show(AppLembrar.this, "",
					getResources().getString(R.string.msg_verificando), true);
		}

		@Override
		protected Void doInBackground(Bundle... arg0) {
			String nome, lista, data, tags, prioridade, listaAntiga,
			hora, repeticao, url;
			Long idSeries, id;
			Bundle extras = arg0[0];
			nome = extras.getString(TarefasDbAdapter.CHAVE_NAME);
			lista = extras.getString(TarefasDbAdapter.CHAVE_LISTA);
			listaAntiga = extras.getString("listaAntiga");
			tags = extras.getString(TarefasDbAdapter.CHAVE_TAGS);
			data = TrataData.proRTM(extras
					.getString(TarefasDbAdapter.CHAVE_DATAPREVISTA));			
			hora = extras.getString(TarefasDbAdapter.CHAVE_HORAPREVISTA);
			prioridade = extras.getString(TarefasDbAdapter.CHAVE_PRIO);
			repeticao = extras.getString(TarefasDbAdapter.CHAVE_REPETICAO);
			url = extras.getString(TarefasDbAdapter.CHAVE_URL);
			temInternet();
			switch (extras.getInt("tipoOperacao")) {

			case CREATE_TASK_OPERATION:

				Calendar cd = Calendar.getInstance();
				long iddef = cd.getTimeInMillis() / 1000;
				long idSdef = iddef;
				
				publishProgress(getResources().getString(R.string.msg_adicionando));
				HashMap<String, String> idsTarefaAdicionada = 
					lembrar.adicionaTarefaComSmart(auth_token, nome, lista,
							tags, prioridade, repeticao, data, hora, url);
				if (temInternet && idsTarefaAdicionada
						
				!= null) {
					iddef = new Long(idsTarefaAdicionada.get("id"));
					idSdef = new Long(idsTarefaAdicionada.get("idSeries"));
					lista = idsTarefaAdicionada.get("lista");
					publishProgress(getResources().getString(R.string.msg_adicionando2));

				} else {
					publishProgress("Agendando opera��o...");
					if (!mDbHelper.isAberto())
						mDbHelper.open();

					mDbHelper.adicionaOperacao(iddef, Operacao.ADICIONAR,
							idSdef, lista);
					mDbHelper.close();
					publishProgress(getResources().getString(R.string.msg_adicionando3));

				}
				if (!mDbHelper.isAberto())
					mDbHelper.open();

				mDbHelper.adicionaTarefa(iddef, nome, idSdef, lista, prioridade,
						data, hora, repeticao, url);
				mDbHelper.adicionaTarefaTemporaria(iddef, nome, idSdef, lista,
						prioridade, data, hora, repeticao, url);
				for (String stg : TrataTag.deStringParaArrayList(tags)){
					mDbHelper.adicionaTags(iddef, stg, idSdef);
					mDbHelper.adicionaTagsTemporarias(iddef, stg, idSdef);
				}
				mDbHelper.close();
				break;

			case EDIT_TASK_OPERATION:

				idSeries = extras.getLong(TarefasDbAdapter.CHAVE_IDSERIES);
				id = extras.getLong(TarefasDbAdapter.CHAVE_ID);

				if (Integer.valueOf(extras.getInt("Operacao")).equals(
						Operacao.ADIAR)) {
					publishProgress(getResources().getString(R.string.msg_adiando));
					if (temInternet
							&& lembrar.adiarTarefa(auth_token, id.toString(),
									idSeries.toString(), lista)) {
						publishProgress(getResources().getString(R.string.msg_adiando2));

					} else {
						
						if (!mDbHelper.isAberto())
							mDbHelper.open();

						mDbHelper.adicionaOperacao(id, Operacao.ADIAR,
								idSeries, lista);
						mDbHelper.close();
						publishProgress(getResources().getString(R.string.msg_adiando3));
					}
					if (!mDbHelper.isAberto())
						mDbHelper.open();
								
					GregorianCalendar ontem = (GregorianCalendar) Calendar.getInstance();
					ontem.add(GregorianCalendar.DAY_OF_MONTH, -1);
					GregorianCalendar dtTmp = ontem;				 
					GregorianCalendar dataTarefa = TrataData.trata(data);
					
					
					if(dataTarefa != null && (dataTarefa.after(ontem)))
						dtTmp = dataTarefa;
					
					dtTmp.add(Calendar.DAY_OF_MONTH, 1);
					
					mDbHelper.atualizarTarefa(idSeries, id, nome, TrataData
							.trataGregorian(dtTmp), lista, prioridade, hora, repeticao, url);

					mDbHelper.close();		
					

				} else if (Integer.valueOf(extras.getInt("Operacao")).equals(
						Operacao.COMPLETAR)) {
					publishProgress(getResources().getString(R.string.msg_completando));
					if (temInternet
							&& lembrar.completarTarefa(auth_token, id
									.toString(), idSeries.toString(), lista)) {
						publishProgress(getResources().getString(R.string.msg_completando2));

					} else {
						
						if (!mDbHelper.isAberto())
							mDbHelper.open();
						mDbHelper.adicionaOperacao(id, Operacao.COMPLETAR,
								idSeries, lista);
						mDbHelper.close();
						publishProgress(getResources().getString(R.string.msg_completando3));
					}
					if (!mDbHelper.isAberto())
						mDbHelper.open();
					mDbHelper.deletaTarefa(id, idSeries);
					mDbHelper.close();

				} else if (Integer.valueOf(extras.getInt("Operacao")).equals(
						Operacao.ATUALIZAR)) {

					boolean agendar = false;
					if (temInternet) {
						publishProgress(getResources().getString(R.string.msg_atualizando));
													
						if (!TrataTag.listasDeTagsSaoIguais(tarefaSelecionada.getTags(),
								tags) && !lembrar.inserirTags(auth_token, id
										.toString(), idSeries.toString(),
										lista, tags)){
							
							agendar = true;
						}						
						
						if ((!data.equals(TrataData.trataGregorian(tarefaSelecionada.getDataPrevista()))
								|| !hora.equals(tarefaSelecionada.getHoraPrevista()))
								&& !lembrar.inserirLimite(auth_token, id
										.toString(), idSeries.toString(),
										lista, data, hora, diferencaFuso)) {
							
							agendar = true;
						}						
						
						if (!prioridade.equals(tarefaSelecionada.getPrioridade()) &&
								!lembrar.inserirPrioridade(auth_token, id
								.toString(), idSeries.toString(), lista,
								prioridade)){
							agendar = true;
							
						}						
						
						if (!nome.equals(tarefaSelecionada.getName()) &&
								!lembrar.alterarTexto(auth_token, id.toString(),
								idSeries.toString(), lista, nome)){
							agendar = true;
							
						}
						
						if(!repeticao.equals(tarefaSelecionada.getRepeticao())
								&& !lembrar.adicionarRepeticao(auth_token, id.toString(),
								idSeries.toString(), lista, repeticao)){
							agendar = true;
							
						}
						if(!url.equals(tarefaSelecionada.getUrl())
								&& 	!lembrar.inserirUrl(auth_token, id.toString(),
										idSeries.toString(), lista, url)){
							agendar = true;
						}

						if (!lista.equals(listaAntiga)
								&& !lembrar.MudaDeLista(auth_token, id
										.toString(), idSeries.toString(),
										listaAntiga, lista)){
							agendar = true;
							
						}
					} else{
						agendar = true;
						
					}

					if (agendar) {
						
						if (!mDbHelper.isAberto())
							mDbHelper.open();

						mDbHelper.adicionaOperacao(id, Operacao.ATUALIZAR,
								idSeries, lista);
						publishProgress(getResources().getString(R.string.msg_atualizando3));
						mDbHelper.close();

					} else
						publishProgress(getResources().getString(R.string.msg_atualizando2));

					if (!mDbHelper.isAberto())
						mDbHelper.open();
					mDbHelper.atualizarTarefa(idSeries, id, nome, data, lista,
							prioridade, hora, repeticao, url);
					mDbHelper.adicionaTarefaTemporaria(id, nome, idSeries, lista,
							prioridade, data, hora, repeticao, url);
					if (!TrataTag.listasDeTagsSaoIguais(tarefaSelecionada.getTags(),
							tags)) {
						mDbHelper.deletaTagsPorTarefa(id, idSeries);
						mDbHelper.deletaTagsPorTarefaTemporaria(id, idSeries);
						for (String tgs : TrataTag.deStringParaArrayList(tags)) {
							mDbHelper.adicionaTags(id, tgs, idSeries);
							mDbHelper.adicionaTagsTemporarias(id, tgs, idSeries);
						}
					}

					mDbHelper.close();
				}

				break;

			default:
				break;

			}
			return null;
		}

		protected void onProgressUpdate(String... mensagem) {
			progressDialog.setMessage(mensagem[0]);
		}

		@Override
		protected void onPostExecute(Void result) {

			selecionaTarefasPelaLista(listaSelecionada);
			progressDialog.dismiss();

		}

	}

	private boolean temInternet() {

		ConnectivityManager connMgr = (ConnectivityManager) AppLembrar.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connMgr.getActiveNetworkInfo();
		if (info == null) {
			temInternet = false;
			return false;
		}
		temInternet = true;
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, ADIAR_ID, 0, R.string.menu_adiar);
		menu.add(0, COMPLETAR_ID, 0, R.string.menu_completa);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Bundle extras = new Bundle();
		extras.putLong(TarefasDbAdapter.CHAVE_ID, todasTarefas.get(
				(int) info.id).getId());
		extras.putLong(TarefasDbAdapter.CHAVE_IDSERIES, todasTarefas.get(
				(int) info.id).getIdSeries());
		extras.putString(TarefasDbAdapter.CHAVE_LISTA, todasTarefas.get(
				(int) info.id).getLista());
		extras.putString(TarefasDbAdapter.CHAVE_NAME, todasTarefas.get(
				(int) info.id).getName());
		extras.putString(TarefasDbAdapter.CHAVE_PRIO, todasTarefas.get(
				(int) info.id).getPrioridade());
		extras.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, TrataData
				.trataGregorian(todasTarefas.get((int) info.id)
						.getDataPrevista()));
		extras.putString(TarefasDbAdapter.CHAVE_HORAPREVISTA, todasTarefas.get(
				(int) info.id)
						.getHoraPrevista());
		extras.putString(TarefasDbAdapter.CHAVE_REPETICAO, todasTarefas.get(
				(int) info.id)
						.getRepeticao());

		extras.putInt("tipoOperacao", item.getItemId());
		new RealizaOperacaoContextoOnline().execute(extras, null, null);

		return super.onContextItemSelected(item);
	}
	
	private void mostraTarefasPreSelecionadas(List<Tarefa> tarefas, String par){
		texto_msg.setText(par);
		texto_msg.setLines(1);
		
		setListAdapter(new TarefaAdapter(AppLembrar.this, todasTarefas));

		AppLembrar.this.getListView().setEmptyView(listaVazia);
	}

	private void selecionaTarefasPelaLista(Lista lista) {
		texto_msg.setText("");
		texto_msg.setLines(0);
		if (lista == null) {
			lista = new Lista(IdTarefasHoje, getResources().getString(R.string.str_hoje));

		}

		if (!mDbHelper.isAberto())
			mDbHelper.open();

		if (lista.getId().equals(TodasTarefas_Id))
			mTarefasCursor = mDbHelper.getTodasTarefas("");
		else if (lista.getId().equals(IdTarefasHoje)) {
			final Calendar c = Calendar.getInstance();
			mTarefasCursor = mDbHelper.getTarefasNoIntervalo(TrataData
					.trataGregorian((GregorianCalendar) c), TrataData
					.trataGregorian((GregorianCalendar) c),
					TarefasDbAdapter.CHAVE_PRIO);

		} else if (lista.getId().equals(IdTarefasVencidas)) {
			final Calendar c = Calendar.getInstance();
			c.add(GregorianCalendar.DAY_OF_MONTH, -1);
			mTarefasCursor = mDbHelper.getTarefasNoIntervalo(TrataData
					.trataGregorian(new GregorianCalendar(1900, 0, 1)),
					TrataData.trataGregorian((GregorianCalendar) c),
					TarefasDbAdapter.CHAVE_PRIO);

		} else
			mTarefasCursor = mDbHelper.getTarefasPorLista("", lista.getId()
					.toString());

		todasTarefas = TarefasDbAdapter
				.deCursorParaArrayListTarefas(mTarefasCursor);
		
		Cursor tmp = null,tmp2 = null;
		
		for (Tarefa t : todasTarefas) {
			tmp = mDbHelper.getTagsPorTarefa(t.getId(), t.getIdSeries());
			tmp2 = mDbHelper.getNotasPorTarefa(t.getId());
			t.setTags(TarefasDbAdapter.deCursorParaArrayListTags(tmp));
			
			t.setNotas(TarefasDbAdapter.deCursorParaArrayListNotas(tmp2));
			
			if (tmp != null)
				tmp.close();
			
			if (tmp2 != null)
				tmp2.close();
		}

		Collections.sort(todasTarefas, new ComparaTarefas());
		
		setListAdapter(new TarefaAdapter(AppLembrar.this, todasTarefas));

		AppLembrar.this.getListView().setEmptyView(listaVazia);
		mTarefasCursor.close();
		mDbHelper.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		if(auth_token == null || auth_token.trim().equals("")){
			menu.setGroupVisible(R.id.itens_menu, false);
		}
		else
			menu.setGroupVisible(R.id.itens_menu,true);
		return super.onPrepareOptionsMenu(menu);		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.atualizar:
			
			new RealizaAtualizacao().execute(null, null, null);
			return true;
		case R.id.item_tag:
			if(!mDbHelper.isAberto())
				mDbHelper.open();
			Cursor tmpTag = mDbHelper.getTodasTags();
			String teste[] = new String[tmpTag.getCount()];
			TarefasDbAdapter.deCursorParaArrayListTags(tmpTag).toArray(teste);
			final CharSequence[] items = teste;

			tmpTag.close();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.t_seleciona_tag));
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			       todasTarefas =  mDbHelper.retornaTarefasDeUmaTag((String)items[item]);
			       Collections.sort(todasTarefas, new ComparaTarefas());
			       mostraTarefasPreSelecionadas(todasTarefas,
			    		   getResources().getString(R.string.str_tarefas_tag) 
			    		   + items[item]);
			       
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		case R.id.item_dia:
			showDialog(0);
			return true;
		case R.id.cnf:
			Intent i = new Intent(this, Configuracoes.class);
			Bundle dados = new Bundle();
			dados.putInt(TarefasDbAdapter.CHAVE_CAPA, aparenciaEscolhida);
			dados.putBoolean(TarefasDbAdapter.CHAVE_ATUALIZA_INICIO, sincronizarInicio);
			i.putExtras(dados);
			startActivityForResult(i, CONFIGURE_OPERATION);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
            	if(!mDbHelper.isAberto())
            		mDbHelper.open();
            	
               todasTarefas = mDbHelper.retornaTarefasNoIntervalo(year + "-" + 
            		   ((monthOfYear+1) >= 10?String.valueOf(monthOfYear+1) : "0" + String.valueOf(monthOfYear+1))
            		   + "-" + (dayOfMonth >= 10?String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth)),
            		   year + "-" + 
            		   ((monthOfYear+1) >= 10?String.valueOf(monthOfYear+1) : "0" + String.valueOf(monthOfYear+1))
            		   + "-" + (dayOfMonth >= 10?String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth))
            		   , TarefasDbAdapter.CHAVE_PRIO);
              
               mDbHelper.close();
               Collections.sort(todasTarefas, new ComparaTarefas());
               mostraTarefasPreSelecionadas(todasTarefas, 
            		   getResources().getString(R.string.str_tarefas_em)
            		   + (dayOfMonth >= 10?String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth))
            		   + "/"
            		   + ((monthOfYear+1) >= 10?String.valueOf(monthOfYear+1) : "0" + String.valueOf(monthOfYear+1))
            		   + "/" + year);
                
            }
        };
	
        protected Dialog onCreateDialog(int id) {
            switch (id) {
            case 0:
            	Calendar g = Calendar.getInstance();
                return new DatePickerDialog(this,
                            mDateSetListener, g.get(Calendar.YEAR),
                            g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH));
            }
            return null;
        }
        
	public class RealizaAtualizacao extends AsyncTask<Void, String, Void> {
		private ProgressDialog progressDialog;

		protected void onPreExecute() {

			progressDialog = new ProgressDialog(AppLembrar.this);

			progressDialog = ProgressDialog.show(AppLembrar.this, "",
					getResources().getString(R.string.msg_verificando), true);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (!temInternet())
				publishProgress(getResources().getString(R.string.msg_n_atualizou));
			else {
				publishProgress(getResources().getString(R.string.msg_sincronizando));
				Operacao.executaOperacoesAgendadas(mDbHelper, auth_token, diferencaFuso);
				publishProgress(getResources().getString(R.string.msg_buscando));
				BuscaTarefasNaInternet();
				publishProgress(getResources().getString(R.string.msg_armazenando));
				insereNovasTarefasNoBanco();
			}

			return null;
		}

		protected void onProgressUpdate(String... mensagem) {
			progressDialog.setMessage(mensagem[0]);
		}

		@Override
		protected void onPostExecute(Void result) {

			
			selecionaTarefasPelaLista(listaSelecionada);
			todasListas = null;
			
			buscaTodasAsListas(listaSelecionada);
			progressDialog.dismiss();

		}

	}

}