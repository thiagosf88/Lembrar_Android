package br.thg.lmb.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.thg.rlmb.R;
import br.thg.lmb.dominio.Nota;

public class NotaAdapter extends BaseAdapter {
	private Context context;
	private List<Nota> notas;

	public NotaAdapter(Context context, List<Nota> notas) {
		this.context = context;
		this.notas = notas;
	}

	@Override
	public int getCount() {
		if (notas != null)
			return notas.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return notas.get(position);
	}

	@Override
	/**
	 * retorna o id mesmo n�o a posic�
	 */
	public long getItemId(int position) {
		return notas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Nota nt = notas.get(position);

		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.linha_nota, null);
		TextView titulo = (TextView)view.findViewById(R.id.nota_titulo);
		TextView nota = (TextView)view.findViewById(R.id.nota_texto);
		
		if(nt.getTitulo() != null && !nt.getTitulo().trim().equals(""))
			titulo.setText(nt.getTitulo());
		
		nota.setText(nt.getNota());
		

		return view;
	}
	
	

}