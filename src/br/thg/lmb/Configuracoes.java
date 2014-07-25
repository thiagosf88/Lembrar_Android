package br.thg.lmb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import br.thg.lmb.dao.TarefasDbAdapter;

public class Configuracoes extends Activity {
	
	ToggleButton sincronizaInicio;
	TextView escolheCapa;
	TextView apagaDados;
	int capaAntiga;
	int capa;
	boolean sincInicioAntigo;
	boolean sincInicio;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tela_config);
		setTitle(R.string.m_cnf);
		
		sincronizaInicio = (ToggleButton) findViewById(R.id.tb_sincroniza_inicio);
		escolheCapa = (TextView) findViewById(R.id.tv_escolha_capa);
		apagaDados = (TextView) findViewById(R.id.tv_sair_lembrar);
		
		Bundle extras = getIntent().getExtras();
		capaAntiga = extras.getInt(TarefasDbAdapter.CHAVE_CAPA);
		capa = capaAntiga;
		sincInicioAntigo = extras.getBoolean(TarefasDbAdapter.CHAVE_ATUALIZA_INICIO);
		sincronizaInicio.setChecked(sincInicioAntigo);
		
		sincronizaInicio.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        
		        if (sincronizaInicio.isChecked()) {
		            sincInicio = true;
		        } else {
		            sincInicio = false;
		        }
		        
		        if(sincInicio != sincInicioAntigo){
					TarefasDbAdapter mDbHelper = new TarefasDbAdapter(Configuracoes.this);
					mDbHelper.open();
					
					mDbHelper.atualizarSincronizacao(sincInicio);
											
					mDbHelper.close();
			    	}
		    }
		});
		
		
		escolheCapa.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				CharSequence [] capas = {
						getResources().getString(R.string.op_capa_1),
						getResources().getString(R.string.op_capa_2),
						getResources().getString(R.string.op_capa_3),
						getResources().getString(R.string.op_capa_4)
						};
				
				

				AlertDialog.Builder builder = new AlertDialog.Builder(Configuracoes.this);
				AlertDialog alert;
				
				builder.setTitle(getResources().getString(R.string.t_notas));
				builder.setItems(capas, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	capa = item;
				    	if(capa != capaAntiga){
						TarefasDbAdapter mDbHelper = new TarefasDbAdapter(Configuracoes.this);
						mDbHelper.open();
						
						mDbHelper.atualizarCapa(capa);
												
						mDbHelper.close();
				    	}
				    }
				});
				alert = builder.create();
				alert.show();
			}
		});
		
		apagaDados.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				
				AlertDialog.Builder dialogo = new
				AlertDialog.Builder(Configuracoes.this);
				dialogo.setTitle("");
				dialogo.setIcon(android.R.drawable.ic_dialog_alert);
				dialogo.setMessage(getResources().getString(R.string.msg_confirmar_exclusao_total));
				dialogo.setPositiveButton(R.string.b_sim, new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	TarefasDbAdapter mDbHelper = new TarefasDbAdapter(Configuracoes.this);
						mDbHelper.open();
						
						mDbHelper.apagaTudo();
												
						mDbHelper.close();
						Toast
						.makeText(
								Configuracoes.this,
								getResources().getString(R.string.msg_sem_dados),
								Toast.LENGTH_LONG).show();
						Bundle bundle = new Bundle();
						
						bundle.putInt("FINALIZAR", -7);
						Intent mIntent = new Intent();
						mIntent.putExtras(bundle);
						setResult(RESULT_OK, mIntent);	
						finish();
		                    
		            }

		        });
		        dialogo.setNegativeButton(R.string.b_nao, null);
				dialogo.show();
				
				
			}
			});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	Bundle bundle = new Bundle();
			
			bundle.putInt(TarefasDbAdapter.CHAVE_CAPA, capa);
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);		
			finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	
}
