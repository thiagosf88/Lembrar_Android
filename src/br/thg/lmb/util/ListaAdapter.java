package br.thg.lmb.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.thg.lmb.R;
import br.thg.lmb.dominio.Lista;

public class ListaAdapter extends BaseAdapter {
	private Context context;
	private List<Lista> listaLista;

	public ListaAdapter(Context context, List<Lista> listaLista) {
		this.context = context;
		this.listaLista = listaLista;
	}

	@Override
	public int getCount() {
		if (listaLista != null)
			return listaLista.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return listaLista.get(position);
	}

	@Override
	/**
	 * retorna o id mesmo não a posicõ
	 */
	public long getItemId(int position) {
		return listaLista.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Lista tf = listaLista.get(position);

		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.linha_lista, null);
		TextView nome = (TextView)view.findViewById(R.id.nome_lista);
		nome.setText(tf.getNome());

		return view;
	}
	
	

}