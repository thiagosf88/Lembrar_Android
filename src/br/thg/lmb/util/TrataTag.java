package br.thg.lmb.util;

import java.util.ArrayList;
import java.util.List;

public class TrataTag {

	public static String deArrayListParaStringRTM(List<String> tags){
		StringBuffer tmp = new StringBuffer();
		if(tags != null && tags.size() > 0){
		for(String s : tags){
			tmp.append(s).append(" ");
		}
		
		return tmp.substring(0, tmp.length() - 1);
		}
		return "";
	}
	
	public static ArrayList<String> deStringParaArrayList(String tags){
		ArrayList<String> tagsSeparadas = new ArrayList<String>();
		//Retirar todos os espaços duplos da string. E caso haja retira o
		//espaço no início da string
		tags = tags.replaceAll("\\s+", " ");
		tags = tags.replaceAll("^\\s+", "");
		
		StringBuffer sb = new StringBuffer(trataCaracteres(tags));
		int inicio = 0, fim = sb.indexOf(" ");
		while(sb.length() > 0){
			if(fim == -1)
				fim = sb.length();
			tagsSeparadas.add(sb.substring(inicio, fim));
			sb.replace(inicio, fim + 1, "");
			fim = sb.indexOf(" ");
		}
		return tagsSeparadas;
	}
		
	public static String trataCaracteres(String tagsCruas){		
		
		tagsCruas = tagsCruas.replace(",", " ");
		
		return tagsCruas;
	}
	
	public static boolean listasDeTagsSaoIguais(List<String> tagsEmArray, String tagsEmString){
		ArrayList<String> gerada = deStringParaArrayList(tagsEmString);
		
		if((gerada == null && tagsEmArray != null)
				|| (gerada != null && tagsEmArray == null)
				|| (gerada.size() != tagsEmArray.size()))
			return false;
		
		if (gerada == null && tagsEmArray == null)
			return true;
		
		for(int i = 0; i < gerada.size();i++){
			if(!gerada.get(i).equals(tagsEmArray.get(i))){
				return false;
			}
		}
		return true;
	}
	

	
}
