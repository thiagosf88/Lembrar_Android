package br.thg.rlmb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import br.thg.lmb.dao.TarefasDbAdapter;
import br.thg.lmb.dominio.Lista;
import br.thg.lmb.util.ListaAdapter;
import br.thg.lmb.util.TokenizerEspaco;
import br.thg.rlmb.R;

public class TarefaCriar extends Activity {

	private EditText mNameText;
	private EditText mTagsText;
	private EditText mUrlText;
	private MultiAutoCompleteTextView mRepeticao;
	private ArrayList<Lista> todasListas;
	private Spinner spListas;
	private Button bt_salvar;
	private Button bt_completar;
	private Button bt_prioridade;
	private Button bt_adiar;
	private Button bt_limpar_data;
	private Button bt_limpar_hora;
	private TextView data_texto;
	private TextView hora_texto;
	
	private int mAno;
	private int mMes;
	private int mDia;
	private int mHora;
	private int mMinuto;
	private int prio = 0;
	private HashMap<Integer, Integer> cores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		cores = new HashMap<Integer, Integer>();
		cores.put(1, Color.rgb(255, 69, 0));
		cores.put(2, Color.rgb(25, 25, 112));
		cores.put(3, Color.rgb(70, 130, 180));
		final Calendar c = Calendar.getInstance();
		mAno = c.get(Calendar.YEAR);
		mMes = c.get(Calendar.MONTH);
		mDia = c.get(Calendar.DAY_OF_MONTH);
		mHora = 9;
		mMinuto = 0;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tarefa_editar);
		setTitle(R.string.tarefa_criar);

		mNameText = (EditText) findViewById(R.id.nome);
		mTagsText = (EditText) findViewById(R.id.ed_tags_tarefa);
		mUrlText = (EditText) findViewById(R.id.ed_text_url);
		mRepeticao = (MultiAutoCompleteTextView) findViewById(R.id.auto_compl_repeticao);
		TokenizerEspaco ct = new TokenizerEspaco();
		mRepeticao.setThreshold(1);
		mRepeticao.setTokenizer(ct);
		data_texto = (TextView) findViewById(R.id.ed_text_data);
		data_texto.setText(getResources().getString(R.string.t_sem_data));
		hora_texto = (TextView) findViewById(R.id.ed_text_hora);
		hora_texto.setText(getResources().getString(R.string.t_sem_hora));
		bt_salvar = (Button) findViewById(R.id.bt_salvar);
		bt_completar = (Button) findViewById(R.id.bt_completar_tarefa);
		bt_completar.setVisibility(View.INVISIBLE);
		bt_adiar = (Button) findViewById(R.id.bt_adiar_tarefa);
		bt_adiar.setVisibility(View.INVISIBLE);
		bt_prioridade = (Button) findViewById(R.id.bt_prioridade);
		bt_limpar_data = (Button) findViewById(R.id.bt_limpar_data);
		bt_limpar_data.setVisibility(View.INVISIBLE);
		bt_limpar_hora = (Button) findViewById(R.id.bt_limpar_hora);
		bt_limpar_hora.setVisibility(View.INVISIBLE);
		
		String[] termos = getResources().getStringArray(R.array.repeticao_termos);
		ArrayAdapter<String> termosRepeticao = new ArrayAdapter<String>(this, R.layout.linha_termo, termos);
		
		mRepeticao.setAdapter(termosRepeticao);
		
		// todasListas = new Lembrar().getListas();
		if (todasListas == null) {

			TarefasDbAdapter mDbHelper = new TarefasDbAdapter(this);
			mDbHelper.open();
			Cursor tmpLista = mDbHelper.getListasExceto
			("Sent' AND "+  TarefasDbAdapter.CHAVE_NOME_LISTA + "<> 'Enviadas' AND "
			 + TarefasDbAdapter.CHAVE_ID + "> '" + AppLembrar.IdTarefasVencidas);

			todasListas = TarefasDbAdapter
					.deCursorParaArrayListListas(tmpLista);
			tmpLista.close();
			mDbHelper.close();

		}
		
		
		spListas = (Spinner) findViewById(R.id.edit_lista);
		spListas.setAdapter(new ListaAdapter(this, todasListas));

		bt_salvar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Bundle bundle = new Bundle();

				if (mNameText.getText().toString().trim().equals("")) {
					Toast.makeText(TarefaCriar.this,
							getResources().getString(R.string.msg_s_descricao),
							Toast.LENGTH_LONG).show();
					mNameText.requestFocus();
					return;
				}

				bundle.putBoolean("completar", false);

				bundle.putString(TarefasDbAdapter.CHAVE_NAME, mNameText
						.getText().toString());

				bundle
						.putString(TarefasDbAdapter.CHAVE_LISTA,
								((Lista) spListas.getSelectedItem()).getId()
										.toString());

				String dt = data_texto.getText().toString();
				if (dt.equals(getResources().getString(R.string.t_sem_data))) {
					bundle.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, null);
				} else
					bundle.putString(TarefasDbAdapter.CHAVE_DATAPREVISTA, dt);

				if (hora_texto.getText().toString()
						.equalsIgnoreCase(getResources().getString(R.string.t_sem_hora))) {
					bundle.putString(TarefasDbAdapter.CHAVE_HORAPREVISTA, null);
				} else
					bundle.putString(TarefasDbAdapter.CHAVE_HORAPREVISTA,
							hora_texto.getText().toString());

				bundle.putString(TarefasDbAdapter.CHAVE_TAGS, mTagsText
						.getText().toString());

				if (prio > 0)
					bundle.putString(TarefasDbAdapter.CHAVE_PRIO, String
							.valueOf(prio));
				else
					bundle.putString(TarefasDbAdapter.CHAVE_PRIO, "N");
				
				bundle.putString(TarefasDbAdapter.CHAVE_REPETICAO, mRepeticao.getText().toString());

				bundle.putString(TarefasDbAdapter.CHAVE_URL, mUrlText.getText().toString());
				
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
				if (++prio <= 3) {
					bt_prioridade.setText(String.valueOf(prio));
					bt_prioridade.setTextColor(cores.get(prio));
				} else {
					bt_prioridade.setText("-");
					bt_prioridade.setTextColor(Color.BLACK);
					prio = 0;
				}
			}
		});

	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mAno = year;
			mMes = monthOfYear;
			mDia = dayOfMonth;
			atualizaDataMostrada();
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

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
			return new DatePickerDialog(this, mDateSetListener, mAno, mMes,
					mDia);
		case 1:
			return new TimePickerDialog(this, mTimeSetListener, mHora, mMinuto,
					true);
		}
		return null;
	}

	private void atualizaDataMostrada() {
		String mes, dia;
		dia = mDia >= 10 ? String.valueOf(mDia) : "0" + String.valueOf(mDia);
		mes = mMes >= 9 ? String.valueOf(mMes+1) : "0"
				+ String.valueOf(mMes + 1);

		data_texto.setText(new StringBuilder()

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
}
