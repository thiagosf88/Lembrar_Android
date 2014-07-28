package br.thg.lmb.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.thg.rlmb.R;
import br.thg.lmb.dominio.Tarefa;

public class TarefaAdapter extends BaseAdapter {
    private Context context;
    private List<Tarefa> tarefaLista;
   
    
    public TarefaAdapter(Context context, List<Tarefa> tarefaLista){
        this.context = context;
        this.tarefaLista = tarefaLista;
    }
    
    @Override
    public int getCount() {
    	if(tarefaLista != null)
        return tarefaLista.size();
    	return 0;
    }
 
    @Override
    public Object getItem(int position) {
        return tarefaLista.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Recupera o estado da posi��o atual
        Tarefa tf = tarefaLista.get(position);
        
        // Cria uma inst�ncia do layout XML para os objetos correspondentes
        // na View
        LayoutInflater inflater = (LayoutInflater)
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.linha_tarefa, null);
        
        // Texto
        TextView text = (TextView)view.findViewById(R.id.texto);
        ImageView prio = (ImageView) view.findViewById(R.id.img_prio);
        text.setText(tf.getName());
        if(tf.getPrioridade() != null){
        	if(tf.getPrioridade().equals("1"))
        		prio.setBackgroundResource(R.drawable.l);
        	else if(tf.getPrioridade().equals("2"))
        		prio.setBackgroundResource(R.drawable.ae);
        	else if(tf.getPrioridade().equals("3"))
        		prio.setBackgroundResource(R.drawable.ac);
        	else
        		prio.setImageResource(R.drawable.b);
        }
        
        // Data
        if(tf.getDataPrevista() != null){
       TextView textData = (TextView)view.findViewById(R.id.data_linha);
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        textData.setText(formato.format(tf.getDataPrevista().getTime()));
        final Calendar c = Calendar.getInstance();
        
        if(TrataData.trataGregorian(tf.getDataPrevista())
        		.equals(TrataData.trataGregorian((GregorianCalendar)c)))
        text.setTypeface(text.getTypeface(),Typeface.BOLD);
        else if (tf.getDataPrevista()
        		.before(c)){
        	text.setTypeface(text.getTypeface(),Typeface.BOLD);
        	text.setTextColor(Color.RED);
        }
        }
        
        
        if(tf.getTags() != null && tf.getTags().size() > 0){
        	TextView textTags = (TextView) view.findViewById(R.id.tags_linha);
        	String tmp = "#";
        	for(String tg : tf.getTags()){
        		tmp += tg + ",";
        	}
        	
        	textTags.setText(tmp.substring(0, tmp.length() - 1));
        }
        
        
 
        return view;
    }

    
}