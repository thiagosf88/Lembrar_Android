package br.thg.lmb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import br.thg.lmb.AppLembrar.RealizaOperacaoOnline;
import br.thg.lmb.dao.TarefasDbAdapter;
import br.thg.lmb.dominio.Lista;
import br.thg.lmb.dominio.Nota;
import br.thg.lmb.dominio.Operacao;
import br.thg.lmb.dominio.Tarefa;
import br.thg.lmb.util.ListaAdapter;
import br.thg.lmb.util.NotaAdapter;
import br.thg.lmb.util.TokenizerEspaco;
import br.thg.lmb.util.TrataData;
import br.thg.lmb.util.WebViewActivity;
import br.thg.rlmb.R;

public class TarefaEdit extends Activity {

	private static final int VIEW_USER_URL = 0;
	private EditText mNameText;
	private EditText mTagsText;
	private EditText mUrlText;
	private MultiAutoCompleteTextView mRepeticao;
	private Tarefa tarefa = new Tarefa();
	private ArrayList<Lista> todasListas;
	private Spinner spListas;
	private Button bt_salvar;
	private Button bt_completar;
	private Button bt_adiar;
	private Button bt_prioridade;
	private Button bt_limpar_data;
	private Button bt_limpar_hora;
	private TextView data_texto;
	private TextView link_texto;
	private TextView hora_texto;
	private TextView nota_texto;
	private int mAno;
    private int mMes;
    private int mDia;
    private int mHora;
    private int mMinuto;
    private int prio = 0;
    private HashMap<Integer, Integer> cores;
    private HashMap<Long, Integer> listaPosicoes;
    private ArrayList<Nota> notas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		cores = new HashMap<Integer, Integer>();
		cores.put(1, Color.rgb(255, 69, 0));
		cores.put(2, Color.rgb(25, 25, 112));
		cores.put(3, Color.rgb(70, 130, 180));
listaPosicoes = new HashMap<Long, Integer>();
		final Calendar c = Calendar.getInstance();
        mAno = c.get(Calendar.YEAR);
        mMes = c.get(Calendar.MONTH);
        mDia = c.get(Calendar.DAY_OF_MONTH);
        mHora = 9;
        mMinuto = 0;
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tarefa_editar);
		setTitle(R.string.tarefa_editar);

		mNameText = (EditText) findViewById(R.id.nome);
		mTagsText = (EditText) findViewById(R.id.ed_tags_tarefa);
		mUrlText = (EditText) findViewById(R.id.ed_text_url);
		mRepeticao = (MultiAutoCompleteTextView) findViewById(R.id.auto_compl_repeticao);
		TokenizerEspaco ct = new TokenizerEspaco();
		mRepeticao.setThreshold(1);
		mRepeticao.setTokenizer(ct);
		
		data_texto = (TextView) findViewById(R.id.ed_text_data);
		hora_texto = (TextView) findViewById(R.id.ed_text_hora);
		link_texto = (TextView) findViewById(R.id.ed_link_texto);
		nota_texto = (TextView) findViewById(R.id.ed_notas_texto);
		
		
		bt_salvar = (Button) findViewById(R.id.bt_salvar);
		bt_completar = (Button) findViewById(R.id.bt_completar_tarefa);
		bt_adiar = (Button) findViewById(R.id.bt_adiar_tarefa);
		bt_prioridade = (Button) findViewById(R.id.bt_prioridade);
		bt_limpar_data = (Button) findViewById(R.id.bt_limpar_data);
		bt_limpar_hora  = (Button) findViewById(R.id.bt_limpar_hora);
		String[] termos = getResources().getStringArray(R.array.repeticao_termos);
		ArrayAdapter<String> termosRepeticao = new ArrayAdapter<String>(this, R.layout.linha_termo, termos);
		
		mRepeticao.setAdapter(termosRepeticao);
			TarefasDbAdapter mDbHelper = new TarefasDbAdapter(this);
			mDbHelper.open();
			Cursor tmpLista =
				mDbHelper.getListasExceto("Sent' AND "+  TarefasDbAdapter.CHAVE_NOME_LISTA + "<> 'Enviadas' AND "
						 + TarefasDbAdapter.CHAVE_ID + "> '" + AppLembrar.IdTarefasVencidas);
			todasListas = TarefasDbAdapter.deCursorParaArrayListListas(tmpLista);
			tmpLista.close();
			
			mDbHelper.close();
			if(todasListas == null)
				return;
			for(int o = 0; o < todasListas.size();o++){
				listaPosicoes.put(todasListas.get(o).getId(), o);
			}
		
		spListas = (Spinner) findViewById(R.id.edit_lista);
		spListas.setAdapter(new ListaAdapter(this, todasListas));
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Long id = extras.getLong(TarefasDbAdapter.CHAVE_ID);
			tarefa.setId(id);
			String nome = extras.getString(TarefasDbAdapter.CHAVE_NAME);
			tarefa.setName(nome);
			Long idSeries = extras.getLong(TarefasDbAdapter.CHAVE_IDSERIES);
			tarefa.setIdSeries(idSeries);
			tarefa.setLista(extras.getString(TarefasDbAdapter.CHAVE_LISTA));
			tarefa.setTags(extras.getStringArrayList(TarefasDbAdapter.CHAVE_TAGS));
			tarefa.setDataPrevista(TrataData.trata(extras.getString(TarefasDbAdapter.CHAVE_DATAPREVISTA)));
			tarefa.setHoraPrevista(extras.getString(TarefasDbAdapter.CHAVE_HORAPREVISTA));
			tarefa.setPrioridade(extras.getString(TarefasDbAdapter.CHAVE_PRIO));
			tarefa.setRepeticao(extras.getString(TarefasDbAdapter.CHAVE_REPETICAO));
			tarefa.setUrl(extras.getString(TarefasDbAdapter.CHAVE_URL));
			
			
			mDbHelper.open();
			
			notas = TarefasDbAdapter.deCursorParaArrayListNotas(
					mDbHelper.getNotasPorTarefa(tarefa.getId()));
			
			mDbHelper.close();
			
			
			
			
			if (nome != null) {
				mNameText.setText(nome);
			}

			if (tarefa.getLista() != null) {
				
				spListas.setSelection(listaPosicoes.get(Long.parseLong(tarefa.getLista())));
				
			}
			if (tarefa.getTags() != null && tarefa.getTags().size() > 0){
				String tmp = "";
				for(String t : tarefa.getTags()){
					tmp += t + " ";
				}
				mTagsText.setText(tmp.substring(0, tmp.length() -1));
			}
			if(tarefa.getDataPrevista() != null){
				mAno = tarefa.getDataPrevista().get(GregorianCalendar.YEAR);
				mMes = tarefa.getDataPrevista().get(GregorianCalendar.MONTH);
				mDia = tarefa.getDataPrevista().get(GregorianCalendar.DAY_OF_MONTH);
				atualizaDataMostrada();
			}else{
				data_texto.setText(getResources().getString(R.string.t_sem_data));
				bt_limpar_data.setVisibility(View.INVISIBLE);
			}
			
			if(tarefa.getHoraPrevista() != null && 
					!tarefa.getHoraPrevista().equals("00:00")
					&& tarefa.getHoraPrevista().trim().length() == 5){
				
				mHora = Integer.parseInt(tarefa.getHoraPrevista().substring(0, 2));
				
				if(tarefa.getHoraPrevista().substring(3,4).trim().equals("0"))
					mMinuto = Integer.parseInt(tarefa.getHoraPrevista().substring(4,5));
				else
					mMinuto = Integer.parseInt(tarefa.getHoraPrevista().trim().substring(3));
				atualizaHoraMostrada();
			}
			else{
				hora_texto.setText(getResources().getString(R.string.t_sem_hora));
				bt_limpar_hora.setVisibility(View.INVISIBLE);
			}
			
			if(tarefa.getPrioridade() != null){
				if(tarefa.getPrioridade().equals("N")){
					bt_prioridade.setText("-");
					bt_prioridade.setTextColor(Color.BLACK);
					prio = 0;
				}
				else{
					prio = Integer.parseInt(tarefa.getPrioridade());
					bt_prioridade.setText(tarefa.getPrioridade());
					bt_prioridade.setTextColor(cores.get(Integer.parseInt(tarefa.getPrioridade())));
				}
			}
			
			if(tarefa.getRepeticao()!= null)
				mRepeticao.setText(tarefa.getRepeticao());
			
			if(tarefa.getUrl() != null){
				mUrlText.setText(tarefa.getUrl());
				if(!tarefa.getUrl().trim().equals("")){
				link_texto.setTypeface(Typeface.DEFAULT,Typeface.BOLD_ITALIC);
				link_texto.setTextColor(Color.BLUE);
				}
			}
			
			if(notas != null && notas.size() > 0){
				nota_texto.setVisibility(View.VISIBLE);
			}
	        

		}

		bt_salvar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				
				if(mNameText
						.getText().toString().trim().equals("")){
					Toast.makeText(TarefaEdit.this,getResources().getString(R.string.msg_s_descricao),Toast.LENGTH_LONG).show();
					mNameText.requestFocus();
					return;
				}
				
				Bundle bundle = new Bundle();
				
				bundle.putInt("Operacao", Operacao.ATUALIZAR);
				
				
				bundle.putLong(TarefasDbAdapter.CHAVE_ID, tarefa.getId());
				bundle.putLong(TarefasDbAdapter.CHAVE_IDSERIES, tarefa
						.getIdSeries());
				bundle.putString(TarefasDbAdapter.CHAVE_NAME, mNameText
						.getText().toString());
					
				
				bundle.putString(TarefasDbAdapter.CHAVE_LISTA, String.valueOf(spListas.getSelectedItemId()));
				bundle.putString("listaAntiga", tarefa.getLista());
				String dt = data_texto.getText().toString();
				if(dt.equalsIgnoreCase(getResources().getString(R.string.t_sem_data))){
					bundle.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, "");
				}
				else
					bundle.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, dt);
				
				if(hora_texto.getText().toString().equalsIgnoreCase(getResources().getString(R.string.t_sem_hora))){
					bundle.putString(TarefasDbAdapter.CHAVE_HORAPREVISTA, "");
				}
				else
					bundle.putString(TarefasDbAdapter.CHAVE_HORAPREVISTA,
							hora_texto.getText().toString());
				
				bundle.putString(TarefasDbAdapter.CHAVE_TAGS, mTagsText.getText().toString());
				if(prio > 0)
				bundle.putString(TarefasDbAdapter.CHAVE_PRIO, String.valueOf(prio));
				else
					bundle.putString(TarefasDbAdapter.CHAVE_PRIO,"N");
				
				bundle.putString(TarefasDbAdapter.CHAVE_REPETICAO, mRepeticao.getText().toString());
				
				bundle.putString(TarefasDbAdapter.CHAVE_URL, mUrlText.getText().toString());
				
				
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();

			}

		});
		bt_completar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Bundle bundle = new Bundle();
				bundle.putInt("Operacao", Operacao.COMPLETAR);
				bundle.putString(TarefasDbAdapter.CHAVE_NAME, mNameText
						.getText().toString());
				bundle.putLong(TarefasDbAdapter.CHAVE_ID, tarefa.getId());
				
				bundle.putString(TarefasDbAdapter.CHAVE_TAGS, mTagsText.getText().toString());

				bundle.putLong(TarefasDbAdapter.CHAVE_IDSERIES, tarefa
						.getIdSeries());
				bundle.putString(TarefasDbAdapter.CHAVE_LISTA, tarefa
						.getLista());
				
				if(prio > 0)
					bundle.putString(TarefasDbAdapter.CHAVE_PRIO, String.valueOf(prio));
					else
						bundle.putString(TarefasDbAdapter.CHAVE_PRIO,"N");

				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();

			}

		});
		
		bt_adiar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Bundle bundle = new Bundle();
				bundle.putInt("Operacao", Operacao.ADIAR);
				bundle.putString(TarefasDbAdapter.CHAVE_NAME, mNameText
						.getText().toString());
				bundle.putLong(TarefasDbAdapter.CHAVE_ID, tarefa.getId());
				
				bundle.putString(TarefasDbAdapter.CHAVE_TAGS, mTagsText.getText().toString());

				String dt = data_texto.getText().toString();
				if(dt.equals(getResources().getString(R.string.t_sem_data))){
					bundle.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, null);
				}
				else
					bundle.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, dt);
				
				bundle.putLong(TarefasDbAdapter.CHAVE_IDSERIES, tarefa
						.getIdSeries());
				bundle.putString(TarefasDbAdapter.CHAVE_LISTA, tarefa
						.getLista());

				if(prio > 0)
					bundle.putString(TarefasDbAdapter.CHAVE_PRIO, String.valueOf(prio));
					else
						bundle.putString(TarefasDbAdapter.CHAVE_PRIO,"N");
				
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();

			}

		});
		
		data_texto.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				 showDialog(0);
			}
		});
		
		hora_texto.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				 showDialog(1);
			}
		});
		
		bt_limpar_data.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				data_texto.setText(getResources().getString(R.string.t_sem_data));
				bt_limpar_data.setVisibility(View.INVISIBLE);
			}
		});
		
		bt_limpar_hora.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				hora_texto.setText(getResources().getString(R.string.t_sem_hora));
				bt_limpar_hora.setVisibility(View.INVISIBLE);
			}
		});
		
		bt_prioridade.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if(++prio <=3 ){
					bt_prioridade.setText(String.valueOf(prio));
					bt_prioridade.setTextColor(cores.get(prio));
				}
					else
					{
						bt_prioridade.setText("-");
						bt_prioridade.setTextColor(Color.BLACK);
						prio = 0;
					}
				
			}
		});
		
		link_texto.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String url = mUrlText.getText().toString();
				if (!url.trim().equals("")) {
					
					if (!url.contains("://")) {
						url = "http://" + url;
					}
					/*Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW);
					i.addCategory(Intent.CATEGORY_BROWSABLE);
					try{
					i.setData(Uri.parse(url));
					startActivity(i);
					}catch(Exception e){
						link_texto.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
					}*/
					Intent webSite = new Intent(TarefaEdit.this, WebViewActivity.class);
					webSite.setAction(Intent.ACTION_VIEW);
					webSite.addCategory(Intent.CATEGORY_BROWSABLE);
					webSite.putExtra(WebViewActivity.URL, url);
					startActivityForResult(webSite, VIEW_USER_URL);
				} else {
					link_texto.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
				}
			}
		});
		
		nota_texto.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				
				NotaAdapter nts = new NotaAdapter(TarefaEdit.this, notas);
				

				AlertDialog.Builder builder = new AlertDialog.Builder(TarefaEdit.this);
				AlertDialog alert;
				
				builder.setTitle(getResources().getString(R.string.t_notas));
				builder.setAdapter(nts, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	
				    }
				});
				alert = builder.create();
				alert.show();
			}
		});
          
		
	}
	private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mAno = year;
                mMes = monthOfYear;
                mDia = dayOfMonth;
                atualizaDataMostrada();
            }
        };
        
    	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					mHora = hourOfDay;
					mMinuto = minute;
					atualizaHoraMostrada();
					
				}
            };
	
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case 0:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mAno, mMes, mDia);
        case 1:
        	return new TimePickerDialog(this,
        			mTimeSetListener, 
        			mHora, mMinuto, true);
        }
        return null;
    }
	
    private void atualizaDataMostrada() {
    	String mes, dia;
    	dia = mDia >= 10 ?String.valueOf(mDia) : "0" + String.valueOf(mDia);
    	mes = mMes >= 9 ?String.valueOf(mMes + 1) : "0" + String.valueOf(mMes + 1);
    	
        data_texto.setText(
            new StringBuilder()
            
            		.append(dia).append("/")                    
            
                    .append(mes).append("/")
                    
                    .append(mAno).append(" "));
        
        
		bt_limpar_data.setVisibility(View.VISIBLE);
    }
    
    private void atualizaHoraMostrada(){
    	String hora, minuto;
    	hora = mHora >= 10?String.valueOf(mHora) : "0" + String.valueOf(mHora);
    	minuto = mMinuto >= 10 ? String.valueOf(mMinuto) : "0" + String.valueOf(mMinuto);
    	
    	hora_texto.setText(new StringBuilder()
            
            		.append(hora).append(":")                    
            
                    .append(minuto).append(" ")
                  );
    	bt_limpar_hora.setVisibility(View.VISIBLE);
    }

    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (intent == null)
			return;


		
		switch (requestCode) {
		
		case VIEW_USER_URL:
			if(resultCode == TarefaEdit.RESULT_OK){
				
			}
			if(resultCode == TarefaEdit.RESULT_CANCELED){
				link_texto.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
			}
			break;
		default:
			
			break;
		}

	}
}
